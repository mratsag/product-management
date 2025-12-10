package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {

    List<WalletAccount> findByCustomerId(Long customerId);

    Page<WalletAccount> findByCustomerId(Long customerId, Pageable pageable);

    List<WalletAccount> findByStatus(WalletAccountStatusEnum status);

    List<WalletAccount> findByCustomerIdAndStatus(Long customerId, WalletAccountStatusEnum status);

    List<WalletAccount> findByCustomerIdAndCreatedAtBetween(
            Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByCustomerIdAndCurrencyCode(Long customerId, String currencyCode);
}
