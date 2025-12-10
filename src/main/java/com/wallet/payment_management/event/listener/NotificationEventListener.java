package com.wallet.payment_management.event.listener;

import com.wallet.payment_management.event.*;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for notifications.
 * Handles important events that might require user notification.
 * 
 * Note: This is a placeholder implementation.
 * In a real application, this would integrate with:
 * - Email service
 * - Push notifications
 * - SMS gateway
 * - WebSocket for real-time updates
 */
@Component
@Slf4j
public class NotificationEventListener {

    @Async
    @EventListener
    public void handleWalletCreated(WalletCreatedEvent event) {
        log.info("🔔 NOTIFICATION - New wallet created for customer {}. Welcome email would be sent.",
                event.getCustomerId());
        // TODO: Implement email notification
        // emailService.sendWelcomeEmail(event.getCustomerId(), event.getWalletId());
    }

    @Async
    @EventListener
    public void handleLargeBalanceChange(BalanceChangedEvent event) {
        // Notify for large transactions (e.g., > 10,000)
        if (event.getAmount().abs().compareTo(java.math.BigDecimal.valueOf(10000)) > 0) {
            log.warn("🔔 NOTIFICATION - Large transaction detected! walletId={}, amount={} {}",
                    event.getWalletId(),
                    event.getAmount(),
                    event.getCurrencyCode());
            // TODO: Implement fraud alert notification
            // fraudService.checkTransaction(event);
        }
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentStatusChangedEvent event) {
        if (event.getNewStatus() == PaymentStatusEnum.PAID) {
            log.info("🔔 NOTIFICATION - Payment completed! paymentId={}, orderId={}. Confirmation would be sent.",
                    event.getPaymentId(),
                    event.getOrderId());
            // TODO: Implement payment confirmation notification
            // notificationService.sendPaymentConfirmation(event.getOrderId());
        }
    }

    @Async
    @EventListener
    public void handlePaymentFailed(PaymentStatusChangedEvent event) {
        if (event.getNewStatus() == PaymentStatusEnum.FAILED) {
            log.warn("🔔 NOTIFICATION - Payment failed! paymentId={}, orderId={}. Failure notice would be sent.",
                    event.getPaymentId(),
                    event.getOrderId());
            // TODO: Implement payment failure notification
            // notificationService.sendPaymentFailureNotice(event.getOrderId());
        }
    }
}
