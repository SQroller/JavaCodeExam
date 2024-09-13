package com.examjc;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.UUID;

@Data
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private double amount;
}

enum OperationType {
    DEPOSIT, WITHDRAW
}
