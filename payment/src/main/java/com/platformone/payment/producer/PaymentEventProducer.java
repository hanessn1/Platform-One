package com.platformone.payment.producer;

import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.events.*;
import com.platformone.payment.exception.InsufficientBalanceException;
import com.platformone.payment.service.PaymentService;
import com.platformone.payment.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);
    private final PaymentService paymentService;
    private final WalletService walletService;
    private final KafkaTemplate<String, PaymentSucceededEvent> paymentSucceededKafkaTemplate;
    private final KafkaTemplate<String, PaymentFailedEvent> paymentFailedKafkaTemplate;
    private final KafkaTemplate<String, PaymentRefundedEvent> paymentRefundedKafkaTemplate;

    public PaymentEventProducer(
            PaymentService paymentService,
            WalletService walletService,
            KafkaTemplate<String, PaymentSucceededEvent> paymentSucceededKafkaTemplate,
            KafkaTemplate<String, PaymentFailedEvent> paymentFailedKafkaTemplate,
            KafkaTemplate<String, PaymentRefundedEvent> paymentRefundedKafkaTemplate
    ) {
        this.paymentService = paymentService;
        this.walletService = walletService;
        this.paymentSucceededKafkaTemplate = paymentSucceededKafkaTemplate;
        this.paymentFailedKafkaTemplate = paymentFailedKafkaTemplate;
        this.paymentRefundedKafkaTemplate = paymentRefundedKafkaTemplate;
    }

    @KafkaListener(
            topics = "booking_created",
            groupId = "payment-service",
            containerFactory = "bookingCreatedKafkaListenerContainerFactory"
    )
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.debug("Received BookingCreatedEvent: {}", event);

        Wallet wallet = walletService.getWalletByUserId(event.getUserId());

        Payment newPayment = new Payment(event.getBookingId(), event.getFareAmount(), PaymentStatusType.PENDING);
        Payment payment = paymentService.createPayment(newPayment);

        try {
            WalletTransactionRequestDTO debitRequest = new WalletTransactionRequestDTO(payment.getAmount(), payment.getPaymentId());
            walletService.debit(wallet.getWalletId(), debitRequest);
            payment.setPaymentStatusType(PaymentStatusType.SUCCESS);
            paymentService.updatePayment(payment.getPaymentId(), payment);
            paymentSucceededKafkaTemplate.send(
                    "payment_succeeded",
                    new PaymentSucceededEvent(payment.getBookingId(), payment.getPaymentId())
            );
        } catch (InsufficientBalanceException ex) {
            payment.setPaymentStatusType(PaymentStatusType.FAILED);
            paymentService.updatePayment(payment.getPaymentId(), payment);
            paymentFailedKafkaTemplate.send(
                    "payment_failed",
                    new PaymentFailedEvent(payment.getBookingId(), payment.getPaymentId(), "Insufficient balance")
            );
        }
    }

    @KafkaListener(
            topics = "booking_cancelled",
            groupId = "payment-service",
            containerFactory = "bookingCancelledKafkaListenerContainerFactory"
    )
    public void handleBookingCancelled(BookingCancelledEvent event) {
        log.debug("Received BookingCancelledEvent: {}", event);

        Wallet wallet = walletService.getWalletByUserId(event.getUserId());

        Payment payment = paymentService.getPaymentByBookingId(event.getBookingId());
        if (payment == null || payment.getPaymentStatusType() != PaymentStatusType.SUCCESS) {
            log.warn("Payment not found or not successful, skipping refund for bookingId: {}", event.getBookingId());
            return;
        }

        try {
            WalletTransactionRequestDTO creditRequest = new WalletTransactionRequestDTO(payment.getAmount(), payment.getPaymentId());
            walletService.credit(wallet.getWalletId(), creditRequest);
            payment.setPaymentStatusType(PaymentStatusType.REFUNDED);
            paymentService.updatePayment(payment.getPaymentId(), payment);
            paymentRefundedKafkaTemplate.send(
                    "payment_refunded",
                    new PaymentRefundedEvent(payment.getBookingId(), payment.getPaymentId(), payment.getAmount())
            );
            log.info("Refund successful for bookingId: {}", payment.getBookingId());
        } catch (Exception ex) {
            payment.setPaymentStatusType(PaymentStatusType.FAILED);
            paymentService.updatePayment(payment.getPaymentId(), payment);
            paymentFailedKafkaTemplate.send(
                    "payment_failed",
                    new PaymentFailedEvent(payment.getBookingId(), payment.getPaymentId(), "Refund failed: " + ex.getMessage())
            );
            log.error("Refund failed for bookingId {}: {}", payment.getBookingId(), ex.getMessage(), ex);
        }
    }
}