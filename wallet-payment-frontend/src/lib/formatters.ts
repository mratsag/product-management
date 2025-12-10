import { format } from 'date-fns';

export function formatCurrency(amount: number, currencyCode = 'TRY'): string {
    return new Intl.NumberFormat('tr-TR', {
        style: 'currency',
        currency: currencyCode,
    }).format(amount);
}

export function formatDate(date: string | Date): string {
    return format(new Date(date), 'dd MMM yyyy');
}

export function formatDateTime(date: string | Date): string {
    return format(new Date(date), 'dd MMM yyyy HH:mm');
}

export function formatNumber(num: number): string {
    return new Intl.NumberFormat('tr-TR').format(num);
}
