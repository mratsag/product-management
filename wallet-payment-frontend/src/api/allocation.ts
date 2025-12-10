import apiClient from './client';
import type {
    OrderPaymentAllocation,
    CreateOrderAllocationRequest,
    UpdateOrderAllocationRequest,
} from '@/types';

const BASE_PATH = '/order-payment-allocations';

export const allocationApi = {
    // Create allocation
    create: async (data: CreateOrderAllocationRequest): Promise<OrderPaymentAllocation> => {
        const response = await apiClient.post<OrderPaymentAllocation>(BASE_PATH, data);
        return response.data;
    },

    // Get allocations by order ID
    getByOrderId: async (orderId: number): Promise<OrderPaymentAllocation[]> => {
        const response = await apiClient.get<OrderPaymentAllocation[]>(
            `${BASE_PATH}/order/${orderId}`
        );
        return response.data;
    },

    // Update allocation
    update: async (
        id: number,
        data: UpdateOrderAllocationRequest
    ): Promise<OrderPaymentAllocation> => {
        const response = await apiClient.put<OrderPaymentAllocation>(
            `${BASE_PATH}/${id}`,
            data
        );
        return response.data;
    },
};

export default allocationApi;
