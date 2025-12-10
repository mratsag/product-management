import { Link } from 'react-router-dom';
import { Wallet, CreditCard, TrendingUp, Clock, Plus, ArrowRight, RefreshCw } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { StatCard } from '@/components/common';
import { StatusBadge } from '@/components/common';
import { useWalletsByCustomer, useLedgerEntries, usePayments } from '@/hooks';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { LedgerEntryType, PaymentStatus } from '@/types';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
    Legend,
} from 'recharts';

export function Dashboard() {
    // Fetch real data
    const { data: walletsData, isLoading: walletsLoading, refetch: refetchWallets } = useWalletsByCustomer(12345, { size: 100 });
    const { data: ledgerData, isLoading: ledgerLoading, refetch: refetchLedger } = useLedgerEntries({ size: 10, sort: 'createdAt,desc' });
    const { data: paymentsData, isLoading: paymentsLoading, refetch: refetchPayments } = usePayments({ size: 100 });

    const wallets = walletsData?.content ?? [];
    const ledgerEntries = ledgerData?.content ?? [];
    const payments = paymentsData?.content ?? [];

    // Calculate stats
    const activeWallets = wallets.filter(w => w.status === 'ACTIVE').length;
    const totalBalance = wallets.reduce((sum, w) => sum + w.currentBalance, 0);
    const todayTransactions = ledgerEntries.length; // Simplified - would need date filter
    const pendingPayments = payments.filter(p => p.status === PaymentStatus.PENDING).length;

    // Payment status distribution for pie chart
    const paymentStatusCounts = payments.reduce((acc, p) => {
        acc[p.status] = (acc[p.status] || 0) + 1;
        return acc;
    }, {} as Record<string, number>);

    const paymentStatusData = [
        { name: 'PAID', value: paymentStatusCounts[PaymentStatus.PAID] || 0, color: 'hsl(var(--success))' },
        { name: 'PENDING', value: paymentStatusCounts[PaymentStatus.PENDING] || 0, color: 'hsl(var(--warning))' },
        { name: 'PARTIAL', value: paymentStatusCounts[PaymentStatus.PARTIALLY_PAID] || 0, color: 'hsl(var(--chart-3))' },
        { name: 'FAILED', value: paymentStatusCounts[PaymentStatus.FAILED] || 0, color: 'hsl(var(--destructive))' },
    ].filter(item => item.value > 0);

    // Transaction volume by type
    const transactionVolume = ledgerEntries.reduce((acc, entry) => {
        const type = entry.entryType;
        if (!acc[type]) acc[type] = 0;
        acc[type] += entry.amount;
        return acc;
    }, {} as Record<string, number>);

    const transactionVolumeData = Object.entries(transactionVolume).map(([name, amount]) => ({
        name,
        amount,
    }));

    const handleRefreshAll = () => {
        refetchWallets();
        refetchLedger();
        refetchPayments();
    };

    const isLoading = walletsLoading || ledgerLoading || paymentsLoading;

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
                    <p className="text-muted-foreground">
                        Welcome back! Here's an overview of your wallet and payment system.
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" onClick={handleRefreshAll} disabled={isLoading}>
                        <RefreshCw className={`mr-2 h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
                        Refresh
                    </Button>
                    <Button asChild>
                        <Link to="/wallets">
                            <Plus className="mr-2 h-4 w-4" />
                            Create Wallet
                        </Link>
                    </Button>
                    <Button variant="outline" asChild>
                        <Link to="/payments">
                            <CreditCard className="mr-2 h-4 w-4" />
                            New Payment
                        </Link>
                    </Button>
                </div>
            </div>

            {/* Stats Grid */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    title="Active Wallets"
                    value={activeWallets}
                    subtitle="for customer 12345"
                    icon={Wallet}
                    isLoading={walletsLoading}
                />
                <StatCard
                    title="Total Balance"
                    value={formatCurrency(totalBalance)}
                    subtitle="across all wallets"
                    icon={TrendingUp}
                    isLoading={walletsLoading}
                />
                <StatCard
                    title="Recent Transactions"
                    value={todayTransactions}
                    subtitle="last 10 entries"
                    icon={CreditCard}
                    isLoading={ledgerLoading}
                />
                <StatCard
                    title="Pending Payments"
                    value={pendingPayments}
                    subtitle="requires attention"
                    icon={Clock}
                    isLoading={paymentsLoading}
                />
            </div>

            {/* Charts Row */}
            <div className="grid gap-6 lg:grid-cols-2">
                {/* Transaction Volume Chart */}
                <Card>
                    <CardHeader>
                        <CardTitle>Transaction Volume by Type</CardTitle>
                        <CardDescription>Recent transaction amounts by entry type</CardDescription>
                    </CardHeader>
                    <CardContent>
                        {ledgerLoading ? (
                            <Skeleton className="h-[300px] w-full" />
                        ) : transactionVolumeData.length > 0 ? (
                            <div className="h-[300px]">
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={transactionVolumeData}>
                                        <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                                        <XAxis
                                            dataKey="name"
                                            className="text-xs fill-muted-foreground"
                                            tickLine={false}
                                            axisLine={false}
                                        />
                                        <YAxis
                                            className="text-xs fill-muted-foreground"
                                            tickLine={false}
                                            axisLine={false}
                                            tickFormatter={(value) => `₺${value}`}
                                        />
                                        <Tooltip
                                            contentStyle={{
                                                backgroundColor: 'hsl(var(--card))',
                                                border: '1px solid hsl(var(--border))',
                                                borderRadius: '8px',
                                            }}
                                            formatter={(value: number) => [`₺${value.toLocaleString()}`, 'Amount']}
                                        />
                                        <Bar
                                            dataKey="amount"
                                            fill="hsl(var(--primary))"
                                            radius={[4, 4, 0, 0]}
                                        />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        ) : (
                            <div className="h-[300px] flex items-center justify-center text-muted-foreground">
                                No transaction data available
                            </div>
                        )}
                    </CardContent>
                </Card>

                {/* Payment Status Distribution */}
                <Card>
                    <CardHeader>
                        <CardTitle>Payment Status Distribution</CardTitle>
                        <CardDescription>Current payment statuses breakdown</CardDescription>
                    </CardHeader>
                    <CardContent>
                        {paymentsLoading ? (
                            <Skeleton className="h-[300px] w-full" />
                        ) : paymentStatusData.length > 0 ? (
                            <div className="h-[300px]">
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={paymentStatusData}
                                            cx="50%"
                                            cy="50%"
                                            innerRadius={60}
                                            outerRadius={100}
                                            paddingAngle={2}
                                            dataKey="value"
                                            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                            labelLine={false}
                                        >
                                            {paymentStatusData.map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.color} />
                                            ))}
                                        </Pie>
                                        <Tooltip
                                            contentStyle={{
                                                backgroundColor: 'hsl(var(--card))',
                                                border: '1px solid hsl(var(--border))',
                                                borderRadius: '8px',
                                            }}
                                        />
                                        <Legend />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
                        ) : (
                            <div className="h-[300px] flex items-center justify-center text-muted-foreground">
                                No payment data available
                            </div>
                        )}
                    </CardContent>
                </Card>
            </div>

            {/* Recent Activity */}
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <div>
                        <CardTitle>Recent Activity</CardTitle>
                        <CardDescription>Latest wallet transactions</CardDescription>
                    </div>
                    <Button variant="ghost" size="sm" asChild>
                        <Link to="/wallets">
                            View All
                            <ArrowRight className="ml-2 h-4 w-4" />
                        </Link>
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
                            {ledgerEntries.slice(0, 5).map((entry) => (
                                <div
                                    key={entry.id}
                                    className="flex items-center justify-between rounded-lg border border-border p-4"
                                >
                                    <div className="flex items-center gap-4">
                                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10 text-primary">
                                            <CreditCard className="h-5 w-5" />
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2">
                                                <StatusBadge status={entry.entryType} type="ledgerEntry" />
                                                <span className="text-sm text-muted-foreground">
                                                    Wallet #{entry.walletAccountId}
                                                </span>
                                            </div>
                                            <p className="text-xs text-muted-foreground">
                                                {formatDateTime(entry.createdAt)}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-4">
                                        <StatusBadge status={entry.status} type="ledgerStatus" />
                                        <span
                                            className={`text-lg font-semibold ${entry.entryType === LedgerEntryType.SPEND ? 'text-destructive' : 'text-success'
                                                }`}
                                        >
                                            {entry.entryType === LedgerEntryType.SPEND ? '-' : '+'}₺{entry.amount.toLocaleString()}
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="py-8 text-center text-muted-foreground">
                            No recent activity
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
