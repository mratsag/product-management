package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.PaymentTransactionRequest;
import com.wallet.payment_management.dto.response.PaymentTransactionResponse;
import com.wallet.payment_management.entity.Payment;
import com.wallet.payment_management.entity.PaymentTransaction;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTransactionStatusEnum;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.PaymentRepository;
import com.wallet.payment_management.repository.PaymentTransactionRepository;
import com.wallet.payment_management.service.PaymentTransactionService;
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
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentTransactionResponse createTransaction(PaymentTransactionRequest request) {
        log.info("Creating payment transaction. Payment ID: {}, Type: {}, Amount: {}", 
                request.getPaymentId(), request.getTransactionType(), request.getPaidAmount());

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId()));

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(request.getTransactionType())
                .paidAmount(request.getPaidAmount())
                .method(request.getMethod())
                .status(PaymentTransactionStatusEnum.PENDING)
                .reference(request.getReference())
                .build();

        PaymentTransaction saved = paymentTransactionRepository.save(transaction);
        log.info("Payment transaction created. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public PaymentTransactionResponse processTransactionResult(Long id, PaymentTransactionStatusEnum status) {
        log.info("Processing transaction result. ID: {}, Status: {}", id, status);

        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", id));

        transaction.setStatus(status);
        PaymentTransaction saved = paymentTransactionRepository.save(transaction);

        // Update payment paidAmount and status if transaction is successful
        if (status == PaymentTransactionStatusEnum.SUCCESS) {
            Payment payment = saved.getPayment();
            BigDecimal newPaidAmount = payment.getPaidAmount().add(saved.getPaidAmount());
            payment.setPaidAmount(newPaidAmount);

            // Update payment status based on paidAmount
            if (newPaidAmount.compareTo(payment.getAmount()) >= 0) {
                payment.setStatus(PaymentStatusEnum.PAID);
            } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                payment.setStatus(PaymentStatusEnum.PARTIALLY_PAID);
            } else {
                payment.setStatus(PaymentStatusEnum.PENDING);
            }

            paymentRepository.save(payment);
        } else if (status == PaymentTransactionStatusEnum.FAILED) {
            // If all transactions failed, mark payment as FAILED
            Payment payment = saved.getPayment();
            long successCount = payment.getTransactions().stream()
                    .filter(t -> t.getStatus() == PaymentTransactionStatusEnum.SUCCESS)
                    .count();
            if (successCount == 0 && payment.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                payment.setStatus(PaymentStatusEnum.FAILED);
                paymentRepository.save(payment);
            }
        }

        log.info("Transaction result processed. ID: {}, Status: {}", saved.getId(), saved.getStatus());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getTransactionsByPaymentId(Long paymentId) {
        log.info("Getting transactions for payment: {}", paymentId);
        return paymentTransactionRepository.findByPaymentId(paymentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void correlateWithWallet(Long paymentTransactionId, String walletReference) {
        log.info("Correlating payment transaction {} with wallet reference: {}", paymentTransactionId, walletReference);

        PaymentTransaction transaction = paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentTransaction", "id", paymentTransactionId));

        // Update reference to link with wallet ledger entry
        transaction.setReference(walletReference);
        paymentTransactionRepository.save(transaction);

        log.info("Payment transaction correlated with wallet. Transaction ID: {}, Reference: {}", 
                transaction.getId(), walletReference);
    }

    private PaymentTransactionResponse mapToResponse(PaymentTransaction transaction) {
        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .paymentId(transaction.getPayment().getId())
                .transactionType(transaction.getTransactionType())
                .paidAmount(transaction.getPaidAmount())
                .method(transaction.getMethod())
                .status(transaction.getStatus())
                .reference(transaction.getReference())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
