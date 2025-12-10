import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Wallet, TrendingUp, CreditCard, Settings, Loader2 } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
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
import { useWallet, useUpdateWalletStatus, useLedgerEntries } from '@/hooks';
import { WalletAccountStatus, LedgerEntryType } from '@/types';
import { LedgerActionButtons } from '@/components/wallet';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
} from 'recharts';

export function WalletDetail() {
    const { id } = useParams<{ id: string }>();
    const walletId = parseInt(id || '0', 10);

    const { data: wallet, isLoading: walletLoading, isError } = useWallet(walletId);
    const { data: ledgerData, isLoading: ledgerLoading } = useLedgerEntries({
        walletAccountId: walletId,
        page: 0,
        size: 10,
        sort: 'createdAt,desc'
    });
    const updateStatus = useUpdateWalletStatus();

    const ledgerEntries = ledgerData?.content ?? [];

    // Generate balance history from ledger entries (reversed for chronological order)
    const balanceHistory = [...ledgerEntries].reverse().map((entry, index) => ({
        date: formatDateTime(entry.createdAt).split(' ')[0] + ' ' + formatDateTime(entry.createdAt).split(' ')[1],
        balance: entry.balanceAfter,
    }));

    const handleStatusChange = (newStatus: string) => {
        if (wallet && newStatus !== wallet.status) {
            updateStatus.mutate({
                id: wallet.id,
                data: { status: newStatus as WalletAccountStatus }
            });
        }
    };

    if (walletLoading) {
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

    if (isError || !wallet) {
        return (
            <div className="flex flex-col items-center justify-center py-12">
                <p className="text-lg text-muted-foreground mb-4">Wallet not found</p>
                <Button asChild>
                    <Link to="/wallets">Back to Wallets</Link>
                </Button>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Button variant="ghost" size="icon" asChild>
                        <Link to="/wallets">
                            <ArrowLeft className="h-5 w-5" />
                        </Link>
                    </Button>
                    <div>
                        <div className="flex items-center gap-3">
                            <h1 className="text-3xl font-bold tracking-tight">
                                Wallet #{wallet.id}
                            </h1>
                            <StatusBadge status={wallet.status} type="wallet" />
                        </div>
                        <p className="text-muted-foreground">
                            Customer #{wallet.customerId} • {wallet.currencyCode}
                        </p>
                    </div>
                </div>
                <div className="flex gap-2">
                    <Select
                        value={wallet.status}
                        onValueChange={handleStatusChange}
                        disabled={updateStatus.isPending}
                    >
                        <SelectTrigger className="w-40">
                            {updateStatus.isPending ? (
                                <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                                <SelectValue />
                            )}
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value={WalletAccountStatus.ACTIVE}>Active</SelectItem>
                            <SelectItem value={WalletAccountStatus.SUSPENDED}>Suspend</SelectItem>
                            <SelectItem value={WalletAccountStatus.CLOSED}>Close</SelectItem>
                        </SelectContent>
                    </Select>
                    <Button asChild>
                        <Link to={`/wallets/${id}/transactions`}>
                            <CreditCard className="mr-2 h-4 w-4" />
                            View Transactions
                        </Link>
                    </Button>
                </div>
            </div>

            {/* Ledger Operation Buttons */}
            <Card>
                <CardHeader className="pb-3">
                    <CardTitle className="text-lg">Quick Actions</CardTitle>
                    <CardDescription>Perform wallet operations</CardDescription>
                </CardHeader>
                <CardContent>
                    <LedgerActionButtons walletAccountId={wallet.id} />
                </CardContent>
            </Card>

            {/* Stats Grid */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    title="Current Balance"
                    value={formatCurrency(wallet.currentBalance, wallet.currencyCode)}
                    icon={Wallet}
                />
                <StatCard
                    title="Account Type"
                    value={wallet.accountType}
                    icon={Settings}
                />
                <StatCard
                    title="Currency"
                    value={wallet.currencyCode}
                    icon={TrendingUp}
                />
                <StatCard
                    title="Transactions"
                    value={ledgerData?.totalElements ?? 0}
                    icon={CreditCard}
                />
            </div>

            {/* Tabs */}
            <Tabs defaultValue="overview" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="overview">Overview</TabsTrigger>
                    <TabsTrigger value="transactions">Recent Transactions</TabsTrigger>
                    <TabsTrigger value="details">Account Details</TabsTrigger>
                </TabsList>

                <TabsContent value="overview" className="space-y-4">
                    {/* Balance History Chart */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Balance History</CardTitle>
                            <CardDescription>Balance trend from recent transactions</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {balanceHistory.length > 0 ? (
                                <div className="h-[300px]">
                                    <ResponsiveContainer width="100%" height="100%">
                                        <LineChart data={balanceHistory}>
                                            <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                                            <XAxis
                                                dataKey="date"
                                                className="text-xs fill-muted-foreground"
                                                tickLine={false}
                                                axisLine={false}
                                            />
                                            <YAxis
                                                className="text-xs fill-muted-foreground"
                                                tickLine={false}
                                                axisLine={false}
                                                tickFormatter={(value) => `₺${(value / 1000).toFixed(0)}k`}
                                            />
                                            <Tooltip
                                                contentStyle={{
                                                    backgroundColor: 'hsl(var(--card))',
                                                    border: '1px solid hsl(var(--border))',
                                                    borderRadius: '8px',
                                                }}
                                                formatter={(value: number) => [formatCurrency(value, wallet.currencyCode), 'Balance']}
                                            />
                                            <Line
                                                type="monotone"
                                                dataKey="balance"
                                                stroke="hsl(var(--primary))"
                                                strokeWidth={2}
                                                dot={{ fill: 'hsl(var(--primary))' }}
                                            />
                                        </LineChart>
                                    </ResponsiveContainer>
                                </div>
                            ) : (
                                <p className="text-center text-muted-foreground py-8">
                                    No transaction history yet
                                </p>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="transactions" className="space-y-4">
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between">
                            <div>
                                <CardTitle>Recent Ledger Entries</CardTitle>
                                <CardDescription>Last 10 wallet transactions</CardDescription>
                            </div>
                            <Button variant="outline" asChild>
                                <Link to={`/wallets/${id}/transactions`}>View All</Link>
                            </Button>
                        </CardHeader>
                        <CardContent>
                            {ledgerLoading ? (
                                <div className="space-y-4">
                                    {[1, 2, 3].map(i => (
                                        <Skeleton key={i} className="h-16 w-full" />
                                    ))}
                                </div>
                            ) : ledgerEntries.length > 0 ? (
                                <div className="space-y-4">
                                    {ledgerEntries.map((entry) => (
                                        <div
                                            key={entry.id}
                                            className="flex items-center justify-between rounded-lg border border-border p-4"
                                        >
                                            <div className="flex items-center gap-4">
                                                <StatusBadge status={entry.entryType} type="ledgerEntry" />
                                                <div>
                                                    <p className="text-sm text-muted-foreground">
                                                        {formatDateTime(entry.createdAt)}
                                                    </p>
                                                    {entry.description && (
                                                        <p className="text-xs text-muted-foreground">
                                                            {entry.description}
                                                        </p>
                                                    )}
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-4">
                                                <StatusBadge status={entry.status} type="ledgerStatus" />
                                                <span
                                                    className={`text-lg font-semibold ${entry.entryType === LedgerEntryType.SPEND
                                                        ? 'text-destructive'
                                                        : 'text-success'
                                                        }`}
                                                >
                                                    {entry.entryType === LedgerEntryType.SPEND ? '-' : '+'}
                                                    {formatCurrency(entry.amount, wallet.currencyCode)}
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
                            <CardTitle>Account Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid gap-4 md:grid-cols-2">
                                <div>
                                    <p className="text-sm text-muted-foreground">Wallet ID</p>
                                    <p className="font-mono font-medium">#{wallet.id}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Customer ID</p>
                                    <p className="font-mono font-medium">{wallet.customerId}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Account Type</p>
                                    <Badge variant="outline">{wallet.accountType}</Badge>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Currency</p>
                                    <p className="font-medium">{wallet.currencyCode}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Created At</p>
                                    <p className="text-sm">{formatDateTime(wallet.createdAt)}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Last Updated</p>
                                    <p className="text-sm">{formatDateTime(wallet.updatedAt)}</p>
                                </div>
                                {wallet.closedAt && (
                                    <div>
                                        <p className="text-sm text-muted-foreground">Closed At</p>
                                        <p className="text-sm">{formatDateTime(wallet.closedAt)}</p>
                                    </div>
                                )}
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>
        </div>
    );
}
