package com.platformone.payment.controller;

import com.platformone.payment.entity.Transaction;
import com.platformone.payment.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Transaction> createdTransaction(@RequestBody Transaction newTransaction) {
        Transaction savedTransaction = transactionService.createTransaction(newTransaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable long transactionId, @RequestBody Transaction updatedTransaction) {
        Optional<Transaction> transaction = transactionService.updateTransaction(transactionId, updatedTransaction);
        if (transaction.isPresent())
            return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable long transactionId) {
        boolean deleted = transactionService.deleteTransaction(transactionId);
        if (deleted)
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
    }
}