import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, CreditCard, DollarSign, Receipt, Plus, Loader2 } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Skeleton } from '@/components/ui/skeleton';
import { StatCard, StatusBadge } from '@/components/common';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { usePayment, useUpdatePaymentStatus, usePaymentTransactions } from '@/hooks';
import { PaymentStatus } from '@/types';
import type { PaymentTransaction } from '@/types';
import { CreateTransactionDialog } from '@/components/payment';

export function PaymentDetail() {
    const { id } = useParams<{ id: string }>();
    const paymentId = parseInt(id || '0', 10);
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

    const { data: payment, isLoading: paymentLoading, isError } = usePayment(paymentId);
    const { data: transactionsData, isLoading: transactionsLoading } = usePaymentTransactions(paymentId);
    const updateStatus = useUpdatePaymentStatus();

    const transactions: PaymentTransaction[] = transactionsData ?? [];

    const handleStatusChange = (newStatus: string) => {
        if (payment && newStatus !== payment.status) {
            updateStatus.mutate({
                id: payment.id,
                data: { status: newStatus as PaymentStatus }
            });
        }
    };

    if (paymentLoading) {
        return (
            <div className="space-y-6">
                <div className="flex items-center gap-4">
                    <Skeleton className="h-10 w-10" />
                    <div>
                        <Skeleton className="h-8 w-48" />
                        <Skeleton className="h-4 w-32 mt-2" />
                    </div>
                </div>
                <div className="grid gap-4 md:grid-cols-4">
                    {[1, 2, 3, 4].map(i => (
                        <Card key={i}>
                            <CardContent className="pt-6">
                                <Skeleton className="h-4 w-24 mb-2" />
                                <Skeleton className="h-8 w-32" />
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>
        );
    }

    if (isError || !payment) {
        return (
            <div className="flex flex-col items-center justify-center py-12">
                <p className="text-lg text-muted-foreground mb-4">Payment not found</p>
                <Button asChild>
                    <Link to="/payments">Back to Payments</Link>
                </Button>
            </div>
        );
    }

    const progress = payment.amount > 0 ? (payment.paidAmount / payment.amount) * 100 : 0;

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Button variant="ghost" size="icon" asChild>
                        <Link to="/payments">
                            <ArrowLeft className="h-5 w-5" />
                        </Link>
                    </Button>
                    <div>
                        <div className="flex items-center gap-3">
                            <h1 className="text-3xl font-bold tracking-tight">
                                Payment #{payment.id}
                            </h1>
                            <StatusBadge status={payment.status} type="paymentStatus" />
                        </div>
                        <p className="text-muted-foreground">
                            {payment.description || `${payment.paymentType} payment`}
                        </p>
                    </div>
                </div>
                <div className="flex gap-2">
                    <Select
                        value={payment.status}
                        onValueChange={handleStatusChange}
                        disabled={updateStatus.isPending}
                    >
                        <SelectTrigger className="w-48">
                            {updateStatus.isPending ? (
                                <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                                <SelectValue />
                            )}
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value={PaymentStatus.PENDING}>Pending</SelectItem>
                            <SelectItem value={PaymentStatus.PARTIALLY_PAID}>Partially Paid</SelectItem>
                            <SelectItem value={PaymentStatus.PAID}>Paid</SelectItem>
                            <SelectItem value={PaymentStatus.CANCELLED}>Cancelled</SelectItem>
                            <SelectItem value={PaymentStatus.FAILED}>Failed</SelectItem>
                        </SelectContent>
                    </Select>
                    <Button onClick={() => setIsCreateDialogOpen(true)}>
                        <Plus className="mr-2 h-4 w-4" />
                        Add Transaction
                    </Button>
                </div>
            </div>

            {/* Stats Grid */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    title="Total Amount"
                    value={formatCurrency(payment.amount)}
                    icon={DollarSign}
                />
                <StatCard
                    title="Paid Amount"
                    value={formatCurrency(payment.paidAmount)}
                    icon={CreditCard}
                />
                <StatCard
                    title="Remaining"
                    value={formatCurrency(payment.amount - payment.paidAmount)}
                    icon={Receipt}
                />
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Progress
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{progress.toFixed(0)}%</div>
                        <div className="mt-2 h-2 w-full rounded-full bg-muted">
                            <div
                                className="h-full rounded-full bg-primary transition-all"
                                style={{ width: `${Math.min(progress, 100)}%` }}
                            />
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Tabs */}
            <Tabs defaultValue="transactions" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="transactions">Transactions</TabsTrigger>
                    <TabsTrigger value="details">Payment Details</TabsTrigger>
                </TabsList>

                <TabsContent value="transactions" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Payment Transactions</CardTitle>
                            <CardDescription>All transactions for this payment</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {transactionsLoading ? (
                                <div className="space-y-4">
                                    {[1, 2, 3].map(i => (
                                        <Skeleton key={i} className="h-16 w-full" />
                                    ))}
                                </div>
                            ) : transactions.length > 0 ? (
                                <div className="space-y-4">
                                    {transactions.map((tx: PaymentTransaction) => (
                                        <div
                                            key={tx.id}
                                            className="flex items-center justify-between rounded-lg border border-border p-4"
                                        >
                                            <div className="flex items-center gap-4">
                                                <StatusBadge status={tx.transactionType} type="transaction" />
                                                <div>
                                                    <p className="font-medium">{tx.method || 'N/A'}</p>
                                                    <p className="text-sm text-muted-foreground">
                                                        Ref: {tx.reference || tx.id}
                                                    </p>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-4">
                                                <StatusBadge status={tx.status} type="transactionStatus" />
                                                <span className="text-lg font-semibold">
                                                    {formatCurrency(tx.paidAmount)}
                                                </span>
                                                <span className="text-sm text-muted-foreground">
                                                    {formatDateTime(tx.createdAt)}
                                                </span>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-center text-muted-foreground py-8">
                                    No transactions yet
                                </p>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="details" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Payment Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid gap-4 md:grid-cols-2">
                                <div>
                                    <p className="text-sm text-muted-foreground">Payment ID</p>
                                    <p className="font-mono font-medium">#{payment.id}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Payment Type</p>
                                    <StatusBadge status={payment.paymentType} type="payment" />
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Created At</p>
                                    <p className="text-sm">{formatDateTime(payment.createdAt)}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Last Updated</p>
                                    <p className="text-sm">{formatDateTime(payment.updatedAt)}</p>
                                </div>
                                <div className="md:col-span-2">
                                    <p className="text-sm text-muted-foreground">Description</p>
                                    <p>{payment.description || '-'}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>

            {/* Create Transaction Dialog */}
            <CreateTransactionDialog
                paymentId={paymentId}
                open={isCreateDialogOpen}
                onOpenChange={setIsCreateDialogOpen}
            />
        </div>
    );
}
