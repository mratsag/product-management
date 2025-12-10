package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.PaymentTransaction;
import com.wallet.payment_management.enums.PaymentTransactionStatusEnum;
import com.wallet.payment_management.enums.PaymentTransactionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByPaymentId(Long paymentId);

    List<PaymentTransaction> findByStatus(PaymentTransactionStatusEnum status);

    List<PaymentTransaction> findByTransactionType(PaymentTransactionTypeEnum transactionType);

    List<PaymentTransaction> findByPaymentIdAndStatus(
            Long paymentId, PaymentTransactionStatusEnum status);

    List<PaymentTransaction> findByPaymentIdAndTransactionType(
            Long paymentId, PaymentTransactionTypeEnum transactionType);

    List<PaymentTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentTransaction> findByReference(String reference);
}
