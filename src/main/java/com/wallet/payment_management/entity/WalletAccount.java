package com.wallet.payment_management.entity;

import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallet_accounts", indexes = {
        @Index(name = "idx_wallet_accounts_customer_id", columnList = "customer_id"),
        @Index(name = "idx_wallet_accounts_status", columnList = "status"),
        @Index(name = "idx_wallet_accounts_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    @Builder.Default
    private WalletAccountTypeEnum accountType = WalletAccountTypeEnum.STANDARD;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "current_balance", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WalletAccountStatusEnum status = WalletAccountStatusEnum.ACTIVE;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "walletAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WalletLedgerEntry> ledgerEntries = new ArrayList<>();
}
