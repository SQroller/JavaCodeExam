package com.examjc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<String> performOperation(@RequestBody WalletOperationRequest request) {
        try {
            Wallet wallet;
            if (request.getOperationType() == OperationType.DEPOSIT) {
                wallet = walletService.deposit(request.getWalletId(), request.getAmount());
            } else if (request.getOperationType() == OperationType.WITHDRAW) {
                wallet = walletService.withdraw(request.getWalletId(), request.getAmount());
            } else {
                return new ResponseEntity<>("Invalid operation type", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok("Operation successful. New balance: " + wallet.getBalance());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<String> getBalance(@PathVariable UUID walletId) {
        try {
            Wallet wallet = walletService.findWalletById(walletId);
            return ResponseEntity.ok("Wallet balance: " + wallet.getBalance());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
