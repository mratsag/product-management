package com.wallet.payment_management.event.listener;

import com.wallet.payment_management.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for audit logging.
 * Logs all domain events for audit trail purposes.
 */
@Component
@Slf4j
public class AuditEventListener {

    @Async
    @EventListener
    public void handleWalletCreated(WalletCreatedEvent event) {
        log.info("📗 AUDIT - Wallet Created: walletId={}, customerId={}, currency={}, type={}, balance={}",
                event.getWalletId(),
                event.getCustomerId(),
                event.getCurrencyCode(),
                event.getAccountType(),
                event.getInitialBalance());
    }

    @Async
    @EventListener
    public void handleWalletStatusChanged(WalletStatusChangedEvent event) {
        log.info("📙 AUDIT - Wallet Status Changed: walletId={}, {} -> {}, reason={}",
                event.getWalletId(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                event.getReason());
    }

    @Async
    @EventListener
    public void handleBalanceChanged(BalanceChangedEvent event) {
        log.info("💰 AUDIT - Balance Changed: walletId={}, type={}, amount={}, {} -> {} {}",
                event.getWalletId(),
                event.getOperationType(),
                event.getAmount(),
                event.getPreviousBalance(),
                event.getNewBalance(),
                event.getCurrencyCode());
    }

    @Async
    @EventListener
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        log.info("💳 AUDIT - Payment Created: paymentId={}, orderId={}, amount={} {}",
                event.getPaymentId(),
                event.getOrderId(),
                event.getAmount(),
                event.getCurrencyCode());
    }

    @Async
    @EventListener
    public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
        log.info("📊 AUDIT - Payment Status Changed: paymentId={}, {} -> {}, paid={}/{}",
                event.getPaymentId(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                event.getPaidAmount(),
                event.getTotalAmount());
    }
}
