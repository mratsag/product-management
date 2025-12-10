import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { walletApi } from '@/api';
import type {
    WalletAccount,
    CreateWalletAccountRequest,
    UpdateWalletStatusRequest,
    WalletAccountFilters,
} from '@/types';
import { toast } from 'sonner';

export const walletKeys = {
    all: ['wallets'] as const,
    lists: () => [...walletKeys.all, 'list'] as const,
    list: (customerId: number, filters?: WalletAccountFilters) =>
        [...walletKeys.lists(), customerId, filters] as const,
    details: () => [...walletKeys.all, 'detail'] as const,
    detail: (id: number) => [...walletKeys.details(), id] as const,
    balance: (id: number) => [...walletKeys.all, 'balance', id] as const,
};

export function useWalletsByCustomer(customerId: number, filters?: WalletAccountFilters) {
    return useQuery({
        queryKey: walletKeys.list(customerId, filters),
        queryFn: () => walletApi.getByCustomerId(customerId, filters),
        enabled: !!customerId,
    });
}

export function useWallet(id: number) {
    return useQuery({
        queryKey: walletKeys.detail(id),
        queryFn: () => walletApi.getById(id),
        enabled: !!id,
    });
}

export function useWalletBalance(id: number) {
    return useQuery({
        queryKey: walletKeys.balance(id),
        queryFn: () => walletApi.checkBalance(id),
        enabled: !!id,
    });
}

export function useCreateWallet() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateWalletAccountRequest) => walletApi.create(data),
        onSuccess: (newWallet: WalletAccount) => {
            queryClient.invalidateQueries({ queryKey: walletKeys.lists() });
            toast.success('Wallet created successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to create wallet: ${error.message}`);
        },
    });
}

export function useUpdateWalletStatus() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: UpdateWalletStatusRequest }) =>
            walletApi.updateStatus(id, data),
        onSuccess: (updatedWallet: WalletAccount) => {
            queryClient.invalidateQueries({ queryKey: walletKeys.detail(updatedWallet.id) });
            queryClient.invalidateQueries({ queryKey: walletKeys.lists() });
            toast.success('Wallet status updated');
        },
        onError: (error: Error) => {
            toast.error(`Failed to update status: ${error.message}`);
        },
    });
}

export function useAllWallets(params?: WalletAccountFilters) {
    return useQuery({
        queryKey: [...walletKeys.all, 'all', params],
        queryFn: () => walletApi.getAll(params),
    });
}

