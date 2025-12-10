package com.wallet.payment_management.event;

import com.wallet.payment_management.enums.PaymentStatusEnum;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Event published when a new payment is created.
 */
@Getter
public class PaymentCreatedEvent extends BaseEvent {

    private final Long paymentId;
    private final Long orderId;
    private final BigDecimal amount;
    private final String currencyCode;
    private final PaymentStatusEnum status;

    public PaymentCreatedEvent(Long paymentId, Long orderId, BigDecimal amount,
            String currencyCode, PaymentStatusEnum status) {
        super();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("PaymentCreatedEvent[paymentId=%d, orderId=%d, amount=%s %s]",
                paymentId, orderId, amount, currencyCode);
    }
}
