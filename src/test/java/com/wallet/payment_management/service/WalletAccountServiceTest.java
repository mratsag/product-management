package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.WalletAccountRequest;
import com.wallet.payment_management.dto.request.WalletAccountStatusUpdateRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletAccountResponse;
import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import com.wallet.payment_management.exception.ClosedAccountException;
import com.wallet.payment_management.exception.InvalidStatusTransitionException;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.WalletAccountRepository;
import com.wallet.payment_management.service.impl.WalletAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletAccountServiceTest {

    @Mock
    private WalletAccountRepository walletAccountRepository;

    @InjectMocks
    private WalletAccountServiceImpl walletAccountService;

    private WalletAccount walletAccount;
    private WalletAccountRequest walletAccountRequest;

    @BeforeEach
    void setUp() {
        walletAccount = WalletAccount.builder()
                .id(1L)
                .customerId(12345L)
                .accountType(WalletAccountTypeEnum.STANDARD)
                .currencyCode("TRY")
                .currentBalance(BigDecimal.ZERO)
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        walletAccountRequest = WalletAccountRequest.builder()
                .customerId(12345L)
                .currencyCode("TRY")
                .accountType(WalletAccountTypeEnum.STANDARD)
                .build();
    }

    @Test
    void shouldCreateWalletAccount() {
        // Given
        when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);

        // When
        WalletAccountResponse response = walletAccountService.createWalletAccount(walletAccountRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(12345L);
        assertThat(response.getCurrencyCode()).isEqualTo("TRY");
        assertThat(response.getStatus()).isEqualTo(WalletAccountStatusEnum.ACTIVE);
        verify(walletAccountRepository, times(1)).save(any(WalletAccount.class));
    }

    @Test
    void shouldGetWalletAccountById() {
        // Given
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When
        WalletAccountResponse response = walletAccountService.getWalletAccountById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(12345L);
    }

    @Test
    void shouldThrowExceptionWhenWalletAccountNotFound() {
        // Given
        when(walletAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> walletAccountService.getWalletAccountById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("WalletAccount not found");
    }

    @Test
    void shouldGetWalletAccountsByCustomerId() {
        // Given
        WalletAccount account2 = WalletAccount.builder()
                .id(2L)
                .customerId(12345L)
                .currencyCode("USD")
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<WalletAccount> page = new PageImpl<>(Arrays.asList(walletAccount, account2), pageable, 2);

        when(walletAccountRepository.findByCustomerId(eq(12345L), any(Pageable.class)))
                .thenReturn(page);

        // When
        PageResponse<WalletAccountResponse> response = walletAccountService.getWalletAccountsByCustomerId(12345L, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent().get(0).getCustomerId()).isEqualTo(12345L);
    }

    @Test
    void shouldUpdateWalletAccountStatus() {
        // Given
        WalletAccountStatusUpdateRequest updateRequest = WalletAccountStatusUpdateRequest.builder()
                .status(WalletAccountStatusEnum.SUSPENDED)
                .build();

        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));
        when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);

        // When
        WalletAccountResponse response = walletAccountService.updateWalletAccountStatus(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(walletAccountRepository, times(1)).save(any(WalletAccount.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingClosedAccount() {
        // Given
        walletAccount.setStatus(WalletAccountStatusEnum.CLOSED);
        WalletAccountStatusUpdateRequest updateRequest = WalletAccountStatusUpdateRequest.builder()
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When & Then
        assertThatThrownBy(() -> walletAccountService.updateWalletAccountStatus(1L, updateRequest))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void shouldCheckBalance() {
        // Given
        walletAccount.setCurrentBalance(BigDecimal.valueOf(1000.00));
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When
        boolean hasBalance = walletAccountService.checkBalance(1L, BigDecimal.valueOf(500.00));

        // Then
        assertThat(hasBalance).isTrue();
    }

    @Test
    void shouldReturnFalseWhenInsufficientBalance() {
        // Given
        walletAccount.setCurrentBalance(BigDecimal.valueOf(100.00));
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When
        boolean hasBalance = walletAccountService.checkBalance(1L, BigDecimal.valueOf(500.00));

        // Then
        assertThat(hasBalance).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenCheckingBalanceForClosedAccount() {
        // Given
        walletAccount.setStatus(WalletAccountStatusEnum.CLOSED);
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When & Then
        assertThatThrownBy(() -> walletAccountService.checkBalance(1L, BigDecimal.valueOf(100.00)))
                .isInstanceOf(ClosedAccountException.class);
    }
}
