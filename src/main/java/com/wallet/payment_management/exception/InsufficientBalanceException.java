package com.wallet.payment_management.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(Long walletAccountId, Double currentBalance, Double requiredAmount) {
        super(String.format("Insufficient balance in wallet account %d. Current: %.2f, Required: %.2f", 
                walletAccountId, currentBalance, requiredAmount));
    }
}
