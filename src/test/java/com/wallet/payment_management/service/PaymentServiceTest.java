package com.wallet.payment_management.service;

import com.wallet.payment_management.dto.request.PaymentRequest;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.PaymentResponse;
import com.wallet.payment_management.entity.Payment;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import com.wallet.payment_management.exception.ResourceNotFoundException;
import com.wallet.payment_management.repository.PaymentRepository;
import com.wallet.payment_management.service.impl.PaymentServiceImpl;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .paidAmount(BigDecimal.ZERO)
                .status(PaymentStatusEnum.PENDING)
                .build();

        paymentRequest = PaymentRequest.builder()
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(250.00))
                .description("Test payment")
                .build();
    }

    @Test
    void shouldCreatePayment() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentType()).isEqualTo(PaymentTypeEnum.ORDER);
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
        assertThat(response.getStatus()).isEqualTo(PaymentStatusEnum.PENDING);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldGetPaymentById() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When
        PaymentResponse response = paymentService.getPaymentById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPaymentType()).isEqualTo(PaymentTypeEnum.ORDER);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        // Given
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdatePaymentStatus() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        PaymentResponse response = paymentService.updatePaymentStatus(1L, PaymentStatusEnum.PAID);

        // Then
        assertThat(response).isNotNull();
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldGetPaymentsByType() {
        // Given
        Payment payment2 = Payment.builder()
                .id(2L)
                .paymentType(PaymentTypeEnum.ORDER)
                .amount(BigDecimal.valueOf(100.00))
                .status(PaymentStatusEnum.PENDING)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> page = new PageImpl<>(Arrays.asList(payment, payment2), pageable, 2);

        when(paymentRepository.findByPaymentType(eq(PaymentTypeEnum.ORDER), any(Pageable.class)))
                .thenReturn(page);

        // When
        PageResponse<PaymentResponse> response = paymentService.getPayments(
                PaymentTypeEnum.ORDER, null, null, null, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getPaymentType()).isEqualTo(PaymentTypeEnum.ORDER);
    }

    @Test
    void shouldGetPaymentsByStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> page = new PageImpl<>(Arrays.asList(payment), pageable, 1);

        when(paymentRepository.findByStatus(eq(PaymentStatusEnum.PENDING), any(Pageable.class)))
                .thenReturn(page);

        // When
        PageResponse<PaymentResponse> response = paymentService.getPayments(
                null, PaymentStatusEnum.PENDING, null, null, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getStatus()).isEqualTo(PaymentStatusEnum.PENDING);
    }
}
