import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from '@/components/ui/sonner';
import { Layout } from '@/components/layout';
import {
  Dashboard,
  WalletList,
  WalletDetail,
  PaymentList,
  PaymentDetail,
  Allocations,
  Reports,
} from '@/pages';

import './App.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Dashboard />} />
            <Route path="wallets" element={<WalletList />} />
            <Route path="wallets/:id" element={<WalletDetail />} />
            <Route path="wallets/:id/transactions" element={<WalletDetail />} />
            <Route path="payments" element={<PaymentList />} />
            <Route path="payments/:id" element={<PaymentDetail />} />
            <Route path="payments/:id/transactions" element={<PaymentDetail />} />
            <Route path="allocations" element={<Allocations />} />
            <Route path="reports" element={<Reports />} />
          </Route>
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" richColors />
    </QueryClientProvider>
  );
}

export default App;
