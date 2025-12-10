package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.PaymentTransactionFeeRequest;
import com.wallet.payment_management.dto.response.PaymentTransactionFeeResponse;
import com.wallet.payment_management.entity.PaymentTransaction;
import com.wallet.payment_management.entity.PaymentTransactionFee;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.PaymentTransactionFeeRepository;
import com.wallet.payment_management.repository.PaymentTransactionRepository;
import com.wallet.payment_management.service.PaymentTransactionFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentTransactionFeeServiceImpl implements PaymentTransactionFeeService {

    private final PaymentTransactionFeeRepository paymentTransactionFeeRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentTransactionFeeResponse createFee(PaymentTransactionFeeRequest request) {
        log.info("Creating fee for payment transaction: {}, Amount: {}", request.getPaymentTransactionId(), request.getAmount());

        PaymentTransaction transaction = paymentTransactionRepository.findById(request.getPaymentTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", request.getPaymentTransactionId()));

        PaymentTransactionFee fee = PaymentTransactionFee.builder()
                .paymentTransaction(transaction)
                .feeType(request.getFeeType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();

        PaymentTransactionFee saved = paymentTransactionFeeRepository.save(fee);
        log.info("Fee created. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionFeeResponse> getFeesByTransactionId(Long paymentTransactionId) {
        log.info("Getting fees for payment transaction: {}", paymentTransactionId);
        return paymentTransactionFeeRepository.findByPaymentTransactionId(paymentTransactionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalFeesByPaymentId(Long paymentId) {
        log.info("Getting total fees for payment: {}", paymentId);
        BigDecimal total = paymentTransactionFeeRepository.sumFeesByPaymentId(paymentId);
        return total != null ? total : BigDecimal.ZERO;
    }

    private PaymentTransactionFeeResponse mapToResponse(PaymentTransactionFee fee) {
        return PaymentTransactionFeeResponse.builder()
                .id(fee.getId())
                .paymentTransactionId(fee.getPaymentTransaction().getId())
                .feeType(fee.getFeeType())
                .amount(fee.getAmount())
                .description(fee.getDescription())
                .createdAt(fee.getCreatedAt())
                .build();
    }
}
