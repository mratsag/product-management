import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import {
    WalletAccountStatus,
    LedgerEntryType,
    LedgerEntryDirection,
    LedgerEntryStatus,
    PaymentType,
    PaymentStatus,
    TransactionType,
    TransactionStatus,
} from '@/types';

interface StatusBadgeProps {
    status: string;
    type: 'wallet' | 'ledgerEntry' | 'ledgerDirection' | 'ledgerStatus' | 'payment' | 'paymentStatus' | 'transaction' | 'transactionStatus';
}

const statusStyles: Record<string, string> = {
    // Wallet Status
    [WalletAccountStatus.ACTIVE]: 'bg-success/20 text-success border-success/30',
    [WalletAccountStatus.SUSPENDED]: 'bg-warning/20 text-warning border-warning/30',
    [WalletAccountStatus.CLOSED]: 'bg-destructive/20 text-destructive border-destructive/30',

    // Ledger Entry Type
    [LedgerEntryType.LOAD]: 'bg-success/20 text-success border-success/30',
    [LedgerEntryType.SPEND]: 'bg-chart-3/20 text-chart-3 border-chart-3/30',
    [LedgerEntryType.REFUND]: 'bg-primary/20 text-primary border-primary/30',
    [LedgerEntryType.ADJUSTMENT]: 'bg-chart-4/20 text-chart-4 border-chart-4/30',

    // Ledger Direction
    [LedgerEntryDirection.CREDIT]: 'bg-success/20 text-success border-success/30',
    [LedgerEntryDirection.DEBIT]: 'bg-destructive/20 text-destructive border-destructive/30',

    // Ledger/Transaction Status
    [LedgerEntryStatus.PENDING]: 'bg-warning/20 text-warning border-warning/30',
    [LedgerEntryStatus.COMPLETED]: 'bg-success/20 text-success border-success/30',
    [LedgerEntryStatus.FAILED]: 'bg-destructive/20 text-destructive border-destructive/30',
    [LedgerEntryStatus.CANCELLED]: 'bg-muted-foreground/20 text-muted-foreground border-muted-foreground/30',

    // Payment Type
    [PaymentType.ORDER]: 'bg-primary/20 text-primary border-primary/30',
    [PaymentType.DEPOSIT]: 'bg-success/20 text-success border-success/30',
    [PaymentType.WITHDRAWAL]: 'bg-chart-3/20 text-chart-3 border-chart-3/30',

    // Payment Status
    [PaymentStatus.PENDING]: 'bg-warning/20 text-warning border-warning/30',
    [PaymentStatus.PARTIALLY_PAID]: 'bg-chart-3/20 text-chart-3 border-chart-3/30',
    [PaymentStatus.PAID]: 'bg-success/20 text-success border-success/30',
    [PaymentStatus.CANCELLED]: 'bg-muted-foreground/20 text-muted-foreground border-muted-foreground/30',
    [PaymentStatus.FAILED]: 'bg-destructive/20 text-destructive border-destructive/30',

    // Transaction Type
    [TransactionType.AUTH]: 'bg-primary/20 text-primary border-primary/30',
    [TransactionType.CAPTURE]: 'bg-success/20 text-success border-success/30',
    [TransactionType.REFUND]: 'bg-chart-4/20 text-chart-4 border-chart-4/30',
    [TransactionType.VOID]: 'bg-muted-foreground/20 text-muted-foreground border-muted-foreground/30',

    // Transaction Status
    [TransactionStatus.PENDING]: 'bg-warning/20 text-warning border-warning/30',
    [TransactionStatus.SUCCESS]: 'bg-success/20 text-success border-success/30',
    [TransactionStatus.FAILED]: 'bg-destructive/20 text-destructive border-destructive/30',
};

export function StatusBadge({ status, type }: StatusBadgeProps) {
    const style = statusStyles[status] || 'bg-muted text-muted-foreground';

    return (
        <Badge variant="outline" className={cn('font-medium', style)}>
            {status.replace(/_/g, ' ')}
        </Badge>
    );
}
