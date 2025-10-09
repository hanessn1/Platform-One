package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import com.platformone.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment existingPayment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        existingPayment = new Payment(1001L, 500.0, PaymentStatusType.PENDING);
        existingPayment.setCreationTimeStamp();
    }

    @Test
    void testGetPaymentById_PaymentExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existingPayment));

        Payment found = paymentService.getPaymentById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getBookingId()).isEqualTo(1001L);
        assertThat(found.getAmount()).isEqualTo(500.0);
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaymentById_PaymentDoesNotExist() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        Payment found = paymentService.getPaymentById(1L);

        assertThat(found).isNull();
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(existingPayment);

        Payment created = paymentService.createPayment(existingPayment);

        assertThat(created).isNotNull();
        assertThat(created.getAmount()).isEqualTo(500.0);
        verify(paymentRepository, times(1)).save(existingPayment);
    }

    @Test
    void testUpdatePayment_PaymentExists() {
        Payment updatedData = new Payment(2002L, 800.0, PaymentStatusType.SUCCESS);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Payment> result = paymentService.updatePayment(1L, updatedData);

        assertThat(result).isPresent();
        Payment updated = result.get();
        assertThat(updated.getAmount()).isEqualTo(800.0);
        assertThat(updated.getPaymentStatusType()).isEqualTo(PaymentStatusType.SUCCESS);
        assertThat(updated.getBookingId()).isEqualTo(2002L);

        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(existingPayment);
    }

    @Test
    void testUpdatePayment_PaymentDoesNotExists() {
        Payment updatedData = new Payment(2002L, 800.0, PaymentStatusType.SUCCESS);

        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.updatePayment(1L, updatedData);

        assertThat(result).isEmpty();
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testDeletePayment_PaymentExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existingPayment));

        boolean deleted = paymentService.deletePayment(1L);

        assertThat(deleted).isTrue();
        verify(paymentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePayment_PaymentDoesNotExist() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = paymentService.deletePayment(1L);

        assertThat(deleted).isFalse();
        verify(paymentRepository, never()).deleteById(anyLong());
    }
}