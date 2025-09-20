package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Payment;
import com.platformone.payment.repository.PaymentRepository;
import com.platformone.payment.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment getPaymentById(long paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    @Override
    public Payment createPayment(Payment newPayment) {
        return paymentRepository.save(newPayment);
    }

    @Override
    public Optional<Payment> updatePayment(long paymentId, Payment updatedPayment) {
        return paymentRepository.findById(paymentId).map(payment -> {
            payment.setAmount(updatedPayment.getAmount());
            payment.setPaymentStatusType(updatedPayment.getPaymentStatusType());
            payment.setBookingId(updatedPayment.getBookingId());
            return paymentRepository.save(payment);
        });
    }

    @Override
    public boolean deletePayment(long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) return false;
        paymentRepository.deleteById(paymentId);
        return true;
    }
}
