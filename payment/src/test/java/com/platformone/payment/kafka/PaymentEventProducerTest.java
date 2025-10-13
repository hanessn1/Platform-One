package com.platformone.payment.kafka;

import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.events.BookingCreatedEvent;
import com.platformone.payment.events.PaymentFailedEvent;
import com.platformone.payment.events.PaymentSucceededEvent;
import com.platformone.payment.exception.InsufficientBalanceException;
import com.platformone.payment.producer.PaymentEventProducer;
import com.platformone.payment.service.PaymentService;
import com.platformone.payment.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PaymentEventProducerTest {
    @Mock
    private PaymentService paymentService;

    @Mock
    private WalletService walletService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentEventProducer producer;

    private Wallet wallet;
    private Payment payment;
    private BookingCreatedEvent bookingCreatedEvent;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        long userId = 2L;
        double balance = 1000.0;

        wallet = new Wallet(userId, balance);
        wallet.setCreationTimeStamp();

        long bookingId = 1L;
        double fareAmount = 500.0;

        payment = new Payment(bookingId, fareAmount, PaymentStatusType.PENDING);
        payment.setCreationTimeStamp();

        bookingCreatedEvent = new BookingCreatedEvent(bookingId, userId, 1L, fareAmount);
    }

    @Test
    void handleBookingCreated_successfulPayment() {
        when(walletService.getWalletByUserId(wallet.getUserId())).thenReturn(wallet);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);
        when(walletService.debit(eq(wallet.getWalletId()), any(WalletTransactionRequestDTO.class)))
                .thenReturn(wallet);

        producer.handleBookingCreated(bookingCreatedEvent);

        verify(paymentService).updatePayment(anyLong(), eq(payment));
        verify(kafkaTemplate).send(eq("payment_succeeded"), any(PaymentSucceededEvent.class));
    }

    @Test
    void handleBookingCreated_insufficientBalance() {
        wallet = new Wallet(wallet.getUserId(), 100.0);

        when(walletService.getWalletByUserId(wallet.getUserId())).thenReturn(wallet);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);

        doThrow(new InsufficientBalanceException("Insufficient"))
                .when(walletService).debit(eq(wallet.getWalletId()), any());

        producer.handleBookingCreated(bookingCreatedEvent);

        Assertions.assertEquals(PaymentStatusType.FAILED, payment.getPaymentStatusType());
        verify(paymentService).updatePayment(anyLong(), eq(payment));
        verify(kafkaTemplate).send(eq("payment_failed"), any(PaymentFailedEvent.class));
    }
}