package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.WalletLedgerEntryFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletLedgerEntryFeeRepository extends JpaRepository<WalletLedgerEntryFee, Long> {

    List<WalletLedgerEntryFee> findByWalletLedgerEntryId(Long walletLedgerEntryId);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM WalletLedgerEntryFee f " +
           "WHERE f.walletLedgerEntry.walletAccount.customerId = :customerId " +
           "AND f.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumFeesByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
