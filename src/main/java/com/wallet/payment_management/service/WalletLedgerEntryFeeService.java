package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.WalletLedgerEntryFeeRequest;
import com.wallet.payment_management.dto.response.WalletLedgerEntryFeeResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletLedgerEntryFeeService {

    WalletLedgerEntryFeeResponse createFee(WalletLedgerEntryFeeRequest request); // WF-01

    List<WalletLedgerEntryFeeResponse> getFeesByLedgerEntryId(Long walletLedgerEntryId); // WF-02

    BigDecimal getCustomerFeeReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate); // WF-03
}
