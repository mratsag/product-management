import apiClient from './client';
import type {
    WalletLedgerEntry,
    CreateLedgerEntryRequest,
    PaginatedResponse,
    LedgerEntryFilters,
} from '@/types';

const BASE_PATH = '/wallet-ledger-entries';

export const ledgerApi = {
    // Load balance to wallet
    load: async (data: CreateLedgerEntryRequest): Promise<WalletLedgerEntry> => {
        const response = await apiClient.post<WalletLedgerEntry>(`${BASE_PATH}/load`, data);
        return response.data;
    },

    // Spend from wallet
    spend: async (data: CreateLedgerEntryRequest): Promise<WalletLedgerEntry> => {
        const response = await apiClient.post<WalletLedgerEntry>(`${BASE_PATH}/spend`, data);
        return response.data;
    },

    // Refund to wallet
    refund: async (data: CreateLedgerEntryRequest): Promise<WalletLedgerEntry> => {
        const response = await apiClient.post<WalletLedgerEntry>(`${BASE_PATH}/refund`, data);
        return response.data;
    },

    // Manual adjustment
    adjustment: async (data: CreateLedgerEntryRequest): Promise<WalletLedgerEntry> => {
        const response = await apiClient.post<WalletLedgerEntry>(`${BASE_PATH}/adjustment`, data);
        return response.data;
    },

    // Get ledger entries (paginated, filterable)
    getAll: async (params?: LedgerEntryFilters): Promise<PaginatedResponse<WalletLedgerEntry>> => {
        const response = await apiClient.get<PaginatedResponse<WalletLedgerEntry>>(BASE_PATH, { params });
        return response.data;
    },
};

export default ledgerApi;
