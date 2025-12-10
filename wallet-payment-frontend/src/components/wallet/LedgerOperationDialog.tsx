import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { DollarSign, Minus, RotateCcw, Settings, Loader2 } from 'lucide-react';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { useLoadBalance, useSpendFromWallet, useRefundToWallet, useAdjustment } from '@/hooks';
import { LedgerEntryType } from '@/types';

// Schema for ledger operations
const ledgerOperationSchema = z.object({
    amount: z.coerce
        .number()
        .positive('Amount must be greater than 0'),
    description: z.string().optional(),
});

type LedgerOperationFormData = z.infer<typeof ledgerOperationSchema>;

type OperationType = 'LOAD' | 'SPEND' | 'REFUND' | 'ADJUSTMENT';

interface LedgerOperationDialogProps {
    walletAccountId: number;
    open: boolean;
    onOpenChange: (open: boolean) => void;
    operationType: OperationType;
}

const operationConfig: Record<OperationType, {
    title: string;
    description: string;
    icon: typeof DollarSign;
    buttonText: string;
    buttonVariant: 'default' | 'destructive' | 'secondary';
    entryType: LedgerEntryType;
}> = {
    LOAD: {
        title: 'Load Balance',
        description: 'Add funds to this wallet account.',
        icon: DollarSign,
        buttonText: 'Load Balance',
        buttonVariant: 'default',
        entryType: LedgerEntryType.LOAD,
    },
    SPEND: {
        title: 'Spend from Wallet',
        description: 'Deduct funds from this wallet account.',
        icon: Minus,
        buttonText: 'Spend',
        buttonVariant: 'destructive',
        entryType: LedgerEntryType.SPEND,
    },
    REFUND: {
        title: 'Refund to Wallet',
        description: 'Process a refund to this wallet account.',
        icon: RotateCcw,
        buttonText: 'Process Refund',
        buttonVariant: 'default',
        entryType: LedgerEntryType.REFUND,
    },
    ADJUSTMENT: {
        title: 'Manual Adjustment',
        description: 'Make a manual adjustment to the wallet balance.',
        icon: Settings,
        buttonText: 'Apply Adjustment',
        buttonVariant: 'secondary',
        entryType: LedgerEntryType.ADJUSTMENT,
    },
};

export function LedgerOperationDialog({
    walletAccountId,
    open,
    onOpenChange,
    operationType,
}: LedgerOperationDialogProps) {
    const loadBalance = useLoadBalance();
    const spendFromWallet = useSpendFromWallet();
    const refundToWallet = useRefundToWallet();
    const adjustment = useAdjustment();

    const config = operationConfig[operationType];
    const Icon = config.icon;

    const form = useForm<LedgerOperationFormData>({
        resolver: zodResolver(ledgerOperationSchema),
        defaultValues: {
            amount: 0,
            description: '',
        },
    });

    const getMutation = () => {
        switch (operationType) {
            case 'LOAD': return loadBalance;
            case 'SPEND': return spendFromWallet;
            case 'REFUND': return refundToWallet;
            case 'ADJUSTMENT': return adjustment;
        }
    };

    const mutation = getMutation();
    const isPending = mutation.isPending;

    const onSubmit = async (data: LedgerOperationFormData) => {
        try {
            await mutation.mutateAsync({
                walletAccountId,
                entryType: config.entryType,
                amount: data.amount,
                method: 'MANUAL',
                description: data.description || `${operationType} operation`,
            });
            form.reset();
            onOpenChange(false);
        } catch {
            // Error handled by mutation
        }
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Icon className="h-5 w-5" />
                        {config.title}
                    </DialogTitle>
                    <DialogDescription>
                        {config.description}
                    </DialogDescription>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="amount"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Amount</FormLabel>
                                    <FormControl>
                                        <div className="relative">
                                            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                                                ₺
                                            </span>
                                            <Input
                                                type="number"
                                                step="0.01"
                                                placeholder="0.00"
                                                className="pl-8"
                                                {...field}
                                            />
                                        </div>
                                    </FormControl>
                                    <FormDescription>
                                        Enter the amount to {operationType.toLowerCase()}.
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="description"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Description (Optional)</FormLabel>
                                    <FormControl>
                                        <Textarea
                                            placeholder="Enter a description for this transaction..."
                                            rows={3}
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <DialogFooter>
                            <Button
                                type="button"
                                variant="outline"
                                onClick={() => onOpenChange(false)}
                            >
                                Cancel
                            </Button>
                            <Button
                                type="submit"
                                variant={config.buttonVariant}
                                disabled={isPending}
                            >
                                {isPending ? (
                                    <>
                                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                        Processing...
                                    </>
                                ) : (
                                    config.buttonText
                                )}
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}

// Quick action buttons component
interface LedgerActionButtonsProps {
    walletAccountId: number;
}

export function LedgerActionButtons({ walletAccountId }: LedgerActionButtonsProps) {
    const [openDialog, setOpenDialog] = useState<OperationType | null>(null);

    return (
        <>
            <div className="flex flex-wrap gap-2">
                <Button onClick={() => setOpenDialog('LOAD')} size="sm">
                    <DollarSign className="mr-2 h-4 w-4" />
                    Load Balance
                </Button>
                <Button onClick={() => setOpenDialog('SPEND')} size="sm" variant="destructive">
                    <Minus className="mr-2 h-4 w-4" />
                    Spend
                </Button>
                <Button onClick={() => setOpenDialog('REFUND')} size="sm" variant="outline">
                    <RotateCcw className="mr-2 h-4 w-4" />
                    Refund
                </Button>
                <Button onClick={() => setOpenDialog('ADJUSTMENT')} size="sm" variant="secondary">
                    <Settings className="mr-2 h-4 w-4" />
                    Adjustment
                </Button>
            </div>

            {openDialog && (
                <LedgerOperationDialog
                    walletAccountId={walletAccountId}
                    open={!!openDialog}
                    onOpenChange={(open) => !open && setOpenDialog(null)}
                    operationType={openDialog}
                />
            )}
        </>
    );
}
