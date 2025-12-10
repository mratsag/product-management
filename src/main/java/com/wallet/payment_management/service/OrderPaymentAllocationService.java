package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.OrderPaymentAllocationRequest;
import com.wallet.payment_management.dto.response.OrderPaymentAllocationResponse;

import java.util.List;

public interface OrderPaymentAllocationService {

    OrderPaymentAllocationResponse createAllocation(OrderPaymentAllocationRequest request); // OPA-01

    List<OrderPaymentAllocationResponse> getAllocationsByOrderId(Long orderId); // OPA-02

    OrderPaymentAllocationResponse reallocate(Long id, OrderPaymentAllocationRequest request); // OPA-03
}
