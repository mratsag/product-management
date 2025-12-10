import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ledgerApi, ledgerFeeApi } from '@/api';
import type { CreateLedgerEntryRequest, LedgerEntryFilters } from '@/types';
import { toast } from 'sonner';
import { walletKeys } from './useWallet';

export const ledgerKeys = {
    all: ['ledger'] as const,
    lists: () => [...ledgerKeys.all, 'list'] as const,
    list: (filters?: LedgerEntryFilters) => [...ledgerKeys.lists(), filters] as const,
    fees: (ledgerEntryId: number) => [...ledgerKeys.all, 'fees', ledgerEntryId] as const,
    customerReport: (customerId: number) => [...ledgerKeys.all, 'report', customerId] as const,
};

export function useLedgerEntries(filters?: LedgerEntryFilters) {
    return useQuery({
        queryKey: ledgerKeys.list(filters),
        queryFn: () => ledgerApi.getAll(filters),
    });
}

export function useLedgerEntryFees(ledgerEntryId: number) {
    return useQuery({
        queryKey: ledgerKeys.fees(ledgerEntryId),
        queryFn: () => ledgerFeeApi.getByLedgerEntryId(ledgerEntryId),
        enabled: !!ledgerEntryId,
    });
}

export function useCustomerFeeReport(customerId: number, startDate?: string, endDate?: string) {
    return useQuery({
        queryKey: ledgerKeys.customerReport(customerId),
        queryFn: () => ledgerFeeApi.getCustomerReport(customerId, startDate, endDate),
        enabled: !!customerId,
    });
}

export function useLoadBalance() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateLedgerEntryRequest) => ledgerApi.load(data),
        onSuccess: (entry) => {
            queryClient.invalidateQueries({ queryKey: ledgerKeys.lists() });
            queryClient.invalidateQueries({ queryKey: walletKeys.detail(entry.walletAccountId) });
            queryClient.invalidateQueries({ queryKey: walletKeys.balance(entry.walletAccountId) });
            toast.success('Balance loaded successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to load balance: ${error.message}`);
        },
    });
}

export function useSpendFromWallet() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateLedgerEntryRequest) => ledgerApi.spend(data),
        onSuccess: (entry) => {
            queryClient.invalidateQueries({ queryKey: ledgerKeys.lists() });
            queryClient.invalidateQueries({ queryKey: walletKeys.detail(entry.walletAccountId) });
            queryClient.invalidateQueries({ queryKey: walletKeys.balance(entry.walletAccountId) });
            toast.success('Spend recorded successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to spend: ${error.message}`);
        },
    });
}

export function useRefundToWallet() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateLedgerEntryRequest) => ledgerApi.refund(data),
        onSuccess: (entry) => {
            queryClient.invalidateQueries({ queryKey: ledgerKeys.lists() });
            queryClient.invalidateQueries({ queryKey: walletKeys.detail(entry.walletAccountId) });
            queryClient.invalidateQueries({ queryKey: walletKeys.balance(entry.walletAccountId) });
            toast.success('Refund processed successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to refund: ${error.message}`);
        },
    });
}

export function useAdjustment() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateLedgerEntryRequest) => ledgerApi.adjustment(data),
        onSuccess: (entry) => {
            queryClient.invalidateQueries({ queryKey: ledgerKeys.lists() });
            queryClient.invalidateQueries({ queryKey: walletKeys.detail(entry.walletAccountId) });
            queryClient.invalidateQueries({ queryKey: walletKeys.balance(entry.walletAccountId) });
            toast.success('Adjustment applied successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to apply adjustment: ${error.message}`);
        },
    });
}
