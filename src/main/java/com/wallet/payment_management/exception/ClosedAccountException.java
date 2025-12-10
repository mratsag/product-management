package com.wallet.payment_management.exception;

public class ClosedAccountException extends RuntimeException {

    public ClosedAccountException(String message) {
        super(message);
    }

    public ClosedAccountException(Long walletAccountId) {
        super(String.format("Wallet account %d is closed and cannot accept new ledger entries", walletAccountId));
    }
}
