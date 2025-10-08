package com.platformone.payment.repository;

import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();

        payment = new Payment(101L, 500.0, PaymentStatusType.SUCCESS);
        paymentRepository.save(payment);
    }

    @Test
    void testFindById() {
        Optional<Payment> found = paymentRepository.findById(payment.getPaymentId());

        assertThat(found).isPresent();
        assertThat(found.get().getBookingId()).isEqualTo(101L);
        assertThat(found.get().getAmount()).isEqualTo(500.0);
        assertThat(found.get().getPaymentStatusType()).isEqualTo(PaymentStatusType.SUCCESS);
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void testSaveAnotherPayment() {
        Payment another = new Payment(202L, 750.0, PaymentStatusType.PENDING);
        Payment saved = paymentRepository.save(another);

        assertThat(saved.getPaymentId()).isGreaterThan(0);
        assertThat(saved.getBookingId()).isEqualTo(202L);
        assertThat(saved.getAmount()).isEqualTo(750.0);
        assertThat(saved.getPaymentStatusType()).isEqualTo(PaymentStatusType.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();

        long totalCount = paymentRepository.count();
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    void testDeletePayment() {
        paymentRepository.delete(payment);

        Optional<Payment> deleted = paymentRepository.findById(payment.getPaymentId());
        assertThat(deleted).isNotPresent();

        assertThat(paymentRepository.count()).isZero();
    }
}