import { useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Search, Eye, MoreHorizontal, RefreshCw, Users, User } from 'lucide-react';
import type { ColumnDef } from '@tanstack/react-table';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
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
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { DataTable, StatusBadge } from '@/components/common';
import { CreateWalletDialog } from '@/components/wallet/CreateWalletDialog';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { useWalletsByCustomer, useAllWallets } from '@/hooks';
import type { WalletAccount } from '@/types';
import { WalletAccountStatus } from '@/types';

const columns: ColumnDef<WalletAccount>[] = [
    {
        accessorKey: 'id',
        header: 'Wallet ID',
        cell: ({ row }) => (
            <span className="font-mono text-sm">#{row.getValue('id')}</span>
        ),
    },
    {
        accessorKey: 'customerId',
        header: 'Customer ID',
        cell: ({ row }) => (
            <span className="font-mono text-sm">{row.getValue('customerId')}</span>
        ),
    },
    {
        accessorKey: 'currencyCode',
        header: 'Currency',
        cell: ({ row }) => (
            <span className="font-medium">{row.getValue('currencyCode')}</span>
        ),
    },
    {
        accessorKey: 'currentBalance',
        header: 'Balance',
        cell: ({ row }) => {
            const wallet = row.original;
            return (
                <span className="font-semibold">
                    {formatCurrency(wallet.currentBalance, wallet.currencyCode)}
                </span>
            );
        },
    },
    {
        accessorKey: 'status',
        header: 'Status',
        cell: ({ row }) => (
            <StatusBadge status={row.getValue('status')} type="wallet" />
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
                    <Link to={`/wallets/${row.original.id}`}>
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
                            <Link to={`/wallets/${row.original.id}`}>View Details</Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                            <Link to={`/wallets/${row.original.id}/transactions`}>
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

export function WalletList() {
    const [viewMode, setViewMode] = useState<'customer' | 'all'>('all');
    const [searchCustomerId, setSearchCustomerId] = useState('');
    const [statusFilter, setStatusFilter] = useState<string>('all');
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [page, setPage] = useState(0);
    const [isRefreshing, setIsRefreshing] = useState(false);

    // Fetch wallets from API - by customer ID
    const customerId = searchCustomerId ? parseInt(searchCustomerId, 10) : 0;
    const customerWalletsQuery = useWalletsByCustomer(
        customerId,
        {
            page,
            size: 20,
            sort: 'createdAt,desc',
            status: statusFilter !== 'all' ? statusFilter as WalletAccountStatus : undefined,
        }
    );

    // Fetch all wallets
    const allWalletsQuery = useAllWallets({
        page,
        size: 20,
        sort: 'createdAt,desc',
    });

    // Select the appropriate query based on view mode
    const isAllMode = viewMode === 'all';
    const activeQuery = isAllMode ? allWalletsQuery : customerWalletsQuery;
    const { data, isLoading, isError, isFetching } = activeQuery;

    const wallets = data?.content ?? [];
    const totalPages = data?.totalPages ?? 1;

    // Handle manual refresh
    const handleRefresh = useCallback(async () => {
        setIsRefreshing(true);
        await activeQuery.refetch();
        setIsRefreshing(false);
    }, [activeQuery]);

    // When a wallet is created, update the search to show that customer's wallets
    const handleWalletCreated = useCallback((newWallet: WalletAccount) => {
        // Switch to customer view and show the new wallet's customer
        setViewMode('customer');
        setSearchCustomerId(newWallet.customerId.toString());
        setPage(0);
    }, []);

    // Handle search - trigger refetch when customer ID changes
    const handleSearchChange = (value: string) => {
        setSearchCustomerId(value);
        setPage(0); // Reset to first page when search changes
    };

    // Handle view mode change
    const handleViewModeChange = (mode: string) => {
        setViewMode(mode as 'customer' | 'all');
        setPage(0);
    };

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Wallets</h1>
                    <p className="text-muted-foreground">
                        Manage customer wallet accounts
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button
                        variant="outline"
                        onClick={handleRefresh}
                        disabled={isRefreshing || isFetching}
                    >
                        <RefreshCw className={`mr-2 h-4 w-4 ${(isRefreshing || isFetching) ? 'animate-spin' : ''}`} />
                        {isRefreshing ? 'Refreshing...' : 'Refresh'}
                    </Button>
                    <Button onClick={() => setIsCreateDialogOpen(true)}>
                        <Plus className="mr-2 h-4 w-4" />
                        Create Wallet
                    </Button>
                </div>
            </div>

            {/* Filters */}
            <Card>
                <CardHeader className="pb-3">
                    <div className="flex items-center justify-between">
                        <CardTitle className="text-lg">View Mode</CardTitle>
                        <Tabs value={viewMode} onValueChange={handleViewModeChange}>
                            <TabsList>
                                <TabsTrigger value="all" className="flex items-center gap-2">
                                    <Users className="h-4 w-4" />
                                    All Wallets
                                </TabsTrigger>
                                <TabsTrigger value="customer" className="flex items-center gap-2">
                                    <User className="h-4 w-4" />
                                    By Customer
                                </TabsTrigger>
                            </TabsList>
                        </Tabs>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-wrap gap-4">
                        {viewMode === 'customer' && (
                            <div className="relative w-64">
                                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                                <Input
                                    placeholder="Enter Customer ID..."
                                    value={searchCustomerId}
                                    onChange={(e) => handleSearchChange(e.target.value)}
                                    className="pl-9"
                                />
                            </div>
                        )}
                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-48">
                                <SelectValue placeholder="Filter by status" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Statuses</SelectItem>
                                <SelectItem value={WalletAccountStatus.ACTIVE}>Active</SelectItem>
                                <SelectItem value={WalletAccountStatus.SUSPENDED}>Suspended</SelectItem>
                                <SelectItem value={WalletAccountStatus.CLOSED}>Closed</SelectItem>
                            </SelectContent>
                        </Select>
                        {viewMode === 'customer' && (
                            <Button
                                variant="secondary"
                                onClick={handleRefresh}
                                disabled={!searchCustomerId || isRefreshing}
                            >
                                Search
                            </Button>
                        )}
                    </div>
                    <p className="mt-2 text-sm text-muted-foreground">
                        {isAllMode
                            ? `Showing all wallets (${wallets.length} loaded)`
                            : `Showing wallets for Customer ID: ${searchCustomerId || '(enter a customer ID)'}`
                        }
                    </p>
                </CardContent>
            </Card>

            {/* Error State */}
            {isError && (
                <Card className="border-destructive">
                    <CardContent className="pt-6">
                        <p className="text-center text-destructive">
                            Failed to load wallets. Make sure the backend is running at http://localhost:8080
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
            {!isError && (
                <Card>
                    <CardContent className="pt-6">
                        <DataTable
                            columns={columns}
                            data={wallets}
                            isLoading={isLoading || isFetching}
                            pageCount={totalPages}
                            pageIndex={page}
                            pageSize={20}
                            onPageChange={setPage}
                        />
                    </CardContent>
                </Card>
            )}

            {/* Create Wallet Dialog */}
            <CreateWalletDialog
                open={isCreateDialogOpen}
                onOpenChange={setIsCreateDialogOpen}
                defaultCustomerId={searchCustomerId}
                onWalletCreated={handleWalletCreated}
            />
        </div>
    );
}
