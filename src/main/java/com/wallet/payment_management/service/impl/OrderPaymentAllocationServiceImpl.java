package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.OrderPaymentAllocationRequest;
import com.wallet.payment_management.dto.response.OrderPaymentAllocationResponse;
import com.wallet.payment_management.entity.OrderPaymentAllocation;
import com.wallet.payment_management.entity.Payment;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.OrderPaymentAllocationRepository;
import com.wallet.payment_management.repository.PaymentRepository;
import com.wallet.payment_management.service.OrderPaymentAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderPaymentAllocationServiceImpl implements OrderPaymentAllocationService {

    private final OrderPaymentAllocationRepository orderPaymentAllocationRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public OrderPaymentAllocationResponse createAllocation(OrderPaymentAllocationRequest request) {
        log.info("Creating allocation. Order ID: {}, Payment ID: {}, Amount: {}", 
                request.getOrderId(), request.getPaymentId(), request.getAllocatedAmount());

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId()));

        OrderPaymentAllocation allocation = OrderPaymentAllocation.builder()
                .orderId(request.getOrderId())
                .payment(payment)
                .allocatedAmount(request.getAllocatedAmount())
                .build();

        OrderPaymentAllocation saved = orderPaymentAllocationRepository.save(allocation);
        log.info("Allocation created. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderPaymentAllocationResponse> getAllocationsByOrderId(Long orderId) {
        log.info("Getting allocations for order: {}", orderId);
        return orderPaymentAllocationRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderPaymentAllocationResponse reallocate(Long id, OrderPaymentAllocationRequest request) {
        log.info("Reallocating. ID: {}, New Order ID: {}, New Payment ID: {}, New Amount: {}", 
                id, request.getOrderId(), request.getPaymentId(), request.getAllocatedAmount());

        OrderPaymentAllocation allocation = orderPaymentAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderPaymentAllocation", "id", id));

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId()));

        allocation.setOrderId(request.getOrderId());
        allocation.setPayment(payment);
        allocation.setAllocatedAmount(request.getAllocatedAmount());

        OrderPaymentAllocation saved = orderPaymentAllocationRepository.save(allocation);
        log.info("Allocation updated. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    private OrderPaymentAllocationResponse mapToResponse(OrderPaymentAllocation allocation) {
        return OrderPaymentAllocationResponse.builder()
                .id(allocation.getId())
                .orderId(allocation.getOrderId())
                .paymentId(allocation.getPayment().getId())
                .allocatedAmount(allocation.getAllocatedAmount())
                .createdAt(allocation.getCreatedAt())
                .build();
    }
}
