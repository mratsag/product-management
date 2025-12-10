package com.wallet.payment_management.repository;

import com.wallet.payment_management.entity.WalletAccount;
import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WalletAccountRepositoryTest {

    @Autowired
    private WalletAccountRepository walletAccountRepository;

    private WalletAccount walletAccount;

    @BeforeEach
    void setUp() {
        walletAccount = WalletAccount.builder()
                .customerId(12345L)
                .accountType(WalletAccountTypeEnum.STANDARD)
                .currencyCode("TRY")
                .currentBalance(BigDecimal.valueOf(1000.00))
                .status(WalletAccountStatusEnum.ACTIVE)
                .build();
    }

    @Test
    void shouldSaveWalletAccount() {
        WalletAccount saved = walletAccountRepository.save(walletAccount);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomerId()).isEqualTo(12345L);
        assertThat(saved.getCurrencyCode()).isEqualTo("TRY");
    }

    @Test
    void shouldFindByCustomerId() {
        walletAccountRepository.save(walletAccount);

        List<WalletAccount> accounts = walletAccountRepository.findByCustomerId(12345L);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getCustomerId()).isEqualTo(12345L);
    }

    @Test
    void shouldFindByStatus() {
        walletAccountRepository.save(walletAccount);

        List<WalletAccount> accounts = walletAccountRepository.findByStatus(WalletAccountStatusEnum.ACTIVE);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getStatus()).isEqualTo(WalletAccountStatusEnum.ACTIVE);
    }

    @Test
    void shouldFindByCustomerIdAndStatus() {
        walletAccountRepository.save(walletAccount);

        List<WalletAccount> accounts = walletAccountRepository.findByCustomerIdAndStatus(
                12345L, WalletAccountStatusEnum.ACTIVE);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getCustomerId()).isEqualTo(12345L);
        assertThat(accounts.get(0).getStatus()).isEqualTo(WalletAccountStatusEnum.ACTIVE);
    }
}
