package com.platformone.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.payment.entity.Payment;
import com.platformone.payment.entity.PaymentStatusType;
import com.platformone.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void testGetPaymentById_Success() throws Exception {
        Payment payment = new Payment(123, 250.0, PaymentStatusType.SUCCESS);
        payment.setBookingId(123);

        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/payment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.amount").value(250.0))
                .andExpect(jsonPath("$.paymentStatusType").value("SUCCESS"));
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        when(paymentService.getPaymentById(99L)).thenReturn(null);

        mockMvc.perform(get("/payment/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePayment() throws Exception {
        Payment payment = new Payment(123, 500.0, PaymentStatusType.PENDING);
        Payment savedPayment = new Payment(123, 500.0, PaymentStatusType.PENDING);
        savedPayment.setBookingId(123);

        when(paymentService.createPayment(any(Payment.class))).thenReturn(savedPayment);

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.paymentStatusType").value("PENDING"));
    }

    @Test
    void testUpdatePayment_Success() throws Exception {
        Payment updatedPayment = new Payment(123, 750.0, PaymentStatusType.SUCCESS);
        updatedPayment.setBookingId(123);

        when(paymentService.updatePayment(eq(1L), any(Payment.class)))
                .thenReturn(Optional.of(updatedPayment));

        mockMvc.perform(put("/payment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(750.0))
                .andExpect(jsonPath("$.paymentStatusType").value("SUCCESS"));
    }

    @Test
    void testUpdatePayment_NotFound() throws Exception {
        Payment updatedPayment = new Payment(123, 750.0, PaymentStatusType.PENDING);

        when(paymentService.updatePayment(eq(99L), any(Payment.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/payment/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePayment_Success() throws Exception {
        when(paymentService.deletePayment(1L)).thenReturn(true);

        mockMvc.perform(delete("/payment/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment deleted successfully"));
    }

    @Test
    void testDeletePayment_NotFound() throws Exception {
        when(paymentService.deletePayment(99L)).thenReturn(false);

        mockMvc.perform(delete("/payment/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payment not found"));
    }
}