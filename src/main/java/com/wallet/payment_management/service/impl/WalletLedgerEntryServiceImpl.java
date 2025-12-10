package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.WalletLedgerEntryRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletLedgerEntryResponse;
import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.entity.WalletLedgerEntry;
import com.wallet.payment_management.enums.WalletLedgerEntryDirectionEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import com.wallet.payment_management.event.BalanceChangedEvent;
import com.wallet.payment_management.exception.ClosedAccountException;
import com.wallet.payment_management.exception.InsufficientBalanceException;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.WalletAccountRepository;
import com.wallet.payment_management.repository.WalletLedgerEntryRepository;
import com.wallet.payment_management.service.WalletLedgerEntryService;
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
public class WalletLedgerEntryServiceImpl implements WalletLedgerEntryService {

    private final WalletLedgerEntryRepository walletLedgerEntryRepository;
    private final WalletAccountRepository walletAccountRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WalletLedgerEntryResponse createLoadEntry(WalletLedgerEntryRequest request) {
        log.info("Creating LOAD entry for wallet account: {}, Amount: {}", request.getWalletAccountId(),
                request.getAmount());

        WalletAccount walletAccount = walletAccountRepository.findById(request.getWalletAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", request.getWalletAccountId()));

        if (walletAccount.getStatus() == com.wallet.payment_management.enums.WalletAccountStatusEnum.CLOSED) {
            throw new ClosedAccountException(walletAccount.getId());
        }

        BigDecimal newBalance = walletAccount.getCurrentBalance().add(request.getAmount());

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletAccount(walletAccount)
                .entryType(WalletLedgerEntryTypeEnum.LOAD)
                .entryDirection(WalletLedgerEntryDirectionEnum.CREDIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .status(WalletLedgerEntryStatusEnum.PENDING)
                .method(request.getMethod())
                .reference(request.getReference())
                .description(request.getDescription())
                .build();

        WalletLedgerEntry saved = walletLedgerEntryRepository.save(entry);

        // Update wallet balance and status
        walletAccount.setCurrentBalance(newBalance);
        saved.setStatus(WalletLedgerEntryStatusEnum.POSTED);
        walletAccountRepository.save(walletAccount);
        saved = walletLedgerEntryRepository.save(saved);

        log.info("LOAD entry created. ID: {}, New balance: {}", saved.getId(), newBalance);

        // Publish balance changed event
        publishBalanceChangedEvent(walletAccount, saved, request.getAmount(),
                walletAccount.getCurrentBalance().subtract(request.getAmount()), newBalance);

        return mapToResponse(saved);
    }

    @Override
    public WalletLedgerEntryResponse createSpendEntry(WalletLedgerEntryRequest request) {
        log.info("Creating SPEND entry for wallet account: {}, Amount: {}", request.getWalletAccountId(),
                request.getAmount());

        WalletAccount walletAccount = walletAccountRepository.findById(request.getWalletAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", request.getWalletAccountId()));

        if (walletAccount.getStatus() == com.wallet.payment_management.enums.WalletAccountStatusEnum.CLOSED) {
            throw new ClosedAccountException(walletAccount.getId());
        }

        // Check balance
        if (walletAccount.getCurrentBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    walletAccount.getId(),
                    walletAccount.getCurrentBalance().doubleValue(),
                    request.getAmount().doubleValue());
        }

        BigDecimal newBalance = walletAccount.getCurrentBalance().subtract(request.getAmount());

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletAccount(walletAccount)
                .entryType(WalletLedgerEntryTypeEnum.SPEND)
                .entryDirection(WalletLedgerEntryDirectionEnum.DEBIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .status(WalletLedgerEntryStatusEnum.PENDING)
                .method(request.getMethod())
                .reference(request.getReference())
                .description(request.getDescription())
                .build();

        WalletLedgerEntry saved = walletLedgerEntryRepository.save(entry);

        // Update wallet balance and status
        walletAccount.setCurrentBalance(newBalance);
        saved.setStatus(WalletLedgerEntryStatusEnum.POSTED);
        walletAccountRepository.save(walletAccount);
        saved = walletLedgerEntryRepository.save(saved);

        log.info("SPEND entry created. ID: {}, New balance: {}", saved.getId(), newBalance);

        // Publish balance changed event
        publishBalanceChangedEvent(walletAccount, saved, request.getAmount(),
                walletAccount.getCurrentBalance().add(request.getAmount()), newBalance);

        return mapToResponse(saved);
    }

    @Override
    public WalletLedgerEntryResponse createRefundEntry(WalletLedgerEntryRequest request) {
        log.info("Creating REFUND entry for wallet account: {}, Amount: {}", request.getWalletAccountId(),
                request.getAmount());

        WalletAccount walletAccount = walletAccountRepository.findById(request.getWalletAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", request.getWalletAccountId()));

        if (walletAccount.getStatus() == com.wallet.payment_management.enums.WalletAccountStatusEnum.CLOSED) {
            throw new ClosedAccountException(walletAccount.getId());
        }

        BigDecimal newBalance = walletAccount.getCurrentBalance().add(request.getAmount());

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletAccount(walletAccount)
                .entryType(WalletLedgerEntryTypeEnum.REFUND)
                .entryDirection(WalletLedgerEntryDirectionEnum.CREDIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .status(WalletLedgerEntryStatusEnum.PENDING)
                .method(request.getMethod())
                .reference(request.getReference())
                .description(request.getDescription())
                .build();

        WalletLedgerEntry saved = walletLedgerEntryRepository.save(entry);

        // Update wallet balance and status
        walletAccount.setCurrentBalance(newBalance);
        saved.setStatus(WalletLedgerEntryStatusEnum.POSTED);
        walletAccountRepository.save(walletAccount);
        saved = walletLedgerEntryRepository.save(saved);

        log.info("REFUND entry created. ID: {}, New balance: {}", saved.getId(), newBalance);

        // Publish balance changed event
        publishBalanceChangedEvent(walletAccount, saved, request.getAmount(),
                walletAccount.getCurrentBalance().subtract(request.getAmount()), newBalance);

        return mapToResponse(saved);
    }

    @Override
    public WalletLedgerEntryResponse createAdjustmentEntry(WalletLedgerEntryRequest request) {
        log.info("Creating ADJUSTMENT entry for wallet account: {}, Amount: {}", request.getWalletAccountId(),
                request.getAmount());

        WalletAccount walletAccount = walletAccountRepository.findById(request.getWalletAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", request.getWalletAccountId()));

        if (walletAccount.getStatus() == com.wallet.payment_management.enums.WalletAccountStatusEnum.CLOSED) {
            throw new ClosedAccountException(walletAccount.getId());
        }

        // Determine direction based on amount (positive = credit, negative = debit)
        // For adjustment, we'll use the amount sign to determine direction
        // But in the request, amount is always positive, so we need to check
        // description or add a field
        // For now, we'll default to CREDIT for adjustments (can be changed later)
        WalletLedgerEntryDirectionEnum direction = WalletLedgerEntryDirectionEnum.CREDIT;
        BigDecimal newBalance = walletAccount.getCurrentBalance().add(request.getAmount());

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletAccount(walletAccount)
                .entryType(WalletLedgerEntryTypeEnum.ADJUSTMENT)
                .entryDirection(direction)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .status(WalletLedgerEntryStatusEnum.PENDING)
                .method(request.getMethod())
                .reference(request.getReference())
                .description(request.getDescription())
                .build();

        WalletLedgerEntry saved = walletLedgerEntryRepository.save(entry);

        // Update wallet balance and status
        walletAccount.setCurrentBalance(newBalance);
        saved.setStatus(WalletLedgerEntryStatusEnum.POSTED);
        walletAccountRepository.save(walletAccount);
        saved = walletLedgerEntryRepository.save(saved);

        log.info("ADJUSTMENT entry created. ID: {}, New balance: {}", saved.getId(), newBalance);

        // Publish balance changed event
        publishBalanceChangedEvent(walletAccount, saved, request.getAmount(),
                walletAccount.getCurrentBalance().subtract(request.getAmount()), newBalance);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WalletLedgerEntryResponse> getLedgerEntries(
            Long walletAccountId,
            Long customerId,
            WalletLedgerEntryTypeEnum entryType,
            WalletLedgerEntryStatusEnum status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        log.info(
                "Getting ledger entries. walletAccountId: {}, customerId: {}, entryType: {}, status: {}, Page: {}, Size: {}",
                walletAccountId, customerId, entryType, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<WalletLedgerEntry> page;

        if (walletAccountId != null) {
            if (entryType != null && status != null) {
                page = walletLedgerEntryRepository.findByWalletAccountIdAndEntryTypeAndStatus(
                        walletAccountId, entryType, status, pageable);
            } else if (entryType != null) {
                page = walletLedgerEntryRepository.findByWalletAccountIdAndEntryType(walletAccountId, entryType,
                        pageable);
            } else if (status != null) {
                page = walletLedgerEntryRepository.findByWalletAccountIdAndStatus(walletAccountId, status, pageable);
            } else if (startDate != null && endDate != null) {
                page = walletLedgerEntryRepository.findByWalletAccountIdAndCreatedAtBetween(
                        walletAccountId, startDate, endDate, pageable);
            } else {
                page = walletLedgerEntryRepository.findByWalletAccountId(walletAccountId, pageable);
            }
        } else if (customerId != null) {
            if (startDate != null && endDate != null) {
                page = walletLedgerEntryRepository.findByCustomerIdAndCreatedAtBetween(
                        customerId, startDate, endDate, pageable);
            } else {
                page = walletLedgerEntryRepository.findByCustomerId(customerId, pageable);
            }
        } else {
            page = walletLedgerEntryRepository.findAll(pageable);
        }

        List<WalletLedgerEntryResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<WalletLedgerEntryResponse>builder()
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

    private WalletLedgerEntryResponse mapToResponse(WalletLedgerEntry entry) {
        return WalletLedgerEntryResponse.builder()
                .id(entry.getId())
                .walletAccountId(entry.getWalletAccount().getId())
                .entryType(entry.getEntryType())
                .entryDirection(entry.getEntryDirection())
                .amount(entry.getAmount())
                .balanceAfter(entry.getBalanceAfter())
                .status(entry.getStatus())
                .method(entry.getMethod())
                .reference(entry.getReference())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .build();
    }

    private void publishBalanceChangedEvent(WalletAccount wallet, WalletLedgerEntry entry,
            BigDecimal amount, BigDecimal previousBalance, BigDecimal newBalance) {
        eventPublisher.publishEvent(new BalanceChangedEvent(
                wallet.getId(),
                wallet.getCustomerId(),
                entry.getId(),
                entry.getEntryType(),
                amount,
                previousBalance,
                newBalance,
                wallet.getCurrencyCode(),
                entry.getDescription()));
    }
}
