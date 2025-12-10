package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.PaymentRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.PaymentResponse;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request); // P-01, P-02

    PaymentResponse updatePaymentStatus(Long id, PaymentStatusEnum status); // P-03

    PageResponse<PaymentResponse> getPayments(
            PaymentTypeEnum paymentType,
            PaymentStatusEnum status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable); // P-04

    PaymentResponse getPaymentById(Long id);
}
