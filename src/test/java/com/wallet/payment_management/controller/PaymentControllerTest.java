package com.wallet.payment_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.payment_management.dto.request.PaymentRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.PaymentResponse;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import com.wallet.payment_management.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldCreatePayment() throws Exception {
        // Given
        PaymentRequest request = PaymentRequest.builder()
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .description("Test payment")
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .paidAmount(BigDecimal.ZERO)
                .status(PaymentStatusEnum.PENDING)
                .build();

        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentType").value("ORDER"))
                .andExpect(jsonPath("$.amount").value(250.00));
    }

    @Test
    void shouldGetPaymentById() throws Exception {
        // Given
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .status(PaymentStatusEnum.PENDING)
                .build();

        when(paymentService.getPaymentById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentType").value("ORDER"));
    }

    @Test
    void shouldGetPayments() throws Exception {
        // Given
        PaymentResponse response1 = PaymentResponse.builder()
                .id(1L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .build();

        PaymentResponse response2 = PaymentResponse.builder()
                .id(2L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(100.00))
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        PageResponse<PaymentResponse> pageResponse = PageResponse.<PaymentResponse>builder()
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

        when(paymentService.getPayments(any(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/payments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldUpdatePaymentStatus() throws Exception {
        // Given
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .status(PaymentStatusEnum.PAID)
                .build();

        when(paymentService.updatePaymentStatus(eq(1L), eq(PaymentStatusEnum.PAID)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/api/payments/1/status")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
