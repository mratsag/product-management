# Wallet & Payment Management System
## Teknik Dokümantasyon

**Versiyon:** 1.0.0  
**Tarih:** 10 Aralık 2024  
**Hazırlayan:** Murat Sağ

---

## 📋 İçindekiler

1. [Proje Özeti](#proje-özeti)
2. [Teknik Mimari](#teknik-mimari)
3. [Teknoloji Stack](#teknoloji-stack)
4. [Backend Yapısı](#backend-yapısı)
5. [Frontend Yapısı](#frontend-yapısı)
6. [Veritabanı Şeması](#veritabanı-şeması)
7. [API Endpoints](#api-endpoints)
8. [Event Sistemi](#event-sistemi)
9. [Kod İstatistikleri](#kod-istatistikleri)
10. [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)

---

## 🎯 Proje Özeti

**Wallet & Payment Management System**, dijital cüzdan ve ödeme işlemlerini yönetmek için geliştirilmiş full-stack bir web uygulamasıdır.

### Temel Özellikler
- ✅ **Cüzdan Yönetimi** - Müşteri bazlı dijital cüzdan oluşturma ve yönetimi
- ✅ **Bakiye İşlemleri** - Yükleme, harcama, iade ve düzeltme operasyonları
- ✅ **Ödeme Yönetimi** - Farklı ödeme tipleri ve durum takibi
- ✅ **Allocation Sistemi** - Sipariş-ödeme eşleştirme
- ✅ **Ücret Yönetimi** - İşlem bazlı ücret hesaplama ve takibi
- ✅ **Raporlama** - Dashboard ve analitik raporlar
- ✅ **Event Sistemi** - Asenkron olay işleme ve bildirim

---

## 🏗️ Teknik Mimari

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (React)                        │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐            │
│  │Dashboard│ │ Wallets │ │Payments │ │ Reports │            │
│  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘            │
│       └───────────┴───────────┴───────────┘                  │
│                         │                                     │
│                    Axios HTTP                                 │
└─────────────────────────┼───────────────────────────────────┘
                          │
                    REST API (JSON)
                          │
┌─────────────────────────┼───────────────────────────────────┐
│                      BACKEND (Spring Boot)                   │
│  ┌──────────────────────┴──────────────────────┐            │
│  │              Controller Layer                │            │
│  │  (WalletAccount, Payment, Ledger, etc.)     │            │
│  └──────────────────────┬──────────────────────┘            │
│                         │                                    │
│  ┌──────────────────────┴──────────────────────┐            │
│  │              Service Layer                   │            │
│  │  (Business Logic + Event Publishing)        │            │
│  └──────────────────────┬──────────────────────┘            │
│                         │                                    │
│  ┌──────────────────────┴──────────────────────┐            │
│  │              Repository Layer                │            │
│  │  (JPA/Hibernate)                            │            │
│  └──────────────────────┬──────────────────────┘            │
│                         │                                    │
│  ┌──────────────────────┴──────────────────────┐            │
│  │              Event System                    │            │
│  │  (Spring Events - Async)                    │            │
│  └─────────────────────────────────────────────┘            │
└─────────────────────────┼───────────────────────────────────┘
                          │
                      JPA/JDBC
                          │
┌─────────────────────────┼───────────────────────────────────┐
│                     DATABASE (MySQL)                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │wallet_account│ │   payment   │ │wallet_ledger│            │
│  └─────────────┘ └─────────────┘ │   _entry    │            │
│  ┌─────────────┐ ┌─────────────┐ └─────────────┘            │
│  │  payment_   │ │   order_    │                             │
│  │ transaction │ │ allocation  │                             │
│  └─────────────┘ └─────────────┘                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 💻 Teknoloji Stack

### Backend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | 17 | Programlama dili |
| Spring Boot | 3.x | Framework |
| Spring Data JPA | 3.x | ORM |
| Spring Events | - | Asenkron olay işleme |
| MySQL | 8.x | Veritabanı |
| Lombok | - | Kod optimizasyonu |
| Swagger/OpenAPI | 3.0 | API dokümantasyonu |

### Frontend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| React | 18.x | UI Framework |
| TypeScript | 5.x | Tip güvenli JavaScript |
| Vite | 5.x | Build tool |
| TanStack Query | 5.x | Server state yönetimi |
| Zustand | - | Client state yönetimi |
| Tailwind CSS | 3.x | Styling |
| Shadcn/ui | - | UI component library |
| Recharts | 2.x | Grafikler |
| React Hook Form | - | Form yönetimi |
| Axios | - | HTTP client |

### DevOps & Araçlar
| Araç | Açıklama |
|------|----------|
| Docker | Container |
| Maven | Backend build |
| npm | Frontend package manager |
| Git | Versiyon kontrolü |

---

## 🔧 Backend Yapısı

### Paket Organizasyonu
```
src/main/java/com/wallet/payment_management/
├── config/                 # Konfigürasyon sınıfları
│   ├── AsyncEventConfig    # Async event thread pool
│   ├── CorsConfig          # CORS ayarları
│   ├── JpaConfig           # JPA auditing
│   └── OpenApiConfig       # Swagger konfigürasyonu
│
├── controller/             # REST API endpoints
│   ├── WalletAccountController
│   ├── WalletLedgerEntryController
│   ├── PaymentController
│   ├── PaymentTransactionController
│   └── ...
│
├── dto/                    # Data Transfer Objects
│   ├── request/            # İstek DTO'ları
│   └── response/           # Yanıt DTO'ları
│
├── entity/                 # JPA Entity sınıfları
│   ├── WalletAccount
│   ├── WalletLedgerEntry
│   ├── Payment
│   └── ...
│
├── enums/                  # Enum tanımları
│   ├── WalletAccountStatusEnum
│   ├── PaymentStatusEnum
│   └── ...
│
├── event/                  # Spring Events
│   ├── BaseEvent
│   ├── WalletCreatedEvent
│   ├── BalanceChangedEvent
│   ├── PaymentCreatedEvent
│   └── listener/
│       ├── AuditEventListener
│       └── NotificationEventListener
│
├── exception/              # Custom exception'lar
│   ├── ResourceNotFoundException
│   ├── InsufficientBalanceException
│   └── ...
│
├── repository/             # JPA Repository'ler
│   ├── WalletAccountRepository
│   ├── PaymentRepository
│   └── ...
│
└── service/                # İş mantığı katmanı
    ├── WalletAccountService
    ├── PaymentService
    └── impl/               # Implementasyonlar
```

### Entity İlişkileri

```
WalletAccount (1) ──────── (N) WalletLedgerEntry
      │                              │
      │                              └──── (N) WalletLedgerEntryFee
      │
Payment (1) ────────────── (N) PaymentTransaction
      │                              │
      │                              └──── (N) PaymentTransactionFee
      │
      └──────────────────── (N) OrderPaymentAllocation
```

---

## 🎨 Frontend Yapısı

### Dizin Organizasyonu
```
wallet-payment-frontend/src/
├── api/                    # API istemcileri
│   ├── client.ts           # Axios instance
│   ├── wallet.ts           # Wallet API
│   ├── payment.ts          # Payment API
│   └── ...
│
├── components/             # React bileşenleri
│   ├── common/             # Ortak bileşenler
│   │   ├── DataTable.tsx
│   │   ├── StatusBadge.tsx
│   │   └── StatCard.tsx
│   ├── layout/             # Layout bileşenleri
│   │   ├── Sidebar.tsx
│   │   └── Layout.tsx
│   ├── ui/                 # shadcn/ui bileşenleri
│   └── wallet/             # Wallet modülü
│       ├── CreateWalletDialog.tsx
│       └── LedgerOperationDialog.tsx
│
├── hooks/                  # Custom React hooks
│   ├── useWallet.ts
│   ├── usePayment.ts
│   └── useLedger.ts
│
├── pages/                  # Sayfa bileşenleri
│   ├── Dashboard.tsx
│   ├── Allocations.tsx
│   ├── Reports.tsx
│   ├── wallets/
│   │   ├── WalletList.tsx
│   │   └── WalletDetail.tsx
│   └── payments/
│       ├── PaymentList.tsx
│       └── PaymentDetail.tsx
│
├── store/                  # Zustand state
│   └── index.ts
│
├── types/                  # TypeScript tipleri
│   └── index.ts
│
└── lib/                    # Yardımcı fonksiyonlar
    ├── utils.ts
    └── formatters.ts
```

---

## 🗄️ Veritabanı Şeması

### Ana Tablolar

| Tablo | Açıklama |
|-------|----------|
| `wallet_account` | Müşteri cüzdanları |
| `wallet_ledger_entry` | Cüzdan işlem kayıtları |
| `wallet_ledger_entry_fee` | Cüzdan işlem ücretleri |
| `payment` | Ödeme kayıtları |
| `payment_transaction` | Ödeme transaction'ları |
| `payment_transaction_fee` | Transaction ücretleri |
| `order_payment_allocation` | Sipariş-ödeme eşleştirmeleri |

### wallet_account
| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | BIGINT | Primary key |
| customer_id | BIGINT | Müşteri ID |
| currency_code | VARCHAR | Para birimi (TRY, USD, EUR) |
| current_balance | DECIMAL | Mevcut bakiye |
| status | ENUM | ACTIVE, SUSPENDED, CLOSED |
| account_type | ENUM | MAIN, BONUS, GIFT |

### wallet_ledger_entry
| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | BIGINT | Primary key |
| wallet_account_id | BIGINT | Foreign key |
| entry_type | ENUM | LOAD, SPEND, REFUND, ADJUSTMENT |
| entry_direction | ENUM | CREDIT, DEBIT |
| amount | DECIMAL | İşlem tutarı |
| balance_after | DECIMAL | İşlem sonrası bakiye |
| status | ENUM | PENDING, POSTED, FAILED |

---

## 🌐 API Endpoints

### Wallet Account API
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/wallet-accounts` | Cüzdan oluştur |
| GET | `/api/wallet-accounts` | Tüm cüzdanları listele |
| GET | `/api/wallet-accounts/{id}` | Cüzdan detayı |
| GET | `/api/wallet-accounts/customer/{id}` | Müşteri cüzdanları |
| PATCH | `/api/wallet-accounts/{id}/status` | Durum güncelle |
| GET | `/api/wallet-accounts/{id}/check-balance` | Bakiye kontrolü |

### Wallet Ledger API
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/wallet-ledger-entries/load` | Bakiye yükle |
| POST | `/api/wallet-ledger-entries/spend` | Harcama yap |
| POST | `/api/wallet-ledger-entries/refund` | İade yap |
| POST | `/api/wallet-ledger-entries/adjustment` | Düzeltme yap |
| GET | `/api/wallet-ledger-entries` | İşlemleri listele |

### Payment API
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/payments` | Ödeme oluştur |
| GET | `/api/payments` | Ödemeleri listele |
| GET | `/api/payments/{id}` | Ödeme detayı |
| PATCH | `/api/payments/{id}/status` | Durum güncelle |

### API Dokümantasyonu
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`

---

## 📡 Event Sistemi

### Event Sınıfları
| Event | Tetiklenme Zamanı |
|-------|-------------------|
| `WalletCreatedEvent` | Yeni cüzdan oluşturulduğunda |
| `WalletStatusChangedEvent` | Cüzdan durumu değiştiğinde |
| `BalanceChangedEvent` | Bakiye işlemi yapıldığında |
| `PaymentCreatedEvent` | Yeni ödeme oluşturulduğunda |
| `PaymentStatusChangedEvent` | Ödeme durumu değiştiğinde |

### Event Listeners
| Listener | Görev |
|----------|-------|
| `AuditEventListener` | Tüm olayları loglar |
| `NotificationEventListener` | Önemli olaylarda bildirim gönderir |

### Asenkron İşleme
- Thread pool: 2-10 thread
- Queue kapasitesi: 500
- Prefix: `event-async-`

---

## 📊 Kod İstatistikleri

| Kategori | Değer |
|----------|-------|
| **Toplam Kod Satırı** | 20,784 |
| **Toplam Dosya** | 194 |
| **Java Dosyaları** | 90 |
| **TypeScript Dosyaları** | 69 |
| **Java Kod Satırı** | 3,811 |
| **TypeScript Kod Satırı** | 6,112 |

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java 17+
- Node.js 18+
- MySQL 8.x
- Maven 3.8+

### Backend
```bash
# Proje dizinine git
cd product-management

# Bağımlılıkları yükle ve derle
./mvnw clean install

# Çalıştır
./mvnw spring-boot:run
```

### Frontend
```bash
# Frontend dizinine git
cd wallet-payment-frontend

# Bağımlılıkları yükle
npm install

# Geliştirme sunucusunu başlat
npm run dev
```

### Docker (Opsiyonel)
```bash
docker-compose up -d
```

---

## 📞 İletişim

**Geliştirici:** Murat Sağ  
**Proje:** Wallet & Payment Management System  
**Tarih:** Aralık 2024

---

*Bu dokümantasyon projenin teknik yapısını özetlemektedir.*
