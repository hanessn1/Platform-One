package com.platformone.payment.controller;

import com.platformone.payment.entity.Payment;
import com.platformone.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable long paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Payment> createdPayment(@RequestBody Payment newPayment) {
        Payment savedPayment = paymentService.createPayment(newPayment);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(@PathVariable long paymentId, @RequestBody Payment updatedPayment) {
        Optional<Payment> payment = paymentService.updatePayment(paymentId, updatedPayment);
        if (payment.isPresent())
            return new ResponseEntity<>(payment.get(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable long paymentId) {
        boolean deleted = paymentService.deletePayment(paymentId);
        if (deleted)
            return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>("Payment not found", HttpStatus.NOT_FOUND);
    }
}
