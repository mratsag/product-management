import apiClient from './client';
import type {
    WalletAccount,
    CreateWalletAccountRequest,
    UpdateWalletStatusRequest,
    PaginatedResponse,
    WalletAccountFilters,
    BalanceCheckResponse,
    PaginationParams,
} from '@/types';

const BASE_PATH = '/wallet-accounts';

export const walletApi = {
    // Create wallet account
    create: async (data: CreateWalletAccountRequest): Promise<WalletAccount> => {
        const response = await apiClient.post<WalletAccount>(BASE_PATH, data);
        return response.data;
    },

    // Get wallet accounts by customer ID (paginated)
    getByCustomerId: async (
        customerId: number,
        params?: WalletAccountFilters
    ): Promise<PaginatedResponse<WalletAccount>> => {
        const response = await apiClient.get<PaginatedResponse<WalletAccount>>(
            `${BASE_PATH}/customer/${customerId}`,
            { params }
        );
        return response.data;
    },

    // Get wallet account by ID
    getById: async (id: number): Promise<WalletAccount> => {
        const response = await apiClient.get<WalletAccount>(`${BASE_PATH}/${id}`);
        return response.data;
    },

    // Update wallet account status
    updateStatus: async (
        id: number,
        data: UpdateWalletStatusRequest
    ): Promise<WalletAccount> => {
        const response = await apiClient.patch<WalletAccount>(
            `${BASE_PATH}/${id}/status`,
            data
        );
        return response.data;
    },

    // Check wallet balance
    checkBalance: async (id: number): Promise<BalanceCheckResponse> => {
        const response = await apiClient.get<BalanceCheckResponse>(
            `${BASE_PATH}/${id}/check-balance`
        );
        return response.data;
    },

    // Get all wallet accounts (no customer filter)
    getAll: async (params?: PaginationParams): Promise<PaginatedResponse<WalletAccount>> => {
        const response = await apiClient.get<PaginatedResponse<WalletAccount>>(
            BASE_PATH,
            { params }
        );
        return response.data;
    },
};

export default walletApi;

