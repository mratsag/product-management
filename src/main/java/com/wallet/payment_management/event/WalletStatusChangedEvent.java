package com.wallet.payment_management.event;

import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import lombok.Getter;

/**
 * Event published when a wallet account status changes.
 */
@Getter
public class WalletStatusChangedEvent extends BaseEvent {

    private final Long walletId;
    private final Long customerId;
    private final WalletAccountStatusEnum previousStatus;
    private final WalletAccountStatusEnum newStatus;
    private final String reason;

    public WalletStatusChangedEvent(Long walletId, Long customerId,
            WalletAccountStatusEnum previousStatus,
            WalletAccountStatusEnum newStatus,
            String reason) {
        super();
        this.walletId = walletId;
        this.customerId = customerId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("WalletStatusChangedEvent[walletId=%d, %s -> %s, reason=%s]",
                walletId, previousStatus, newStatus, reason);
    }
}
