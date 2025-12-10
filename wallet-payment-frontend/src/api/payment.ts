import apiClient from './client';
import type {
    Payment,
    CreatePaymentRequest,
    UpdatePaymentStatusRequest,
    PaginatedResponse,
    PaymentFilters,
} from '@/types';

const BASE_PATH = '/payments';

export const paymentApi = {
    // Create payment
    create: async (data: CreatePaymentRequest): Promise<Payment> => {
        const response = await apiClient.post<Payment>(BASE_PATH, data);
        return response.data;
    },

    // Get all payments (paginated, filterable)
    getAll: async (params?: PaymentFilters): Promise<PaginatedResponse<Payment>> => {
        const response = await apiClient.get<PaginatedResponse<Payment>>(BASE_PATH, { params });
        return response.data;
    },

    // Get payment by ID
    getById: async (id: number): Promise<Payment> => {
        const response = await apiClient.get<Payment>(`${BASE_PATH}/${id}`);
        return response.data;
    },

    // Update payment status
    updateStatus: async (id: number, data: UpdatePaymentStatusRequest): Promise<Payment> => {
        const response = await apiClient.patch<Payment>(`${BASE_PATH}/${id}/status`, data);
        return response.data;
    },
};

export default paymentApi;
