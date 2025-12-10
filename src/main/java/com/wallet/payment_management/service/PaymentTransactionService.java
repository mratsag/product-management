package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.PaymentTransactionRequest;
import com.wallet.payment_management.dto.response.PaymentTransactionResponse;
import com.wallet.payment_management.enums.PaymentTransactionStatusEnum;
import com.wallet.payment_management.enums.PaymentTransactionTypeEnum;

import java.util.List;

public interface PaymentTransactionService {

    PaymentTransactionResponse createTransaction(PaymentTransactionRequest request); // PT-01, PT-02

    PaymentTransactionResponse processTransactionResult(Long id, PaymentTransactionStatusEnum status); // PT-03

    List<PaymentTransactionResponse> getTransactionsByPaymentId(Long paymentId); // PT-04

    void correlateWithWallet(Long paymentTransactionId, String walletReference); // PT-05
}
