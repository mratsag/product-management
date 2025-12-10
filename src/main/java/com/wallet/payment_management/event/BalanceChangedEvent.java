package com.wallet.payment_management.event;

import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Event published when a wallet balance changes.
 */
@Getter
public class BalanceChangedEvent extends BaseEvent {

    private final Long walletId;
    private final Long customerId;
    private final Long ledgerEntryId;
    private final WalletLedgerEntryTypeEnum operationType;
    private final BigDecimal amount;
    private final BigDecimal previousBalance;
    private final BigDecimal newBalance;
    private final String currencyCode;
    private final String description;

    public BalanceChangedEvent(Long walletId, Long customerId, Long ledgerEntryId,
            WalletLedgerEntryTypeEnum operationType, BigDecimal amount,
            BigDecimal previousBalance, BigDecimal newBalance,
            String currencyCode, String description) {
        super();
        this.walletId = walletId;
        this.customerId = customerId;
        this.ledgerEntryId = ledgerEntryId;
        this.operationType = operationType;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.currencyCode = currencyCode;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("BalanceChangedEvent[walletId=%d, type=%s, amount=%s, %s -> %s]",
                walletId, operationType, amount, previousBalance, newBalance);
    }
}
