package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.WalletLedgerEntryRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletLedgerEntryResponse;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface WalletLedgerEntryService {

    WalletLedgerEntryResponse createLoadEntry(WalletLedgerEntryRequest request); // WL-01

    WalletLedgerEntryResponse createSpendEntry(WalletLedgerEntryRequest request); // WL-02

    WalletLedgerEntryResponse createRefundEntry(WalletLedgerEntryRequest request); // WL-03

    WalletLedgerEntryResponse createAdjustmentEntry(WalletLedgerEntryRequest request); // WL-04

    PageResponse<WalletLedgerEntryResponse> getLedgerEntries(
            Long walletAccountId,
            Long customerId,
            WalletLedgerEntryTypeEnum entryType,
            WalletLedgerEntryStatusEnum status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable); // WL-05
}
