package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.OrderPaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPaymentAllocationRepository extends JpaRepository<OrderPaymentAllocation, Long> {

    List<OrderPaymentAllocation> findByOrderId(Long orderId);

    List<OrderPaymentAllocation> findByPaymentId(Long paymentId);
}
