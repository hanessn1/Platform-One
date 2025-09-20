package com.platformone.payment.service;

import com.platformone.payment.entity.Payment;

import java.util.Optional;

public interface PaymentService {
    Payment getPaymentById(long paymentId);

    Payment createPayment(Payment newPayment);

    Optional<Payment> updatePayment(long paymentId, Payment updatedPayment);

    boolean deletePayment(long paymentId);
}
