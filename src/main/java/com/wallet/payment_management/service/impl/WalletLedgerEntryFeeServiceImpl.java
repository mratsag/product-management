package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.WalletLedgerEntryFeeRequest;
import com.wallet.payment_management.dto.response.WalletLedgerEntryFeeResponse;
import com.wallet.payment_management.entity.WalletLedgerEntry;
import com.wallet.payment_management.entity.WalletLedgerEntryFee;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.WalletLedgerEntryFeeRepository;
import com.wallet.payment_management.repository.WalletLedgerEntryRepository;
import com.wallet.payment_management.service.WalletLedgerEntryFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class WalletLedgerEntryFeeServiceImpl implements WalletLedgerEntryFeeService {

    private final WalletLedgerEntryFeeRepository walletLedgerEntryFeeRepository;
    private final WalletLedgerEntryRepository walletLedgerEntryRepository;

    @Override
    public WalletLedgerEntryFeeResponse createFee(WalletLedgerEntryFeeRequest request) {
        log.info("Creating fee for ledger entry: {}, Amount: {}", request.getWalletLedgerEntryId(), request.getAmount());

        WalletLedgerEntry ledgerEntry = walletLedgerEntryRepository.findById(request.getWalletLedgerEntryId())
                .orElseThrow(() -> new ResourceNotFoundException("WalletLedgerEntry", "id", request.getWalletLedgerEntryId()));

        WalletLedgerEntryFee fee = WalletLedgerEntryFee.builder()
                .walletLedgerEntry(ledgerEntry)
                .feeType(request.getFeeType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();

        WalletLedgerEntryFee saved = walletLedgerEntryFeeRepository.save(fee);
        log.info("Fee created. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletLedgerEntryFeeResponse> getFeesByLedgerEntryId(Long walletLedgerEntryId) {
        log.info("Getting fees for ledger entry: {}", walletLedgerEntryId);
        return walletLedgerEntryFeeRepository.findByWalletLedgerEntryId(walletLedgerEntryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCustomerFeeReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting fee report for customer: {}, Date range: {} - {}", customerId, startDate, endDate);
        BigDecimal total = walletLedgerEntryFeeRepository.sumFeesByCustomerIdAndDateRange(customerId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    private WalletLedgerEntryFeeResponse mapToResponse(WalletLedgerEntryFee fee) {
        return WalletLedgerEntryFeeResponse.builder()
                .id(fee.getId())
                .walletLedgerEntryId(fee.getWalletLedgerEntry().getId())
                .feeType(fee.getFeeType())
                .amount(fee.getAmount())
                .description(fee.getDescription())
                .createdAt(fee.getCreatedAt())
                .build();
    }
}
