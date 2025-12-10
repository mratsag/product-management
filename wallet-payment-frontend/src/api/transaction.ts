import apiClient from './client';
import type {
    PaymentTransaction,
    CreatePaymentTransactionRequest,
    UpdateTransactionStatusRequest,
    CorrelateTransactionRequest,
} from '@/types';

const BASE_PATH = '/payment-transactions';

export const transactionApi = {
    // Create transaction
    create: async (data: CreatePaymentTransactionRequest): Promise<PaymentTransaction> => {
        const response = await apiClient.post<PaymentTransaction>(BASE_PATH, data);
        return response.data;
    },

    // Get transactions by payment ID
    getByPaymentId: async (paymentId: number): Promise<PaymentTransaction[]> => {
        const response = await apiClient.get<PaymentTransaction[]>(
            `${BASE_PATH}/payment/${paymentId}`
        );
        return response.data;
    },

    // Update transaction status
    updateStatus: async (
        id: number,
        data: UpdateTransactionStatusRequest
    ): Promise<PaymentTransaction> => {
        const response = await apiClient.patch<PaymentTransaction>(
            `${BASE_PATH}/${id}/status`,
            data
        );
        return response.data;
    },

    // Correlate transaction with wallet
    correlate: async (
        id: number,
        data: CorrelateTransactionRequest
    ): Promise<PaymentTransaction> => {
        const response = await apiClient.post<PaymentTransaction>(
            `${BASE_PATH}/${id}/correlate`,
            data
        );
        return response.data;
    },
};

export default transactionApi;
