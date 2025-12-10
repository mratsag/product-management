package com.wallet.payment_management.event;

import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Event published when a new wallet account is created.
 */
@Getter
public class WalletCreatedEvent extends BaseEvent {

    private final Long walletId;
    private final Long customerId;
    private final String currencyCode;
    private final WalletAccountTypeEnum accountType;
    private final WalletAccountStatusEnum status;
    private final BigDecimal initialBalance;

    public WalletCreatedEvent(Long walletId, Long customerId, String currencyCode,
            WalletAccountTypeEnum accountType, WalletAccountStatusEnum status,
            BigDecimal initialBalance) {
        super();
        this.walletId = walletId;
        this.customerId = customerId;
        this.currencyCode = currencyCode;
        this.accountType = accountType;
        this.status = status;
        this.initialBalance = initialBalance;
    }

    @Override
    public String toString() {
        return String.format("WalletCreatedEvent[walletId=%d, customerId=%d, currency=%s, type=%s]",
                walletId, customerId, currencyCode, accountType);
    }
}
