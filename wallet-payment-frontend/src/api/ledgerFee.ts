import apiClient from './client';
import type {
    WalletLedgerEntryFee,
    CreateLedgerEntryFeeRequest,
    FeeReportResponse,
} from '@/types';

const BASE_PATH = '/wallet-ledger-entry-fees';

export const ledgerFeeApi = {
    // Create fee for ledger entry
    create: async (data: CreateLedgerEntryFeeRequest): Promise<WalletLedgerEntryFee> => {
        const response = await apiClient.post<WalletLedgerEntryFee>(BASE_PATH, data);
        return response.data;
    },

    // Get fees by ledger entry ID
    getByLedgerEntryId: async (ledgerEntryId: number): Promise<WalletLedgerEntryFee[]> => {
        const response = await apiClient.get<WalletLedgerEntryFee[]>(
            `${BASE_PATH}/ledger-entry/${ledgerEntryId}`
        );
        return response.data;
    },

    // Get customer fee report
    getCustomerReport: async (
        customerId: number,
        startDate?: string,
        endDate?: string
    ): Promise<FeeReportResponse> => {
        const response = await apiClient.get<FeeReportResponse>(
            `${BASE_PATH}/customer/${customerId}/report`,
            { params: { startDate, endDate } }
        );
        return response.data;
    },
};

export default ledgerFeeApi;
