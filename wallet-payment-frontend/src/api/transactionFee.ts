import apiClient from './client';
import type {
    PaymentTransactionFee,
    CreateTransactionFeeRequest,
    TotalFeesResponse,
} from '@/types';

const BASE_PATH = '/payment-transaction-fees';

export const transactionFeeApi = {
    // Create transaction fee
    create: async (data: CreateTransactionFeeRequest): Promise<PaymentTransactionFee> => {
        const response = await apiClient.post<PaymentTransactionFee>(BASE_PATH, data);
        return response.data;
    },

    // Get fees by transaction ID
    getByTransactionId: async (transactionId: number): Promise<PaymentTransactionFee[]> => {
        const response = await apiClient.get<PaymentTransactionFee[]>(
            `${BASE_PATH}/transaction/${transactionId}`
        );
        return response.data;
    },

    // Get total fees by payment ID
    getTotalByPaymentId: async (paymentId: number): Promise<TotalFeesResponse> => {
        const response = await apiClient.get<TotalFeesResponse>(
            `${BASE_PATH}/payment/${paymentId}/total`
        );
        return response.data;
    },
};

export default transactionFeeApi;
