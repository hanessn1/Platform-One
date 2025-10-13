package com.platformone.payment.producer;

import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.events.BookingCreatedEvent;
import com.platformone.payment.events.PaymentFailedEvent;
import com.platformone.payment.events.PaymentSucceededEvent;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate, WalletService walletService) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.walletService = walletService;
    }

    @KafkaListener(topics = "booking_created", groupId = "payment-service")
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
            kafkaTemplate.send(
                    "payment_succeeded",
                    new PaymentSucceededEvent(payment.getBookingId(), payment.getPaymentId())
            );
        } catch (InsufficientBalanceException ex) {
            payment.setPaymentStatusType(PaymentStatusType.FAILED);
            paymentService.updatePayment(payment.getPaymentId(), payment);
            kafkaTemplate.send(
                    "payment_failed",
                    new PaymentFailedEvent(payment.getBookingId(), payment.getPaymentId(), "Insufficient balance")
            );
        }
    }
}