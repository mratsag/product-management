package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.WalletLedgerEntryRequest;
import com.wallet.payment_management.dto.response.WalletLedgerEntryResponse;
import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.entity.WalletLedgerEntry;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryDirectionEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import com.wallet.payment_management.exception.ClosedAccountException;
import com.wallet.payment_management.exception.InsufficientBalanceException;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.WalletAccountRepository;
import com.wallet.payment_management.repository.WalletLedgerEntryRepository;
import com.wallet.payment_management.service.impl.WalletLedgerEntryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletLedgerEntryServiceTest {

    @Mock
    private WalletLedgerEntryRepository walletLedgerEntryRepository;

    @Mock
    private WalletAccountRepository walletAccountRepository;

    @InjectMocks
    private WalletLedgerEntryServiceImpl walletLedgerEntryService;

    private WalletAccount walletAccount;
    private WalletLedgerEntryRequest loadRequest;
    private WalletLedgerEntryRequest spendRequest;

    @BeforeEach
    void setUp() {
        walletAccount = WalletAccount.builder()
                .id(1L)
                .customerId(12345L)
                .currentBalance(BigDecimal.valueOf(1000.00))
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        loadRequest = WalletLedgerEntryRequest.builder()
                .walletAccountId(1L)
                .entryType(WalletLedgerEntryTypeEnum.LOAD)
                .amount(BigDecimal.valueOf(500.00))
                .method("CARD")
                .description("Test load")
                .build();

        spendRequest = WalletLedgerEntryRequest.builder()
                .walletAccountId(1L)
                .entryType(WalletLedgerEntryTypeEnum.SPEND)
                .amount(BigDecimal.valueOf(200.00))
                .method("CARD")
                .description("Test spend")
                .build();
    }

    @Test
    void shouldCreateLoadEntry() {
        // Given
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));
        when(walletLedgerEntryRepository.save(any(WalletLedgerEntry.class))).thenAnswer(invocation -> {
            WalletLedgerEntry entry = invocation.getArgument(0);
            entry.setId(1L);
            return entry;
        });
        when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);

        // When
        WalletLedgerEntryResponse response = walletLedgerEntryService.createLoadEntry(loadRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEntryType()).isEqualTo(WalletLedgerEntryTypeEnum.LOAD);
        assertThat(response.getEntryDirection()).isEqualTo(WalletLedgerEntryDirectionEnum.CREDIT);
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        verify(walletAccountRepository, atLeastOnce()).save(any(WalletAccount.class));
    }

    @Test
    void shouldCreateSpendEntry() {
        // Given
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));
        when(walletLedgerEntryRepository.save(any(WalletLedgerEntry.class))).thenAnswer(invocation -> {
            WalletLedgerEntry entry = invocation.getArgument(0);
            entry.setId(1L);
            return entry;
        });
        when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);

        // When
        WalletLedgerEntryResponse response = walletLedgerEntryService.createSpendEntry(spendRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEntryType()).isEqualTo(WalletLedgerEntryTypeEnum.SPEND);
        assertThat(response.getEntryDirection()).isEqualTo(WalletLedgerEntryDirectionEnum.DEBIT);
        verify(walletAccountRepository, atLeastOnce()).save(any(WalletAccount.class));
    }

    @Test
    void shouldThrowExceptionWhenSpendingMoreThanBalance() {
        // Given
        spendRequest.setAmount(BigDecimal.valueOf(2000.00));
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When & Then
        assertThatThrownBy(() -> walletLedgerEntryService.createSpendEntry(spendRequest))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void shouldThrowExceptionWhenAccountIsClosed() {
        // Given
        walletAccount.setStatus(WalletAccountStatusEnum.CLOSED);
        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));

        // When & Then
        assertThatThrownBy(() -> walletLedgerEntryService.createLoadEntry(loadRequest))
                .isInstanceOf(ClosedAccountException.class);
    }

    @Test
    void shouldCreateRefundEntry() {
        // Given
        WalletLedgerEntryRequest refundRequest = WalletLedgerEntryRequest.builder()
                .walletAccountId(1L)
                .entryType(WalletLedgerEntryTypeEnum.REFUND)
                .amount(BigDecimal.valueOf(100.00))
                .method("CARD")
                .build();

        when(walletAccountRepository.findById(1L)).thenReturn(Optional.of(walletAccount));
        when(walletLedgerEntryRepository.save(any(WalletLedgerEntry.class))).thenAnswer(invocation -> {
            WalletLedgerEntry entry = invocation.getArgument(0);
            entry.setId(1L);
            return entry;
        });
        when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);

        // When
        WalletLedgerEntryResponse response = walletLedgerEntryService.createRefundEntry(refundRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEntryType()).isEqualTo(WalletLedgerEntryTypeEnum.REFUND);
        assertThat(response.getEntryDirection()).isEqualTo(WalletLedgerEntryDirectionEnum.CREDIT);
    }

    @Test
    void shouldThrowExceptionWhenWalletAccountNotFound() {
        // Given
        WalletLedgerEntryRequest requestWithInvalidId = WalletLedgerEntryRequest.builder()
                .walletAccountId(999L)
                .entryType(WalletLedgerEntryTypeEnum.LOAD)
                .amount(BigDecimal.valueOf(500.00))
                .method("CARD")
                .build();
        
        when(walletAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> walletLedgerEntryService.createLoadEntry(requestWithInvalidId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
