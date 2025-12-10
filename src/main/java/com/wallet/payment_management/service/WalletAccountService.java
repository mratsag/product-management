package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.WalletAccountRequest;
import com.wallet.payment_management.dto.request.WalletAccountStatusUpdateRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletAccountResponse;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WalletAccountService {

    WalletAccountResponse createWalletAccount(WalletAccountRequest request); // WA-01

    PageResponse<WalletAccountResponse> getWalletAccountsByCustomerId(Long customerId, Pageable pageable); // WA-02

    WalletAccountResponse getWalletAccountById(Long id); // WA-03

    WalletAccountResponse updateWalletAccountStatus(Long id, WalletAccountStatusUpdateRequest request); // WA-04

    boolean checkBalance(Long walletAccountId, java.math.BigDecimal requiredAmount); // WA-05
}
