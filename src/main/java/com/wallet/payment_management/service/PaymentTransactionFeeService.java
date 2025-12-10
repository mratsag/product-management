package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.PaymentTransactionFeeRequest;
import com.wallet.payment_management.dto.response.PaymentTransactionFeeResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentTransactionFeeService {

    PaymentTransactionFeeResponse createFee(PaymentTransactionFeeRequest request); // PF-01

    List<PaymentTransactionFeeResponse> getFeesByTransactionId(Long paymentTransactionId); // PF-02

    BigDecimal getTotalFeesByPaymentId(Long paymentId); // PF-03
}
