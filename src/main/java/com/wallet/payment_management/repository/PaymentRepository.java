package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.Payment;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentType(PaymentTypeEnum paymentType);

    Page<Payment> findByPaymentType(PaymentTypeEnum paymentType, Pageable pageable);

    List<Payment> findByStatus(PaymentStatusEnum status);

    Page<Payment> findByStatus(PaymentStatusEnum status, Pageable pageable);

    List<Payment> findByPaymentTypeAndStatus(PaymentTypeEnum paymentType, PaymentStatusEnum status);

    Page<Payment> findByPaymentTypeAndStatus(PaymentTypeEnum paymentType, PaymentStatusEnum status, Pageable pageable);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Payment> findByPaymentTypeAndCreatedAtBetween(
            PaymentTypeEnum paymentType, LocalDateTime startDate, LocalDateTime endDate);

    Page<Payment> findByPaymentTypeAndCreatedAtBetween(
            PaymentTypeEnum paymentType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Payment> findByStatusAndCreatedAtBetween(
            PaymentStatusEnum status, LocalDateTime startDate, LocalDateTime endDate);

    Page<Payment> findByStatusAndCreatedAtBetween(
            PaymentStatusEnum status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
