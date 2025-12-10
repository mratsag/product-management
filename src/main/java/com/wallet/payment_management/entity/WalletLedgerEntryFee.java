package com.wallet.payment_management.entity;

import com.wallet.payment_management.enums.WalletFeeTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet_ledger_entry_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletLedgerEntryFee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_ledger_entry_id", nullable = false)
    private WalletLedgerEntry walletLedgerEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 50)
    private WalletFeeTypeEnum feeType;

    @Column(name = "amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
