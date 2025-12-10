import { useCallback } from 'react';
import { Download } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useLedgerEntries, usePayments, useWalletsByCustomer, useCustomerFeeReport } from '@/hooks';
import { formatCurrency } from '@/lib/formatters';
import { PaymentStatus } from '@/types';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    LineChart,
    Line,
    PieChart,
    Pie,
    Cell,
    Legend,
} from 'recharts';

export function Reports() {
    // Fetch real data
    const { data: walletsData, isLoading: walletsLoading } = useWalletsByCustomer(12345, { size: 100 });
    const { data: paymentsData, isLoading: paymentsLoading } = usePayments({ size: 100 });
    const { data: ledgerData, isLoading: ledgerLoading } = useLedgerEntries({ size: 100, sort: 'createdAt,desc' });
    const { data: feeReport, isLoading: feeLoading } = useCustomerFeeReport(12345);

    const wallets = walletsData?.content ?? [];
    const payments = paymentsData?.content ?? [];
    const ledgerEntries = ledgerData?.content ?? [];

    // Calculate wallet stats
    const totalWallets = wallets.length;
    const activeWallets = wallets.filter(w => w.status === 'ACTIVE').length;
    const totalBalance = wallets.reduce((sum, w) => sum + w.currentBalance, 0);
    const avgBalance = totalWallets > 0 ? totalBalance / totalWallets : 0;

    // Calculate payment stats
    const totalPayments = payments.length;
    const totalPaymentAmount = payments.reduce((sum, p) => sum + p.amount, 0);
    const paidPayments = payments.filter(p => p.status === PaymentStatus.PAID).length;
    const successRate = totalPayments > 0 ? (paidPayments / totalPayments) * 100 : 0;
    const avgPayment = totalPayments > 0 ? totalPaymentAmount / totalPayments : 0;

    // Transaction volume by type
    const transactionVolume = ledgerEntries.reduce((acc, entry) => {
        const type = entry.entryType;
        if (!acc[type]) acc[type] = 0;
        acc[type] += entry.amount;
        return acc;
    }, {} as Record<string, number>);

    const transactionVolumeData = Object.entries(transactionVolume).map(([name, volume]) => ({
        name,
        volume,
    }));

    // Payment method distribution (simulated from payment types)
    const paymentTypeData = payments.reduce((acc, p) => {
        if (!acc[p.paymentType]) acc[p.paymentType] = { count: 0, amount: 0 };
        acc[p.paymentType].count += 1;
        acc[p.paymentType].amount += p.amount;
        return acc;
    }, {} as Record<string, { count: number; amount: number }>);

    const paymentMethodData = Object.entries(paymentTypeData).map(([method, data]) => ({
        method,
        count: data.count,
        amount: data.amount,
    }));

    // Fee breakdown (if available)
    const feeBreakdownData = feeReport ? [
        { name: 'Total Fees', value: feeReport.totalFees || 0, color: 'hsl(var(--chart-1))' },
    ] : [];

    const isLoading = walletsLoading || paymentsLoading || ledgerLoading;

    // Export report as CSV
    const handleExportReport = useCallback(() => {
        const csvRows: string[] = [];

        // Wallet Summary
        csvRows.push('=== WALLET SUMMARY ===');
        csvRows.push('Total Wallets,Active Wallets,Total Balance,Average Balance');
        csvRows.push(`${totalWallets},${activeWallets},${totalBalance.toFixed(2)},${avgBalance.toFixed(2)}`);
        csvRows.push('');

        // Payment Summary
        csvRows.push('=== PAYMENT SUMMARY ===');
        csvRows.push('Total Payments,Total Amount,Success Rate,Average Payment');
        csvRows.push(`${totalPayments},${totalPaymentAmount.toFixed(2)},${successRate.toFixed(1)}%,${avgPayment.toFixed(2)}`);
        csvRows.push('');

        // Payment Types
        if (paymentMethodData.length > 0) {
            csvRows.push('=== PAYMENT TYPES ===');
            csvRows.push('Type,Count,Amount');
            paymentMethodData.forEach(pm => {
                csvRows.push(`${pm.method},${pm.count},${pm.amount.toFixed(2)}`);
            });
            csvRows.push('');
        }

        // Transaction Volume
        if (transactionVolumeData.length > 0) {
            csvRows.push('=== TRANSACTION VOLUME ===');
            csvRows.push('Type,Volume');
            transactionVolumeData.forEach(tv => {
                csvRows.push(`${tv.name},${tv.volume.toFixed(2)}`);
            });
        }

        const csvContent = csvRows.join('\n');
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', `report_${new Date().toISOString().split('T')[0]}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }, [totalWallets, activeWallets, totalBalance, avgBalance, totalPayments,
        totalPaymentAmount, successRate, avgPayment, paymentMethodData, transactionVolumeData]);

    return (
        <div className="space-y-6">
            {/* Page Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Reports</h1>
                    <p className="text-muted-foreground">
                        Analytics and financial reports
                    </p>
                </div>
                <Button variant="outline" onClick={handleExportReport} disabled={isLoading}>
                    <Download className="mr-2 h-4 w-4" />
                    Export Report
                </Button>
            </div>

            {/* Tabs */}
            <Tabs defaultValue="overview" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="overview">Overview</TabsTrigger>
                    <TabsTrigger value="payments">Payment Summary</TabsTrigger>
                    <TabsTrigger value="wallets">Wallet Balance</TabsTrigger>
                </TabsList>

                <TabsContent value="overview" className="space-y-4">
                    <div className="grid gap-6 lg:grid-cols-2">
                        {/* Transaction Volume Chart */}
                        <Card>
                            <CardHeader>
                                <CardTitle>Transaction Volume by Type</CardTitle>
                                <CardDescription>Amounts by transaction type</CardDescription>
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
                                                    tickFormatter={(value) => `₺${(value / 1000).toFixed(0)}k`}
                                                />
                                                <Tooltip
                                                    contentStyle={{
                                                        backgroundColor: 'hsl(var(--card))',
                                                        border: '1px solid hsl(var(--border))',
                                                        borderRadius: '8px',
                                                    }}
                                                    formatter={(value: number) => [formatCurrency(value), 'Volume']}
                                                />
                                                <Bar dataKey="volume" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    </div>
                                ) : (
                                    <div className="h-[300px] flex items-center justify-center text-muted-foreground">
                                        No transaction data
                                    </div>
                                )}
                            </CardContent>
                        </Card>

                        {/* Payment Types Distribution */}
                        <Card>
                            <CardHeader>
                                <CardTitle>Payment Type Distribution</CardTitle>
                                <CardDescription>Breakdown by payment type</CardDescription>
                            </CardHeader>
                            <CardContent>
                                {paymentsLoading ? (
                                    <Skeleton className="h-[300px] w-full" />
                                ) : paymentMethodData.length > 0 ? (
                                    <div className="h-[300px]">
                                        <ResponsiveContainer width="100%" height="100%">
                                            <PieChart>
                                                <Pie
                                                    data={paymentMethodData}
                                                    cx="50%"
                                                    cy="50%"
                                                    outerRadius={100}
                                                    dataKey="amount"
                                                    nameKey="method"
                                                    label={({ method, percent }) => `${method} ${((percent || 0) * 100).toFixed(0)}%`}
                                                    labelLine={false}
                                                >
                                                    {paymentMethodData.map((_, index) => (
                                                        <Cell key={`cell-${index}`} fill={`hsl(var(--chart-${(index % 5) + 1}))`} />
                                                    ))}
                                                </Pie>
                                                <Tooltip
                                                    contentStyle={{
                                                        backgroundColor: 'hsl(var(--card))',
                                                        border: '1px solid hsl(var(--border))',
                                                        borderRadius: '8px',
                                                    }}
                                                    formatter={(value: number) => [formatCurrency(value), 'Amount']}
                                                />
                                                <Legend />
                                            </PieChart>
                                        </ResponsiveContainer>
                                    </div>
                                ) : (
                                    <div className="h-[300px] flex items-center justify-center text-muted-foreground">
                                        No payment data
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    </div>

                    {/* Payment Methods Table */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Payment Types Summary</CardTitle>
                            <CardDescription>Breakdown by payment type</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {isLoading ? (
                                <div className="space-y-4">
                                    {[1, 2, 3].map(i => (
                                        <Skeleton key={i} className="h-16 w-full" />
                                    ))}
                                </div>
                            ) : paymentMethodData.length > 0 ? (
                                <div className="space-y-4">
                                    {paymentMethodData.map((method) => (
                                        <div
                                            key={method.method}
                                            className="flex items-center justify-between rounded-lg border border-border p-4"
                                        >
                                            <div>
                                                <p className="font-medium">{method.method}</p>
                                                <p className="text-sm text-muted-foreground">
                                                    {method.count.toLocaleString()} payments
                                                </p>
                                            </div>
                                            <span className="text-lg font-semibold">
                                                {formatCurrency(method.amount)}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-center text-muted-foreground py-8">
                                    No payment data available
                                </p>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="payments" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Payment Summary Report</CardTitle>
                            <CardDescription>Overview of all payment activities</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {paymentsLoading ? (
                                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                                    {[1, 2, 3, 4].map(i => (
                                        <Skeleton key={i} className="h-24 w-full" />
                                    ))}
                                </div>
                            ) : (
                                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Total Payments</p>
                                        <p className="text-2xl font-bold">{totalPayments.toLocaleString()}</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Total Amount</p>
                                        <p className="text-2xl font-bold">{formatCurrency(totalPaymentAmount)}</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Success Rate</p>
                                        <p className="text-2xl font-bold text-success">{successRate.toFixed(1)}%</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Avg. Payment</p>
                                        <p className="text-2xl font-bold">{formatCurrency(avgPayment)}</p>
                                    </div>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="wallets" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Wallet Balance Report</CardTitle>
                            <CardDescription>Overview of all wallet activities</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {walletsLoading ? (
                                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                                    {[1, 2, 3, 4].map(i => (
                                        <Skeleton key={i} className="h-24 w-full" />
                                    ))}
                                </div>
                            ) : (
                                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Total Wallets</p>
                                        <p className="text-2xl font-bold">{totalWallets.toLocaleString()}</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Total Balance</p>
                                        <p className="text-2xl font-bold">{formatCurrency(totalBalance)}</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Active Wallets</p>
                                        <p className="text-2xl font-bold text-success">{activeWallets}</p>
                                    </div>
                                    <div className="rounded-lg border border-border p-4">
                                        <p className="text-sm text-muted-foreground">Avg. Balance</p>
                                        <p className="text-2xl font-bold">{formatCurrency(avgBalance)}</p>
                                    </div>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>
        </div>
    );
}
