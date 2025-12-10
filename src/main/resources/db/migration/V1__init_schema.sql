-- Wallet Accounts Table
CREATE TABLE wallet_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    currency_code CHAR(3) NOT NULL,
    current_balance DECIMAL(18,4) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    closed_at DATETIME,
    INDEX idx_wallet_accounts_customer_id (customer_id),
    INDEX idx_wallet_accounts_status (status),
    INDEX idx_wallet_accounts_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Wallet Ledger Entries Table
CREATE TABLE wallet_ledger_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_account_id BIGINT NOT NULL,
    entry_type VARCHAR(50) NOT NULL,
    entry_direction ENUM('DEBIT', 'CREDIT') NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    balance_after DECIMAL(18,4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    method VARCHAR(50) NOT NULL,
    reference VARCHAR(100),
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_wallet_ledger_entries_wallet_account_id (wallet_account_id),
    INDEX idx_wallet_ledger_entries_status (status),
    INDEX idx_wallet_ledger_entries_created_at (created_at DESC),
    FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Wallet Ledger Entry Fees Table
CREATE TABLE wallet_ledger_entry_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_ledger_entry_id BIGINT NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (wallet_ledger_entry_id) REFERENCES wallet_ledger_entries(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments Table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_type VARCHAR(50) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    paid_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_payments_status (status),
    INDEX idx_payments_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment Transactions Table
CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    paid_amount DECIMAL(18,4) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reference VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_payment_transactions_payment_id (payment_id),
    INDEX idx_payment_transactions_status (status),
    INDEX idx_payment_transactions_created_at (created_at DESC),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment Transaction Fees Table
CREATE TABLE payment_transaction_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_transaction_id BIGINT NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order Payment Allocations Table
CREATE TABLE order_payment_allocations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    allocated_amount DECIMAL(18,4) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_order_payment_allocations_order_id (order_id),
    INDEX idx_order_payment_allocations_payment_id (payment_id),
    INDEX idx_order_payment_allocations_created_at (created_at DESC),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
