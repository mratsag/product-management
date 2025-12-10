import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transactionApi, transactionFeeApi } from '@/api';
import type {
    CreatePaymentTransactionRequest,
    UpdateTransactionStatusRequest,
    CorrelateTransactionRequest,
} from '@/types';
import { toast } from 'sonner';
import { paymentKeys } from './usePayment';

export const transactionKeys = {
    all: ['transactions'] as const,
    byPayment: (paymentId: number) => [...transactionKeys.all, 'payment', paymentId] as const,
    fees: (transactionId: number) => [...transactionKeys.all, 'fees', transactionId] as const,
    totalFees: (paymentId: number) => [...transactionKeys.all, 'totalFees', paymentId] as const,
};

export function usePaymentTransactions(paymentId: number) {
    return useQuery({
        queryKey: transactionKeys.byPayment(paymentId),
        queryFn: () => transactionApi.getByPaymentId(paymentId),
        enabled: !!paymentId,
    });
}

// Alias for consistent naming
export const useTransactionsByPayment = usePaymentTransactions;

export function useTransactionFees(transactionId: number) {
    return useQuery({
        queryKey: transactionKeys.fees(transactionId),
        queryFn: () => transactionFeeApi.getByTransactionId(transactionId),
        enabled: !!transactionId,
    });
}

export function usePaymentTotalFees(paymentId: number) {
    return useQuery({
        queryKey: transactionKeys.totalFees(paymentId),
        queryFn: () => transactionFeeApi.getTotalByPaymentId(paymentId),
        enabled: !!paymentId,
    });
}

export function useCreateTransaction() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreatePaymentTransactionRequest) => transactionApi.create(data),
        onSuccess: (transaction) => {
            queryClient.invalidateQueries({ queryKey: transactionKeys.byPayment(transaction.paymentId) });
            queryClient.invalidateQueries({ queryKey: paymentKeys.detail(transaction.paymentId) });
            toast.success('Transaction created successfully');
        },
        onError: (error: Error) => {
            toast.error(`Failed to create transaction: ${error.message}`);
        },
    });
}

export function useUpdateTransactionStatus() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, paymentId, data }: { id: number; paymentId: number; data: UpdateTransactionStatusRequest }) =>
            transactionApi.updateStatus(id, data).then(result => ({ ...result, paymentId })),
        onSuccess: (result) => {
            queryClient.invalidateQueries({ queryKey: transactionKeys.byPayment(result.paymentId) });
            queryClient.invalidateQueries({ queryKey: paymentKeys.detail(result.paymentId) });
            toast.success('Transaction status updated');
        },
        onError: (error: Error) => {
            toast.error(`Failed to update transaction status: ${error.message}`);
        },
    });
}

export function useCorrelateTransaction() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, paymentId, data }: { id: number; paymentId: number; data: CorrelateTransactionRequest }) =>
            transactionApi.correlate(id, data).then(result => ({ ...result, paymentId })),
        onSuccess: (result) => {
            queryClient.invalidateQueries({ queryKey: transactionKeys.byPayment(result.paymentId) });
            toast.success('Transaction correlated with wallet');
        },
        onError: (error: Error) => {
            toast.error(`Failed to correlate transaction: ${error.message}`);
        },
    });
}
