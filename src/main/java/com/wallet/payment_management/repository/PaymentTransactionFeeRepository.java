package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.PaymentTransactionFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentTransactionFeeRepository extends JpaRepository<PaymentTransactionFee, Long> {

    List<PaymentTransactionFee> findByPaymentTransactionId(Long paymentTransactionId);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM PaymentTransactionFee f " +
           "WHERE f.paymentTransaction.payment.id = :paymentId")
    BigDecimal sumFeesByPaymentId(@Param("paymentId") Long paymentId);
}
