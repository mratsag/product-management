// ==================== ENUMS ====================

export enum WalletAccountStatus {
    ACTIVE = 'ACTIVE',
    SUSPENDED = 'SUSPENDED',
    CLOSED = 'CLOSED',
}

export enum WalletAccountType {
    STANDARD = 'STANDARD',
    PREMIUM = 'PREMIUM',
}

export enum LedgerEntryType {
    LOAD = 'LOAD',
    SPEND = 'SPEND',
    REFUND = 'REFUND',
    ADJUSTMENT = 'ADJUSTMENT',
}

export enum LedgerEntryDirection {
    DEBIT = 'DEBIT',
    CREDIT = 'CREDIT',
}

export enum LedgerEntryStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
    CANCELLED = 'CANCELLED',
}

export enum PaymentType {
    ORDER = 'ORDER',
    DEPOSIT = 'DEPOSIT',
    WITHDRAWAL = 'WITHDRAWAL',
}

export enum PaymentStatus {
    PENDING = 'PENDING',
    PARTIALLY_PAID = 'PARTIALLY_PAID',
    PAID = 'PAID',
    CANCELLED = 'CANCELLED',
    FAILED = 'FAILED',
}

export enum TransactionType {
    AUTH = 'AUTH',
    CAPTURE = 'CAPTURE',
    REFUND = 'REFUND',
    VOID = 'VOID',
}

export enum TransactionStatus {
    PENDING = 'PENDING',
    SUCCESS = 'SUCCESS',
    FAILED = 'FAILED',
}

export enum WalletFeeType {
    LOAD_FEE = 'LOAD_FEE',
    SPEND_FEE = 'SPEND_FEE',
    REFUND_FEE = 'REFUND_FEE',
    ADJUSTMENT_FEE = 'ADJUSTMENT_FEE',
}

export enum PaymentFeeType {
    TRANSACTION_FEE = 'TRANSACTION_FEE',
    PROCESSING_FEE = 'PROCESSING_FEE',
    SERVICE_FEE = 'SERVICE_FEE',
}

// ==================== ENTITIES ====================

export interface WalletAccount {
    id: number;
    customerId: number;
    accountType: WalletAccountType;
    currencyCode: string;
    currentBalance: number;
    status: WalletAccountStatus;
    createdAt: string;
    updatedAt: string;
    closedAt?: string;
}

export interface WalletLedgerEntry {
    id: number;
    walletAccountId: number;
    entryType: LedgerEntryType;
    entryDirection: LedgerEntryDirection;
    amount: number;
    balanceAfter: number;
    status: LedgerEntryStatus;
    method: string;
    reference?: string;
    description?: string;
    createdAt: string;
    updatedAt: string;
}

export interface WalletLedgerEntryFee {
    id: number;
    ledgerEntryId: number;
    feeType: WalletFeeType;
    amount: number;
    description?: string;
    createdAt: string;
}

export interface Payment {
    id: number;
    paymentType: PaymentType;
    amount: number;
    paidAmount: number;
    status: PaymentStatus;
    description?: string;
    createdAt: string;
    updatedAt: string;
}

export interface PaymentTransaction {
    id: number;
    paymentId: number;
    transactionType: TransactionType;
    paidAmount: number;
    method: string;
    status: TransactionStatus;
    reference?: string;
    createdAt: string;
    updatedAt: string;
}

export interface PaymentTransactionFee {
    id: number;
    transactionId: number;
    feeType: PaymentFeeType;
    amount: number;
    description?: string;
    createdAt: string;
}

export interface OrderPaymentAllocation {
    id: number;
    orderId: number;
    paymentId: number;
    allocatedAmount: number;
    createdAt: string;
    updatedAt: string;
}

// ==================== REQUEST DTOs ====================

export interface CreateWalletAccountRequest {
    customerId: number;
    currencyCode: string;
    accountType?: WalletAccountType;
}

export interface UpdateWalletStatusRequest {
    status: WalletAccountStatus;
}

export interface CreateLedgerEntryRequest {
    walletAccountId: number;
    entryType: LedgerEntryType;
    amount: number;
    method: string;
    reference?: string;
    description?: string;
}

export interface CreateLedgerEntryFeeRequest {
    ledgerEntryId: number;
    feeType: WalletFeeType;
    amount: number;
    description?: string;
}

export interface CreatePaymentRequest {
    paymentType: PaymentType;
    amount: number;
    description?: string;
}

export interface UpdatePaymentStatusRequest {
    status: PaymentStatus;
}

export interface CreatePaymentTransactionRequest {
    paymentId: number;
    transactionType: TransactionType;
    paidAmount: number;
    method: string;
    reference?: string;
}

export interface UpdateTransactionStatusRequest {
    status: TransactionStatus;
}

export interface CorrelateTransactionRequest {
    walletAccountId: number;
}

export interface CreateTransactionFeeRequest {
    transactionId: number;
    feeType: PaymentFeeType;
    amount: number;
    description?: string;
}

export interface CreateOrderAllocationRequest {
    orderId: number;
    paymentId: number;
    allocatedAmount: number;
}

export interface UpdateOrderAllocationRequest {
    allocatedAmount: number;
}

// ==================== PAGINATION ====================

export interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    pageNumber: number;
    pageSize: number;
    first: boolean;
    last: boolean;
    empty: boolean;
    numberOfElements: number;
}

export interface PaginationParams {
    page?: number;
    size?: number;
    sort?: string;
}

// ==================== FILTER PARAMS ====================

export interface WalletAccountFilters extends PaginationParams {
    customerId?: number;
    status?: WalletAccountStatus;
}

export interface LedgerEntryFilters extends PaginationParams {
    walletAccountId?: number;
    entryType?: LedgerEntryType;
    status?: LedgerEntryStatus;
    startDate?: string;
    endDate?: string;
}

export interface PaymentFilters extends PaginationParams {
    paymentType?: PaymentType;
    status?: PaymentStatus;
    startDate?: string;
    endDate?: string;
}

// ==================== API RESPONSES ====================

export interface BalanceCheckResponse {
    walletAccountId: number;
    currentBalance: number;
    currencyCode: string;
    status: WalletAccountStatus;
}

export interface FeeReportResponse {
    customerId: number;
    totalFees: number;
    feeCount: number;
    fees: WalletLedgerEntryFee[];
}

export interface TotalFeesResponse {
    paymentId: number;
    totalFees: number;
    feeCount: number;
}

// ==================== ERROR RESPONSE ====================

export interface ApiError {
    message: string;
    error: string;
    status: number;
    timestamp: string;
    path: string;
}
