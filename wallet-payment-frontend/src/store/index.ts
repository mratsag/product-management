import { create } from 'zustand';

interface SidebarState {
    isCollapsed: boolean;
    toggleSidebar: () => void;
    setCollapsed: (collapsed: boolean) => void;
}

export const useSidebarStore = create<SidebarState>((set) => ({
    isCollapsed: false,
    toggleSidebar: () => set((state) => ({ isCollapsed: !state.isCollapsed })),
    setCollapsed: (collapsed) => set({ isCollapsed: collapsed }),
}));

interface CustomerState {
    selectedCustomerId: number | null;
    setSelectedCustomerId: (id: number | null) => void;
}

export const useCustomerStore = create<CustomerState>((set) => ({
    selectedCustomerId: null,
    setSelectedCustomerId: (id) => set({ selectedCustomerId: id }),
}));
