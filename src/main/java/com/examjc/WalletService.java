package com.examjc;

import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // Метод для депозита средств с оптимистической блокировкой
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Wallet deposit(UUID walletId, double amount) {
        return retry(() -> {
            Wallet wallet = findWalletById(walletId);
            wallet.setBalance(wallet.getBalance() + amount);
            return walletRepository.save(wallet);
        });
    }

    // Метод для снятия сресдвт с оптимистической блокировкой
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Wallet withdraw(UUID walletId, double amount) {
        return retry(() -> {
            Wallet wallet = findWalletById(walletId);
            if (wallet.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient balance");
            }
            wallet.setBalance(wallet.getBalance() - amount);
            return walletRepository.save(wallet);
        });
    }

    // Поиск кошелька по ID
    public Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    // Метод для повторной попытки при OptimisticLockException
    private Wallet retry(RetryableOperation operation) {
        int retries = 3;
        while (retries > 0) {
            try {
                return operation.execute();
            } catch (OptimisticLockException e) {
                retries--;
                if (retries == 0) {
                    throw e;  // Если не удалось после нескольких попыток, выбрасываем исключение
                }
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface RetryableOperation {
        Wallet execute();
    }
}
