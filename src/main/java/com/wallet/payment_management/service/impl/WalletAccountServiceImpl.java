package com.wallet.payment_management.service.impl;

import com.wallet.payment_management.dto.request.WalletAccountRequest;
import com.wallet.payment_management.dto.request.WalletAccountStatusUpdateRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletAccountResponse;
import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.exception.ClosedAccountException;
import com.wallet.payment_management.exception.InvalidStatusTransitionException;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.WalletAccountRepository;
import com.wallet.payment_management.service.WalletAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletAccountServiceImpl implements WalletAccountService {

    private final WalletAccountRepository walletAccountRepository;

    @Override
    public WalletAccountResponse createWalletAccount(WalletAccountRequest request) {
        log.info("Creating wallet account for customer: {}", request.getCustomerId());

        WalletAccount walletAccount = WalletAccount.builder()
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType() != null ? request.getAccountType() : com.wallet.payment_management.enums.WalletAccountTypeEnum.STANDARD)
                .currencyCode(request.getCurrencyCode())
                .currentBalance(BigDecimal.ZERO)
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        WalletAccount saved = walletAccountRepository.save(walletAccount);
        log.info("Wallet account created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WalletAccountResponse> getWalletAccountsByCustomerId(Long customerId, Pageable pageable) {
        log.info("Getting wallet accounts for customer: {}, Page: {}, Size: {}", 
                customerId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<WalletAccount> page = walletAccountRepository.findByCustomerId(customerId, pageable);
        
        List<WalletAccountResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<WalletAccountResponse>builder()
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
    public WalletAccountResponse getWalletAccountById(Long id) {
        log.info("Getting wallet account by ID: {}", id);
        WalletAccount walletAccount = walletAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", id));
        return mapToResponse(walletAccount);
    }

    @Override
    public WalletAccountResponse updateWalletAccountStatus(Long id, WalletAccountStatusUpdateRequest request) {
        log.info("Updating wallet account status. ID: {}, New status: {}", id, request.getStatus());

        WalletAccount walletAccount = walletAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", id));

        WalletAccountStatusEnum currentStatus = walletAccount.getStatus();
        WalletAccountStatusEnum newStatus = request.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidStatusTransitionException("WalletAccount", currentStatus.name(), newStatus.name());
        }

        walletAccount.setStatus(newStatus);

        // Set closedAt if status is CLOSED
        if (newStatus == WalletAccountStatusEnum.CLOSED) {
            walletAccount.setClosedAt(java.time.LocalDateTime.now());
        }

        WalletAccount saved = walletAccountRepository.save(walletAccount);
        log.info("Wallet account status updated. ID: {}, Status: {}", saved.getId(), saved.getStatus());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkBalance(Long walletAccountId, BigDecimal requiredAmount) {
        log.debug("Checking balance for wallet account: {}, Required: {}", walletAccountId, requiredAmount);

        WalletAccount walletAccount = walletAccountRepository.findById(walletAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("WalletAccount", "id", walletAccountId));

        if (walletAccount.getStatus() == WalletAccountStatusEnum.CLOSED) {
            throw new ClosedAccountException(walletAccountId);
        }

        return walletAccount.getCurrentBalance().compareTo(requiredAmount) >= 0;
    }

    private boolean isValidStatusTransition(WalletAccountStatusEnum current, WalletAccountStatusEnum next) {
        // ACTIVE -> SUSPENDED, CLOSED
        // SUSPENDED -> ACTIVE, CLOSED
        // CLOSED -> (no transitions allowed)
        if (current == WalletAccountStatusEnum.CLOSED) {
            return false;
        }
        return true; // All other transitions are valid
    }

    private WalletAccountResponse mapToResponse(WalletAccount walletAccount) {
        return WalletAccountResponse.builder()
                .id(walletAccount.getId())
                .customerId(walletAccount.getCustomerId())
                .accountType(walletAccount.getAccountType())
                .currencyCode(walletAccount.getCurrencyCode())
                .currentBalance(walletAccount.getCurrentBalance())
                .status(walletAccount.getStatus())
                .createdAt(walletAccount.getCreatedAt())
                .updatedAt(walletAccount.getUpdatedAt())
                .closedAt(walletAccount.getClosedAt())
                .build();
    }
}
