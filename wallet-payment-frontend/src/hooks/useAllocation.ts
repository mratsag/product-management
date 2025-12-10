import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { allocationApi } from '@/api';
import type { CreateOrderAllocationRequest, UpdateOrderAllocationRequest } from '@/types';
import { toast } from 'sonner';

export const allocationKeys = {
    all: ['allocations'] as const,
    byOrder: (orderId: number) => [...allocationKeys.all, 'order', orderId] as const,
};

export function useOrderAllocations(orderId: number) {
    return useQuery({
        queryKey: allocationKeys.byOrder(orderId),
        queryFn: () => allocationApi.getByOrderId(orderId),
        enabled: !!orderId,
    });
}

export function useCreateAllocation() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateOrderAllocationRequest) => allocationApi.create(data),
        onSuccess: (allocation) => {
            queryClient.invalidateQueries({ queryKey: allocationKeys.byOrder(allocation.orderId) });
            toast.success('Allocation created successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to create allocation: ${error.message}`);
        },
    });
}

// Alias for consistent naming
export const useAllocationsByOrder = useOrderAllocations;

export function useUpdateAllocation() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, orderId, data }: { id: number; orderId: number; data: UpdateOrderAllocationRequest }) =>
            allocationApi.update(id, data).then(result => ({ ...result, orderId })),
        onSuccess: (result) => {
            queryClient.invalidateQueries({ queryKey: allocationKeys.byOrder(result.orderId) });
            toast.success('Allocation updated successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to update allocation: ${error.message}`);
        },
    });
}
