package com.wallet.payment_management.entity;

import com.wallet.payment_management.enums.WalletLedgerEntryDirectionEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallet_ledger_entries", indexes = {
        @Index(name = "idx_wallet_ledger_entries_wallet_account_id", columnList = "wallet_account_id"),
        @Index(name = "idx_wallet_ledger_entries_status", columnList = "status"),
        @Index(name = "idx_wallet_ledger_entries_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletLedgerEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_account_id", nullable = false)
    private WalletAccount walletAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 50)
    private WalletLedgerEntryTypeEnum entryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_direction", nullable = false, length = 10)
    private WalletLedgerEntryDirectionEnum entryDirection;

    @Column(name = "amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 18, scale = 4)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WalletLedgerEntryStatusEnum status = WalletLedgerEntryStatusEnum.PENDING;

    @Column(name = "method", nullable = false, length = 50)
    private String method;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "walletLedgerEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WalletLedgerEntryFee> fees = new ArrayList<>();
}
