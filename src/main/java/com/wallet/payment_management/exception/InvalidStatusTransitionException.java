package com.wallet.payment_management.exception;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String resource, String currentStatus, String newStatus) {
        super(String.format("Invalid status transition for %s: %s -> %s", resource, currentStatus, newStatus));
    }
}
