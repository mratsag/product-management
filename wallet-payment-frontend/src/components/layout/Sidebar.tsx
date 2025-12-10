import { NavLink } from 'react-router-dom';
import {
    LayoutDashboard,
    Wallet,
    CreditCard,
    Receipt,
    FileText,
    ChevronLeft,
    ChevronRight,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useSidebarStore } from '@/store';

const navItems = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/wallets', label: 'Wallets', icon: Wallet },
    { to: '/payments', label: 'Payments', icon: CreditCard },
    { to: '/allocations', label: 'Allocations', icon: Receipt },
    { to: '/reports', label: 'Reports', icon: FileText },
];

export function Sidebar() {
    const { isCollapsed, toggleSidebar } = useSidebarStore();

    return (
        <aside
            className={cn(
                'relative flex h-screen flex-col border-r border-border bg-card transition-all duration-300',
                isCollapsed ? 'w-16' : 'w-64'
            )}
        >
            {/* Logo */}
            <div className="flex h-16 items-center justify-between border-b border-border px-4">
                {!isCollapsed && (
                    <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                            <Wallet className="h-5 w-5" />
                        </div>
                        <span className="text-lg font-semibold">WalletPay</span>
                    </div>
                )}
                <Button
                    variant="ghost"
                    size="icon"
                    onClick={toggleSidebar}
                    className={cn('h-8 w-8', isCollapsed && 'mx-auto')}
                >
                    {isCollapsed ? (
                        <ChevronRight className="h-4 w-4" />
                    ) : (
                        <ChevronLeft className="h-4 w-4" />
                    )}
                </Button>
            </div>

            {/* Navigation */}
            <ScrollArea className="flex-1 py-4">
                <nav className="flex flex-col gap-1 px-2">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({ isActive }) =>
                                cn(
                                    'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                                    'hover:bg-accent hover:text-accent-foreground',
                                    isActive
                                        ? 'bg-accent text-accent-foreground'
                                        : 'text-muted-foreground',
                                    isCollapsed && 'justify-center px-2'
                                )
                            }
                        >
                            <item.icon className="h-5 w-5 shrink-0" />
                            {!isCollapsed && <span>{item.label}</span>}
                        </NavLink>
                    ))}
                </nav>
            </ScrollArea>

            {/* Footer */}
            <div className="border-t border-border p-4">
                {!isCollapsed && (
                    <p className="text-xs text-muted-foreground">
                        © 2025 WalletPay Admin
                    </p>
                )}
            </div>
        </aside>
    );
}
