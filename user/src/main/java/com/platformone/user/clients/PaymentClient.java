package com.platformone.user.clients;

import com.platformone.user.dto.WalletCreateRequestDTO;
import com.platformone.user.external.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment")
public interface PaymentClient {
    @PostMapping("/wallet/init")
    Wallet initializeWallet(@RequestBody WalletCreateRequestDTO requestDTO);
}