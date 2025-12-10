import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { useCreateWallet } from '@/hooks';
import { WalletAccountType } from '@/types';
import type { WalletAccount } from '@/types';

const createWalletSchema = z.object({
    customerId: z.coerce
        .number()
        .int()
        .positive('Customer ID must be a positive number'),
    currencyCode: z
        .string()
        .length(3, 'Currency code must be 3 characters')
        .toUpperCase(),
    accountType: z.nativeEnum(WalletAccountType).optional(),
});

type CreateWalletFormData = z.infer<typeof createWalletSchema>;

interface CreateWalletDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    defaultCustomerId?: string;
    onWalletCreated?: (wallet: WalletAccount) => void;
}

export function CreateWalletDialog({
    open,
    onOpenChange,
    defaultCustomerId = '',
    onWalletCreated
}: CreateWalletDialogProps) {
    const createWallet = useCreateWallet();

    const form = useForm<CreateWalletFormData>({
        resolver: zodResolver(createWalletSchema),
        defaultValues: {
            customerId: defaultCustomerId ? parseInt(defaultCustomerId, 10) : undefined,
            currencyCode: 'TRY',
            accountType: WalletAccountType.STANDARD,
        },
    });

    const onSubmit = async (data: CreateWalletFormData) => {
        try {
            const newWallet = await createWallet.mutateAsync(data);
            form.reset();
            onOpenChange(false);
            // Call the callback with the new wallet
            if (onWalletCreated) {
                onWalletCreated(newWallet);
            }
        } catch (error) {
            // Error is handled by the mutation
        }
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Create New Wallet</DialogTitle>
                    <DialogDescription>
                        Create a new wallet account for a customer.
                    </DialogDescription>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="customerId"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Customer ID</FormLabel>
                                    <FormControl>
                                        <Input
                                            type="number"
                                            placeholder="Enter customer ID"
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        The unique identifier for the customer.
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="currencyCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Currency Code</FormLabel>
                                    <Select
                                        onValueChange={field.onChange}
                                        defaultValue={field.value}
                                    >
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select currency" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            <SelectItem value="TRY">TRY - Turkish Lira</SelectItem>
                                            <SelectItem value="USD">USD - US Dollar</SelectItem>
                                            <SelectItem value="EUR">EUR - Euro</SelectItem>
                                            <SelectItem value="GBP">GBP - British Pound</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="accountType"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Account Type</FormLabel>
                                    <Select
                                        onValueChange={field.onChange}
                                        defaultValue={field.value}
                                    >
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select account type" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            <SelectItem value={WalletAccountType.STANDARD}>
                                                Standard
                                            </SelectItem>
                                            <SelectItem value={WalletAccountType.PREMIUM}>
                                                Premium
                                            </SelectItem>
                                        </SelectContent>
                                    </Select>
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
                            <Button type="submit" disabled={createWallet.isPending}>
                                {createWallet.isPending ? 'Creating...' : 'Create Wallet'}
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
