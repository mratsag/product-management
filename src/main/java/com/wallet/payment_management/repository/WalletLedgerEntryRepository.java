package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.WalletLedgerEntry;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletLedgerEntryRepository extends JpaRepository<WalletLedgerEntry, Long> {

    List<WalletLedgerEntry> findByWalletAccountId(Long walletAccountId);

    Page<WalletLedgerEntry> findByWalletAccountId(Long walletAccountId, Pageable pageable);

    @Query("SELECT e FROM WalletLedgerEntry e WHERE e.walletAccount.customerId = :customerId")
    List<WalletLedgerEntry> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT e FROM WalletLedgerEntry e WHERE e.walletAccount.customerId = :customerId")
    Page<WalletLedgerEntry> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    List<WalletLedgerEntry> findByStatus(WalletLedgerEntryStatusEnum status);

    List<WalletLedgerEntry> findByEntryType(WalletLedgerEntryTypeEnum entryType);

    List<WalletLedgerEntry> findByWalletAccountIdAndStatus(
            Long walletAccountId, WalletLedgerEntryStatusEnum status);

    Page<WalletLedgerEntry> findByWalletAccountIdAndStatus(
            Long walletAccountId, WalletLedgerEntryStatusEnum status, Pageable pageable);

    List<WalletLedgerEntry> findByWalletAccountIdAndEntryType(
            Long walletAccountId, WalletLedgerEntryTypeEnum entryType);

    Page<WalletLedgerEntry> findByWalletAccountIdAndEntryType(
            Long walletAccountId, WalletLedgerEntryTypeEnum entryType, Pageable pageable);

    List<WalletLedgerEntry> findByWalletAccountIdAndEntryTypeAndStatus(
            Long walletAccountId, WalletLedgerEntryTypeEnum entryType, WalletLedgerEntryStatusEnum status);

    Page<WalletLedgerEntry> findByWalletAccountIdAndEntryTypeAndStatus(
            Long walletAccountId, WalletLedgerEntryTypeEnum entryType, WalletLedgerEntryStatusEnum status, Pageable pageable);

    List<WalletLedgerEntry> findByWalletAccountIdAndCreatedAtBetween(
            Long walletAccountId, LocalDateTime startDate, LocalDateTime endDate);

    Page<WalletLedgerEntry> findByWalletAccountIdAndCreatedAtBetween(
            Long walletAccountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT e FROM WalletLedgerEntry e WHERE e.walletAccount.customerId = :customerId " +
           "AND e.createdAt BETWEEN :startDate AND :endDate")
    List<WalletLedgerEntry> findByCustomerIdAndCreatedAtBetween(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM WalletLedgerEntry e WHERE e.walletAccount.customerId = :customerId " +
           "AND e.createdAt BETWEEN :startDate AND :endDate")
    Page<WalletLedgerEntry> findByCustomerIdAndCreatedAtBetween(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<WalletLedgerEntry> findByReference(String reference);
}
