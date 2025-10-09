package com.platformone.payment.controller;

import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable long walletId) {
        Wallet payment = walletService.getWalletById(walletId);
        if (payment == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet newWallet) {
        Wallet savedWallet = walletService.createWallet(newWallet);
        return new ResponseEntity<>(savedWallet, HttpStatus.CREATED);
    }

    @PostMapping("/init")
    public ResponseEntity<Wallet> initializeWallet(@RequestBody WalletCreateRequestDTO requestDTO) {
        Wallet savedWallet = walletService.initializeWallet(requestDTO);
        return new ResponseEntity<>(savedWallet, HttpStatus.CREATED);
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable long walletId, @RequestBody Wallet updatedWallet) {
        Optional<Wallet> payment = walletService.updateWallet(walletId, updatedWallet);
        if (payment.isPresent())
            return new ResponseEntity<>(payment.get(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<String> deleteWallet(@PathVariable long walletId) {
        boolean deleted = walletService.deleteWallet(walletId);
        if (deleted)
            return new ResponseEntity<>("Wallet deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>("Wallet not found", HttpStatus.NOT_FOUND);
    }
}