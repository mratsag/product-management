# 💳 Wallet & Payment Management Frontend

Modern, type-safe React + TypeScript admin dashboard for the Wallet & Payment Management System.

## ✨ Features

- **Dashboard**: Overview cards, transaction charts, recent activity feed
- **Wallet Management**: List, create, view details, update status
- **Ledger Operations**: LOAD, SPEND, REFUND, ADJUSTMENT entries
- **Payment Management**: Create, track, update payment status
- **Transaction Tracking**: View transactions, fees, correlations
- **Order Allocations**: Manage payment-order allocations
- **Reports**: Fee breakdown, volume charts, payment summaries

## 🛠️ Tech Stack

- **React 19** with **TypeScript**
- **Vite 7** - Build tool
- **Tailwind CSS v4** + **shadcn/ui** - UI components
- **TanStack React Query v5** - Server state management
- **TanStack Table v8** - Data tables
- **React Hook Form v7** + **Zod** - Form validation
- **React Router v6** - Routing
- **Zustand** - Global state
- **Recharts** - Data visualization
- **Axios** - HTTP client

## 🚀 Getting Started

### Prerequisites

- Node.js 18+
- npm 9+

### Installation

```bash
# Clone and enter directory
cd wallet-payment-frontend

# Install dependencies
npm install

# Copy environment file
cp .env.example .env
```

### Development

```bash
npm run dev
```

Open [http://localhost:5173](http://localhost:5173)

### Production Build

```bash
npm run build
npm run preview
```

## 📁 Project Structure

```
src/
├── api/              # Axios client & API services
├── components/
│   ├── ui/           # shadcn/ui components
│   ├── layout/       # Layout, Sidebar, Header
│   ├── common/       # Shared components
│   └── wallet/       # Wallet-specific components
├── hooks/            # React Query hooks
├── lib/              # Utilities
├── pages/            # Route pages
├── store/            # Zustand stores
├── types/            # TypeScript types
└── App.tsx
```

## 🔗 API Integration

Connects to Spring Boot backend at `http://localhost:8080/api`

**Endpoints covered:** 27 total
- Wallet Accounts (5)
- Ledger Entries (5)
- Ledger Fees (3)
- Payments (4)
- Transactions (4)
- Transaction Fees (3)
- Allocations (3)

## 📱 Pages

| Route | Description |
|-------|-------------|
| `/` | Dashboard with overview |
| `/wallets` | Wallet list with filters |
| `/wallets/:id` | Wallet detail with history |
| `/payments` | Payment list |
| `/payments/:id` | Payment detail |
| `/allocations` | Order allocations |
| `/reports` | Analytics & reports |

## 🎨 Theme

- Dark mode by default
- Custom color scheme with status indicators
- Inter font family
- Responsive design

## 📄 License

MIT
