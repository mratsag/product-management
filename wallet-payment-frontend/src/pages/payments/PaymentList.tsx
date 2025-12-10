import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Eye, MoreHorizontal, RefreshCw } from 'lucide-react';
import type { ColumnDef } from '@tanstack/react-table';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { DataTable, StatusBadge } from '@/components/common';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { usePayments, useCreatePayment } from '@/hooks';
import type { Payment, CreatePaymentRequest } from '@/types';
import { PaymentStatus, PaymentType } from '@/types';

const columns: ColumnDef<Payment>[] = [
    {
        accessorKey: 'id',
        header: 'Payment ID',
        cell: ({ row }) => (
            <span className="font-mono text-sm">#{row.getValue('id')}</span>
        ),
    },
    {
        accessorKey: 'paymentType',
        header: 'Type',
        cell: ({ row }) => (
            <StatusBadge status={row.getValue('paymentType')} type="payment" />
        ),
    },
    {
        accessorKey: 'amount',
        header: 'Amount',
        cell: ({ row }) => (
            <span className="font-semibold">{formatCurrency(row.getValue('amount'))}</span>
        ),
    },
    {
        accessorKey: 'paidAmount',
        header: 'Paid Amount',
        cell: ({ row }) => {
            const payment = row.original;
            return (
                <span className={payment.paidAmount < payment.amount ? 'text-warning' : 'text-success'}>
                    {formatCurrency(payment.paidAmount)}
                </span>
            );
        },
    },
    {
        accessorKey: 'status',
        header: 'Status',
        cell: ({ row }) => (
            <StatusBadge status={row.getValue('status')} type="paymentStatus" />
        ),
    },
    {
        accessorKey: 'createdAt',
        header: 'Created At',
        cell: ({ row }) => (
            <span className="text-sm text-muted-foreground">
                {formatDateTime(row.getValue('createdAt'))}
            </span>
        ),
    },
    {
        id: 'actions',
        header: 'Actions',
        cell: ({ row }) => (
            <div className="flex items-center gap-2">
                <Button variant="ghost" size="icon" asChild>
                    <Link to={`/payments/${row.original.id}`}>
                        <Eye className="h-4 w-4" />
                    </Link>
                </Button>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon">
                            <MoreHorizontal className="h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem asChild>
                            <Link to={`/payments/${row.original.id}`}>View Details</Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                            <Link to={`/payments/${row.original.id}/transactions`}>
                                View Transactions
                            </Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem>Update Status</DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>
        ),
    },
];

export function PaymentList() {
    const [typeFilter, setTypeFilter] = useState<string>('all');
    const [statusFilter, setStatusFilter] = useState<string>('all');
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [page, setPage] = useState(0);

    // Form state
    const [paymentType, setPaymentType] = useState<PaymentType>(PaymentType.ORDER);
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');

    // Fetch payments from API
    const { data, isLoading, isError, refetch } = usePayments({
        page,
        size: 20,
        sort: 'createdAt,desc',
        paymentType: typeFilter !== 'all' ? typeFilter as PaymentType : undefined,
        status: statusFilter !== 'all' ? statusFilter as PaymentStatus : undefined,
    });

    const createPayment = useCreatePayment();

    const payments = data?.content ?? [];
    const totalPages = data?.totalPages ?? 1;

    const handleCreatePayment = async () => {
        if (!amount) return;

        const request: CreatePaymentRequest = {
            paymentType,
            amount: parseFloat(amount),
            description: description || undefined,
        };

        try {
            await createPayment.mutateAsync(request);
            setIsCreateDialogOpen(false);
            setAmount('');
            setDescription('');
        } catch {
            // Error handled by mutation
        }
    };

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Payments</h1>
                    <p className="text-muted-foreground">Manage payment records</p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" onClick={() => refetch()}>
                        <RefreshCw className="mr-2 h-4 w-4" />
                        Refresh
                    </Button>
                    <Button onClick={() => setIsCreateDialogOpen(true)}>
                        <Plus className="mr-2 h-4 w-4" />
                        Create Payment
                    </Button>
                </div>
            </div>

            {/* Filters */}
            <Card>
                <CardHeader>
                    <CardTitle className="text-lg">Filters</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-wrap gap-4">
                        <Select value={typeFilter} onValueChange={setTypeFilter}>
                            <SelectTrigger className="w-48">
                                <SelectValue placeholder="Filter by type" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Types</SelectItem>
                                <SelectItem value={PaymentType.ORDER}>Order</SelectItem>
                                <SelectItem value={PaymentType.DEPOSIT}>Deposit</SelectItem>
                                <SelectItem value={PaymentType.WITHDRAWAL}>Withdrawal</SelectItem>
                            </SelectContent>
                        </Select>
                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-48">
                                <SelectValue placeholder="Filter by status" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Statuses</SelectItem>
                                <SelectItem value={PaymentStatus.PENDING}>Pending</SelectItem>
                                <SelectItem value={PaymentStatus.PARTIALLY_PAID}>Partially Paid</SelectItem>
                                <SelectItem value={PaymentStatus.PAID}>Paid</SelectItem>
                                <SelectItem value={PaymentStatus.CANCELLED}>Cancelled</SelectItem>
                                <SelectItem value={PaymentStatus.FAILED}>Failed</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </CardContent>
            </Card>

            {/* Error State */}
            {isError && (
                <Card className="border-destructive">
                    <CardContent className="pt-6">
                        <p className="text-center text-destructive">
                            Failed to load payments. Make sure the backend is running at http://localhost:8080
                        </p>
                        <div className="mt-4 flex justify-center">
                            <Button variant="outline" onClick={() => refetch()}>
                                Try Again
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Data Table */}
            {!isError && (
                <Card>
                    <CardContent className="pt-6">
                        <DataTable
                            columns={columns}
                            data={payments}
                            isLoading={isLoading}
                            pageCount={totalPages}
                            pageIndex={page}
                            pageSize={20}
                            onPageChange={setPage}
                        />
                    </CardContent>
                </Card>
            )}

            {/* Create Payment Dialog */}
            <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Create New Payment</DialogTitle>
                        <DialogDescription>
                            Create a new payment record.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>Payment Type</Label>
                            <Select value={paymentType} onValueChange={(v) => setPaymentType(v as PaymentType)}>
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={PaymentType.ORDER}>Order</SelectItem>
                                    <SelectItem value={PaymentType.DEPOSIT}>Deposit</SelectItem>
                                    <SelectItem value={PaymentType.WITHDRAWAL}>Withdrawal</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="space-y-2">
                            <Label>Amount</Label>
                            <Input
                                type="number"
                                placeholder="Enter amount"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Description</Label>
                            <Input
                                placeholder="Enter description (optional)"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button
                            onClick={handleCreatePayment}
                            disabled={!amount || createPayment.isPending}
                        >
                            {createPayment.isPending ? 'Creating...' : 'Create Payment'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}
