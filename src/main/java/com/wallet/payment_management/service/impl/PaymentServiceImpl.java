package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.PaymentRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.PaymentResponse;
import com.wallet.payment_management.entity.Payment;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import com.wallet.payment_management.event.PaymentCreatedEvent;
import com.wallet.payment_management.event.PaymentStatusChangedEvent;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.PaymentRepository;
import com.wallet.payment_management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment. Type: {}, Amount: {}", request.getPaymentType(), request.getAmount());

        Payment payment = Payment.builder()
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .paidAmount(BigDecimal.ZERO)
                .status(PaymentStatusEnum.PENDING)
                .description(request.getDescription())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created. ID: {}", saved.getId());

        // Publish payment created event
        eventPublisher.publishEvent(new PaymentCreatedEvent(
                saved.getId(),
                null,
                saved.getAmount(),
                "TRY",
                saved.getStatus()));

        return mapToResponse(saved);
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long id, PaymentStatusEnum status) {
        log.info("Updating payment status. ID: {}, Status: {}", id, status);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        PaymentStatusEnum previousStatus = payment.getStatus();
        payment.setStatus(status);
        Payment saved = paymentRepository.save(payment);

        log.info("Payment status updated. ID: {}, Status: {}", saved.getId(), saved.getStatus());

        // Publish payment status changed event
        eventPublisher.publishEvent(new PaymentStatusChangedEvent(
                saved.getId(),
                null, // orderId
                previousStatus,
                saved.getStatus(),
                saved.getPaidAmount(),
                saved.getAmount()));

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(
            PaymentTypeEnum paymentType,
            PaymentStatusEnum status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        log.info("Getting payments. Type: {}, Status: {}, Date range: {} - {}, Page: {}, Size: {}",
                paymentType, status, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        Page<Payment> page;

        if (paymentType != null && status != null) {
            page = paymentRepository.findByPaymentTypeAndStatus(paymentType, status, pageable);
        } else if (paymentType != null) {
            if (startDate != null && endDate != null) {
                page = paymentRepository.findByPaymentTypeAndCreatedAtBetween(paymentType, startDate, endDate,
                        pageable);
            } else {
                page = paymentRepository.findByPaymentType(paymentType, pageable);
            }
        } else if (status != null) {
            if (startDate != null && endDate != null) {
                page = paymentRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate, pageable);
            } else {
                page = paymentRepository.findByStatus(status, pageable);
            }
        } else if (startDate != null && endDate != null) {
            page = paymentRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        } else {
            page = paymentRepository.findAll(pageable);
        }

        List<PaymentResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<PaymentResponse>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.info("Getting payment by ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .paidAmount(payment.getPaidAmount())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
