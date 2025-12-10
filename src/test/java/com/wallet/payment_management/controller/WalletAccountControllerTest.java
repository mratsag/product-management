package com.wallet.payment_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.payment_management.dto.request.WalletAccountRequest;
import com.wallet.payment_management.dto.request.WalletAccountStatusUpdateRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletAccountResponse;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import com.wallet.payment_management.service.WalletAccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class WalletAccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WalletAccountService walletAccountService;

    @InjectMocks
    private WalletAccountController walletAccountController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(walletAccountController)
                .setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldCreateWalletAccount() throws Exception {
        // Given
        WalletAccountRequest request = WalletAccountRequest.builder()
                .customerId(12345L)
                .currencyCode("TRY")
                .build();

        WalletAccountResponse response = WalletAccountResponse.builder()
                .id(1L)
                .customerId(12345L)
                .currencyCode("TRY")
                .accountType(WalletAccountTypeEnum.STANDARD)
                .status(WalletAccountStatusEnum.ACTIVE)
                .currentBalance(BigDecimal.ZERO)
                .build();

        when(walletAccountService.createWalletAccount(any(WalletAccountRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/wallet-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(12345L))
                .andExpect(jsonPath("$.currencyCode").value("TRY"));
    }

    @Test
    void shouldGetWalletAccountById() throws Exception {
        // Given
        WalletAccountResponse response = WalletAccountResponse.builder()
                .id(1L)
                .customerId(12345L)
                .currencyCode("TRY")
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();

        when(walletAccountService.getWalletAccountById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/wallet-accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(12345L));
    }

    @Test
    void shouldGetWalletAccountsByCustomerId() throws Exception {
        // Given
        WalletAccountResponse response1 = WalletAccountResponse.builder()
                .id(1L)
                .customerId(12345L)
                .currencyCode("TRY")
                .build();

        WalletAccountResponse response2 = WalletAccountResponse.builder()
                .id(2L)
                .customerId(12345L)
                .currencyCode("USD")
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        PageResponse<WalletAccountResponse> pageResponse = PageResponse.<WalletAccountResponse>builder()
                .content(Arrays.asList(response1, response2))
                .totalElements(2)
                .totalPages(1)
                .pageNumber(0)
                .pageSize(20)
                .first(true)
                .last(true)
                .empty(false)
                .numberOfElements(2)
                .build();

        when(walletAccountService.getWalletAccountsByCustomerId(eq(12345L), any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/wallet-accounts/customer/12345")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldUpdateWalletAccountStatus() throws Exception {
        // Given
        WalletAccountStatusUpdateRequest request = WalletAccountStatusUpdateRequest.builder()
                .status(WalletAccountStatusEnum.SUSPENDED)
                .build();

        WalletAccountResponse response = WalletAccountResponse.builder()
                .id(1L)
                .status(WalletAccountStatusEnum.SUSPENDED)
                .build();

        when(walletAccountService.updateWalletAccountStatus(eq(1L), any(WalletAccountStatusUpdateRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/api/wallet-accounts/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    void shouldCheckBalance() throws Exception {
        // Given
        when(walletAccountService.checkBalance(eq(1L), any(BigDecimal.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/wallet-accounts/1/check-balance")
                        .param("requiredAmount", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given
        WalletAccountRequest request = WalletAccountRequest.builder()
                .currencyCode("TRY")
                // customerId is missing
                .build();

        // When & Then
        mockMvc.perform(post("/api/wallet-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
