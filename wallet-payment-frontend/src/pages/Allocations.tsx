import { useState, useCallback, useMemo } from 'react';
import { Plus, Search, RefreshCw, Pencil } from 'lucide-react';
import type { ColumnDef } from '@tanstack/react-table';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { DataTable } from '@/components/common';
import { EditAllocationDialog } from '@/components/allocation';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { useAllocationsByOrder, useCreateAllocation } from '@/hooks';
import type { OrderPaymentAllocation, CreateOrderAllocationRequest } from '@/types';

const createColumns = (
    onEdit: (allocation: OrderPaymentAllocation) => void
): ColumnDef<OrderPaymentAllocation>[] => [
        {
            accessorKey: 'id',
            header: 'Allocation ID',
            cell: ({ row }) => (
                <span className="font-mono text-sm">#{row.getValue('id')}</span>
            ),
        },
        {
            accessorKey: 'orderId',
            header: 'Order ID',
            cell: ({ row }) => (
                <span className="font-mono text-sm">#{row.getValue('orderId')}</span>
            ),
        },
        {
            accessorKey: 'paymentId',
            header: 'Payment ID',
            cell: ({ row }) => (
                <span className="font-mono text-sm">#{row.getValue('paymentId')}</span>
            ),
        },
        {
            accessorKey: 'allocatedAmount',
            header: 'Allocated Amount',
            cell: ({ row }) => (
                <span className="font-semibold text-success">
                    {formatCurrency(row.getValue('allocatedAmount'))}
                </span>
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
                <Button
                    variant="outline"
                    size="sm"
                    onClick={() => onEdit(row.original)}
                >
                    <Pencil className="mr-1 h-3 w-3" />
                    Edit
                </Button>
            ),
        },
    ];

export function Allocations() {
    const [searchOrderId, setSearchOrderId] = useState('');
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
    const [selectedAllocation, setSelectedAllocation] = useState<OrderPaymentAllocation | null>(null);
    const [page, setPage] = useState(0);

    // Form state for create dialog
    const [newOrderId, setNewOrderId] = useState('');
    const [newPaymentId, setNewPaymentId] = useState('');
    const [newAmount, setNewAmount] = useState('');

    // Fetch allocations from API
    const orderId = searchOrderId ? parseInt(searchOrderId, 10) : 0;
    const { data: allocations, isLoading, isError, refetch, isFetching } = useAllocationsByOrder(orderId);
    const createAllocation = useCreateAllocation();

    const allocationsList = allocations ?? [];

    const handleRefresh = useCallback(async () => {
        await refetch();
    }, [refetch]);

    const handleSearch = () => {
        if (searchOrderId) {
            refetch();
        }
    };

    const handleEditClick = useCallback((allocation: OrderPaymentAllocation) => {
        setSelectedAllocation(allocation);
        setIsEditDialogOpen(true);
    }, []);

    const columns = useMemo(() => createColumns(handleEditClick), [handleEditClick]);

    const handleCreateAllocation = async () => {
        if (!newOrderId || !newPaymentId || !newAmount) return;

        const request: CreateOrderAllocationRequest = {
            orderId: parseInt(newOrderId, 10),
            paymentId: parseInt(newPaymentId, 10),
            allocatedAmount: parseFloat(newAmount),
        };

        try {
            await createAllocation.mutateAsync(request);
            setIsCreateDialogOpen(false);
            setNewOrderId('');
            setNewPaymentId('');
            setNewAmount('');
            // Update search to show the new allocation
            setSearchOrderId(newOrderId);
        } catch {
            // Error handled by mutation
        }
    };

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Order Allocations</h1>
                    <p className="text-muted-foreground">
                        Manage payment allocations to orders
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button
                        variant="outline"
                        onClick={handleRefresh}
                        disabled={isFetching || !searchOrderId}
                    >
                        <RefreshCw className={`mr-2 h-4 w-4 ${isFetching ? 'animate-spin' : ''}`} />
                        Refresh
                    </Button>
                    <Button onClick={() => setIsCreateDialogOpen(true)}>
                        <Plus className="mr-2 h-4 w-4" />
                        Create Allocation
                    </Button>
                </div>
            </div>

            {/* Filters */}
            <Card>
                <CardHeader>
                    <CardTitle className="text-lg">Search by Order ID</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex gap-4">
                        <div className="relative w-64">
                            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                            <Input
                                placeholder="Enter Order ID..."
                                value={searchOrderId}
                                onChange={(e) => setSearchOrderId(e.target.value)}
                                className="pl-9"
                                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                            />
                        </div>
                        <Button onClick={handleSearch} disabled={!searchOrderId}>
                            Search
                        </Button>
                    </div>
                    {searchOrderId && (
                        <p className="mt-2 text-sm text-muted-foreground">
                            Showing allocations for Order ID: {searchOrderId}
                        </p>
                    )}
                </CardContent>
            </Card>

            {/* No search yet message */}
            {!searchOrderId && (
                <Card>
                    <CardContent className="pt-6">
                        <p className="text-center text-muted-foreground py-8">
                            Enter an Order ID to search for allocations
                        </p>
                    </CardContent>
                </Card>
            )}

            {/* Error State */}
            {searchOrderId && isError && (
                <Card className="border-destructive">
                    <CardContent className="pt-6">
                        <p className="text-center text-destructive">
                            Failed to load allocations. Make sure the backend is running.
                        </p>
                        <div className="mt-4 flex justify-center">
                            <Button variant="outline" onClick={handleRefresh}>
                                Try Again
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Data Table */}
            {searchOrderId && !isError && (
                <Card>
                    <CardContent className="pt-6">
                        <DataTable
                            columns={columns}
                            data={allocationsList}
                            isLoading={isLoading || isFetching}
                            pageCount={1}
                            pageIndex={page}
                            pageSize={20}
                            onPageChange={setPage}
                        />
                    </CardContent>
                </Card>
            )}

            {/* Create Allocation Dialog */}
            <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Create New Allocation</DialogTitle>
                        <DialogDescription>
                            Allocate a payment to an order.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>Order ID</Label>
                            <Input
                                type="number"
                                placeholder="Enter order ID"
                                value={newOrderId}
                                onChange={(e) => setNewOrderId(e.target.value)}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Payment ID</Label>
                            <Input
                                type="number"
                                placeholder="Enter payment ID"
                                value={newPaymentId}
                                onChange={(e) => setNewPaymentId(e.target.value)}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Allocated Amount</Label>
                            <Input
                                type="number"
                                placeholder="Enter amount"
                                value={newAmount}
                                onChange={(e) => setNewAmount(e.target.value)}
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button
                            onClick={handleCreateAllocation}
                            disabled={!newOrderId || !newPaymentId || !newAmount || createAllocation.isPending}
                        >
                            {createAllocation.isPending ? 'Creating...' : 'Create Allocation'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Edit Allocation Dialog */}
            <EditAllocationDialog
                allocation={selectedAllocation}
                open={isEditDialogOpen}
                onOpenChange={setIsEditDialogOpen}
            />
        </div>
    );
}
