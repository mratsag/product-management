import { useState, useEffect } from 'react';
import { Loader2 } from 'lucide-react';
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
import { Button } from '@/components/ui/button';
import { useUpdateAllocation } from '@/hooks';
import type { OrderPaymentAllocation } from '@/types';

interface EditAllocationDialogProps {
    allocation: OrderPaymentAllocation | null;
    open: boolean;
    onOpenChange: (open: boolean) => void;
}

export function EditAllocationDialog({
    allocation,
    open,
    onOpenChange,
}: EditAllocationDialogProps) {
    const [amount, setAmount] = useState('');
    const updateAllocation = useUpdateAllocation();

    useEffect(() => {
        if (allocation) {
            setAmount(allocation.allocatedAmount.toString());
        }
    }, [allocation]);

    const handleSubmit = async () => {
        if (!allocation || !amount) return;

        try {
            await updateAllocation.mutateAsync({
                id: allocation.id,
                orderId: allocation.orderId,
                data: { allocatedAmount: parseFloat(amount) },
            });
            onOpenChange(false);
        } catch {
            // Error handled by mutation
        }
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Edit Allocation</DialogTitle>
                    <DialogDescription>
                        Update the allocated amount for this allocation.
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                    <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                            <span className="text-muted-foreground">Allocation ID:</span>
                            <span className="ml-2 font-mono">#{allocation?.id}</span>
                        </div>
                        <div>
                            <span className="text-muted-foreground">Order ID:</span>
                            <span className="ml-2 font-mono">#{allocation?.orderId}</span>
                        </div>
                        <div>
                            <span className="text-muted-foreground">Payment ID:</span>
                            <span className="ml-2 font-mono">#{allocation?.paymentId}</span>
                        </div>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="amount">Allocated Amount</Label>
                        <div className="relative">
                            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                                ₺
                            </span>
                            <Input
                                id="amount"
                                type="number"
                                step="0.01"
                                placeholder="0.00"
                                className="pl-8"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                            />
                        </div>
                    </div>
                </div>
                <DialogFooter>
                    <Button
                        type="button"
                        variant="outline"
                        onClick={() => onOpenChange(false)}
                    >
                        Cancel
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        disabled={!amount || updateAllocation.isPending}
                    >
                        {updateAllocation.isPending ? (
                            <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                Updating...
                            </>
                        ) : (
                            'Update Allocation'
                        )}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
