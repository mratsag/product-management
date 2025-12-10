package com.wallet.payment_management.event;

import com.wallet.payment_management.enums.PaymentStatusEnum;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Event published when a payment status changes.
 */
@Getter
public class PaymentStatusChangedEvent extends BaseEvent {

    private final Long paymentId;
    private final Long orderId;
    private final PaymentStatusEnum previousStatus;
    private final PaymentStatusEnum newStatus;
    private final BigDecimal paidAmount;
    private final BigDecimal totalAmount;

    public PaymentStatusChangedEvent(Long paymentId, Long orderId,
            PaymentStatusEnum previousStatus,
            PaymentStatusEnum newStatus,
            BigDecimal paidAmount, BigDecimal totalAmount) {
        super();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.paidAmount = paidAmount;
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return String.format("PaymentStatusChangedEvent[paymentId=%d, %s -> %s, paid=%s/%s]",
                paymentId, previousStatus, newStatus, paidAmount, totalAmount);
    }
}
