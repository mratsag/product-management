import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { paymentApi } from '@/api';
import type {
    CreatePaymentRequest,
    UpdatePaymentStatusRequest,
    PaymentFilters,
    Payment,
} from '@/types';
import { toast } from 'sonner';

export const paymentKeys = {
    all: ['payments'] as const,
    lists: () => [...paymentKeys.all, 'list'] as const,
    list: (filters?: PaymentFilters) => [...paymentKeys.lists(), filters] as const,
    details: () => [...paymentKeys.all, 'detail'] as const,
    detail: (id: number) => [...paymentKeys.details(), id] as const,
};

export function usePayments(filters?: PaymentFilters) {
    return useQuery({
        queryKey: paymentKeys.list(filters),
        queryFn: () => paymentApi.getAll(filters),
    });
}

export function usePayment(id: number) {
    return useQuery({
        queryKey: paymentKeys.detail(id),
        queryFn: () => paymentApi.getById(id),
        enabled: !!id,
    });
}

export function useCreatePayment() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreatePaymentRequest) => paymentApi.create(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: paymentKeys.lists() });
            toast.success('Payment created successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to create payment: ${error.message}`);
        },
    });
}

export function useUpdatePaymentStatus() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: UpdatePaymentStatusRequest }) =>
            paymentApi.updateStatus(id, data),
        onSuccess: (updatedPayment: Payment) => {
            queryClient.invalidateQueries({ queryKey: paymentKeys.detail(updatedPayment.id) });
            queryClient.invalidateQueries({ queryKey: paymentKeys.lists() });
            toast.success('Payment status updated');
        },
        onError: (error: Error) => {
            toast.error(`Failed to update payment status: ${error.message}`);
        },
    });
}
