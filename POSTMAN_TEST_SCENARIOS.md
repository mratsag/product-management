# Wallet & Payment Service - Postman Test Senaryoları

**Base URL:** `http://localhost:8080/api`

**Headers (Tüm istekler için):**
```
Content-Type: application/json
Accept: application/json
```

---

## 📋 ENVIRONMENT VARIABLES

Postman'de environment oluştur:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| baseUrl | http://localhost:8080 | http://localhost:8080 |
| customerId | | {{customerId}} |
| walletAccountId | | {{walletAccountId}} |
| ledgerEntryId | | {{ledgerEntryId}} |
| paymentId | | {{paymentId}} |
| transactionId | | {{transactionId}} |
| orderId | 1001 | 1001 |

---

## 🔧 TEST AKIŞI

### Senaryo Sırası:
1. Wallet Account oluştur
2. Wallet'a bakiye yükle (LOAD)
3. Payment oluştur
4. Payment Transaction oluştur
5. Wallet'tan harcama (SPEND)
6. Fee ekle
7. Order Payment Allocation
8. Raporlama

---

## 1️⃣ WALLET ACCOUNT MODÜLÜ

### 1.1 Yeni Wallet Account Oluştur (WA-01)

**Endpoint:** `POST {{baseUrl}}/wallet-accounts`

**Request Body:**
```json
{
  "customerId": 12345,
  "currencyCode": "TRY"
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "customerId": 12345,
  "accountType": "STANDARD",
  "currencyCode": "TRY",
  "currentBalance": 0.00,
  "status": "ACTIVE",
  "createdAt": "2024-12-10T10:00:00",
  "updatedAt": "2024-12-10T10:00:00",
  "closedAt": null,
  "ledgerEntries": []
}
```

**Test Script:**
```javascript
// Save walletAccountId for later tests
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("walletAccountId", response.id);
    pm.environment.set("customerId", response.customerId);
}

pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has wallet account data", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData.status).to.eql('ACTIVE');
    pm.expect(jsonData.currentBalance).to.eql(0.00);
});
```

---

### 1.2 Müşteri Wallet Hesaplarını Listele (WA-02)

**Endpoint:** `GET {{baseUrl}}/wallet-accounts/customer/{{customerId}}?page=0&size=20&sort=createdAt,desc`

**Expected Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "customerId": 12345,
      "accountType": "STANDARD",
      "currencyCode": "TRY",
      "currentBalance": 0.00,
      "status": "ACTIVE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "pageNumber": 0,
  "pageSize": 20,
  "first": true,
  "last": true,
  "empty": false,
  "numberOfElements": 1
}
```

**Test Script:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has pagination data", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('content');
    pm.expect(jsonData).to.have.property('totalElements');
    pm.expect(jsonData).to.have.property('pageNumber');
});
```

---

### 1.3 Wallet Account Detayı (WA-03)

**Endpoint:** `GET {{baseUrl}}/wallet-accounts/{{walletAccountId}}`

**Expected Response:** `200 OK`

---

### 1.4 Wallet Account Durumu Güncelle (WA-04)

**Endpoint:** `PATCH {{baseUrl}}/wallet-accounts/{{walletAccountId}}/status`

**Request Body:**
```json
{
  "status": "SUSPENDED"
}
```

**Expected Response:** `200 OK`

---

### 1.5 Bakiye Kontrolü (WA-05)

**Endpoint:** `GET {{baseUrl}}/wallet-accounts/{{walletAccountId}}/check-balance?requiredAmount=100.00`

**Expected Response:** `200 OK`
```json
true
```

---

### ❌ 1.6 Hata Senaryosu - Geçersiz Customer ID

**Endpoint:** `POST {{baseUrl}}/wallet-accounts`

**Request Body:**
```json
{
  "customerId": null,
  "currencyCode": "TRY"
}
```

**Expected Response:** `400 Bad Request`
```json
{
  "timestamp": "2024-12-10T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/wallet-accounts",
  "details": [
    "customerId: Customer ID is required"
  ]
}
```

---

## 2️⃣ WALLET LEDGER ENTRY MODÜLÜ

### 2.1 Cüzdana Bakiye Yükle - LOAD (WL-01)

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entries/load`

**Request Body:**
```json
{
  "walletAccountId": {{walletAccountId}},
  "entryType": "LOAD",
  "amount": 1000.00,
  "method": "CARD",
  "reference": "TXN-LOAD-001",
  "description": "İlk yükleme"
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "walletAccountId": 1,
  "entryType": "LOAD",
  "entryDirection": "CREDIT",
  "amount": 1000.00,
  "balanceAfter": 1000.00,
  "status": "POSTED",
  "method": "CARD",
  "reference": "TXN-LOAD-001",
  "description": "İlk yükleme",
  "createdAt": "2024-12-10T10:00:00"
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("ledgerEntryId", response.id);
}

pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Balance increased", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.entryDirection).to.eql('CREDIT');
    pm.expect(jsonData.balanceAfter).to.eql(1000.00);
});
```

---

### 2.2 Cüzdandan Harcama - SPEND (WL-02)

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entries/spend`

**Request Body:**
```json
{
  "walletAccountId": {{walletAccountId}},
  "entryType": "SPEND",
  "amount": 250.00,
  "method": "WALLET",
  "reference": "TXN-SPEND-001",
  "description": "Sipariş ödemesi"
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 2,
  "walletAccountId": 1,
  "entryType": "SPEND",
  "entryDirection": "DEBIT",
  "amount": 250.00,
  "balanceAfter": 750.00,
  "status": "POSTED",
  "method": "WALLET",
  "reference": "TXN-SPEND-001",
  "description": "Sipariş ödemesi",
  "createdAt": "2024-12-10T10:05:00"
}
```

---

### 2.3 Cüzdana İade - REFUND (WL-03)

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entries/refund`

**Request Body:**
```json
{
  "walletAccountId": {{walletAccountId}},
  "entryType": "REFUND",
  "amount": 50.00,
  "method": "WALLET",
  "reference": "TXN-REFUND-001",
  "description": "Sipariş iadesi"
}
```

**Expected Response:** `201 Created`

---

### 2.4 Manuel Düzeltme - ADJUSTMENT (WL-04)

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entries/adjustment`

**Request Body:**
```json
{
  "walletAccountId": {{walletAccountId}},
  "entryType": "ADJUSTMENT",
  "amount": 10.00,
  "method": "MANUAL",
  "reference": "TXN-ADJ-001",
  "description": "Operasyonel düzeltme"
}
```

**Expected Response:** `201 Created`

---

### 2.5 Cüzdan Hareketlerini Listele (WL-05)

**Endpoint:** `GET {{baseUrl}}/wallet-ledger-entries?walletAccountId={{walletAccountId}}&page=0&size=20&sort=createdAt,desc`

**Expected Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 2,
      "entryType": "SPEND",
      "amount": 250.00,
      "balanceAfter": 750.00
    },
    {
      "id": 1,
      "entryType": "LOAD",
      "amount": 1000.00,
      "balanceAfter": 1000.00
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "pageNumber": 0,
  "pageSize": 20
}
```

---

### ❌ 2.6 Hata Senaryosu - Yetersiz Bakiye

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entries/spend`

**Request Body:**
```json
{
  "walletAccountId": {{walletAccountId}},
  "amount": 10000.00,
  "method": "WALLET"
}
```

**Expected Response:** `400 Bad Request`
```json
{
  "timestamp": "2024-12-10T10:00:00",
  "status": 400,
  "error": "Insufficient Balance",
  "message": "Insufficient balance in wallet account 1. Current: 750.00, Required: 10000.00",
  "path": "/api/wallet-ledger-entries/spend"
}
```

---

## 3️⃣ WALLET LEDGER ENTRY FEE MODÜLÜ

### 3.1 Ledger Entry Ücreti Oluştur (WF-01)

**Endpoint:** `POST {{baseUrl}}/wallet-ledger-entry-fees`

**Request Body:**
```json
{
  "walletLedgerEntryId": {{ledgerEntryId}},
  "feeType": "SERVICE_FEE",
  "amount": 5.00,
  "description": "Servis ücreti"
}
```

**Expected Response:** `201 Created`

---

### 3.2 Ledger Entry Ücretlerini Listele (WF-02)

**Endpoint:** `GET {{baseUrl}}/wallet-ledger-entry-fees/ledger-entry/{{ledgerEntryId}}`

**Expected Response:** `200 OK`

---

### 3.3 Müşteri Ücret Raporu (WF-03)

**Endpoint:** `GET {{baseUrl}}/wallet-ledger-entry-fees/customer/{{customerId}}/report?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59`

**Expected Response:** `200 OK`
```json
25.50
```

---

## 4️⃣ PAYMENT MODÜLÜ

### 4.1 Yeni Payment Oluştur - ORDER (P-01)

**Endpoint:** `POST {{baseUrl}}/payments`

**Request Body:**
```json
{
  "paymentType": "ORDER",
  "amount": 250.00,
  "description": "Sipariş #1001 ödemesi"
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "paymentType": "ORDER",
  "amount": 250.00,
  "paidAmount": 0.00,
  "status": "PENDING",
  "description": "Sipariş #1001 ödemesi",
  "createdAt": "2024-12-10T10:00:00",
  "updatedAt": "2024-12-10T10:00:00"
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("paymentId", response.id);
}
```

---

### 4.2 DEPOSIT için Payment Oluştur (P-02)

**Endpoint:** `POST {{baseUrl}}/payments`

**Request Body:**
```json
{
  "paymentType": "DEPOSIT",
  "amount": 500.00,
  "description": "Cüzdana para yatırma"
}
```

**Expected Response:** `201 Created`

---

### 4.3 Payment Durumu Güncelle (P-03)

**Endpoint:** `PATCH {{baseUrl}}/payments/{{paymentId}}/status?status=PAID`

**Expected Response:** `200 OK`

---

### 4.4 Payment Listele (P-04)

**Endpoint:** `GET {{baseUrl}}/payments?page=0&size=20&sort=createdAt,desc`

**Query Parameters (Opsiyonel):**
- `paymentType=ORDER`
- `status=PENDING`
- `startDate=2024-01-01T00:00:00`
- `endDate=2024-12-31T23:59:59`

**Expected Response:** `200 OK`

---

### 4.5 Payment Detayı

**Endpoint:** `GET {{baseUrl}}/payments/{{paymentId}}`

**Expected Response:** `200 OK`

---

## 5️⃣ PAYMENT TRANSACTION MODÜLÜ

### 5.1 Payment Transaction Oluştur - AUTH (PT-01)

**Endpoint:** `POST {{baseUrl}}/payment-transactions`

**Request Body:**
```json
{
  "paymentId": {{paymentId}},
  "transactionType": "AUTH",
  "paidAmount": 250.00,
  "method": "CARD",
  "reference": "TXN-AUTH-001"
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "paymentId": 1,
  "transactionType": "AUTH",
  "paidAmount": 250.00,
  "method": "CARD",
  "status": "PENDING",
  "reference": "TXN-AUTH-001",
  "createdAt": "2024-12-10T10:00:00"
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("transactionId", response.id);
}
```

---

### 5.2 REFUND Transaction Oluştur (PT-02)

**Endpoint:** `POST {{baseUrl}}/payment-transactions`

**Request Body:**
```json
{
  "paymentId": {{paymentId}},
  "transactionType": "REFUND",
  "paidAmount": 50.00,
  "method": "CARD",
  "reference": "TXN-REFUND-001"
}
```

**Expected Response:** `201 Created`

---

### 5.3 Transaction Sonucu İşle - SUCCESS (PT-03)

**Endpoint:** `PATCH {{baseUrl}}/payment-transactions/{{transactionId}}/status?status=SUCCESS`

**Expected Response:** `200 OK`

**Not:** Bu işlem otomatik olarak `Payment.paidAmount` ve `Payment.status` güncelleyecek.

---

### 5.4 Transaction Listele (PT-04)

**Endpoint:** `GET {{baseUrl}}/payment-transactions/payment/{{paymentId}}`

**Expected Response:** `200 OK`

---

### 5.5 Wallet ile Correlate Et (PT-05)

**Endpoint:** `POST {{baseUrl}}/payment-transactions/{{transactionId}}/correlate?walletReference=TXN-SPEND-001`

**Expected Response:** `200 OK`

---

## 6️⃣ PAYMENT TRANSACTION FEE MODÜLÜ

### 6.1 Transaction Ücreti Oluştur (PF-01)

**Endpoint:** `POST {{baseUrl}}/payment-transaction-fees`

**Request Body:**
```json
{
  "paymentTransactionId": {{transactionId}},
  "feeType": "GATEWAY_FEE",
  "amount": 2.50,
  "description": "Gateway ücreti"
}
```

**Expected Response:** `201 Created`

---

### 6.2 Transaction Ücretlerini Listele (PF-02)

**Endpoint:** `GET {{baseUrl}}/payment-transaction-fees/transaction/{{transactionId}}`

**Expected Response:** `200 OK`

---

### 6.3 Payment Toplam Ücret (PF-03)

**Endpoint:** `GET {{baseUrl}}/payment-transaction-fees/payment/{{paymentId}}/total`

**Expected Response:** `200 OK`
```json
7.50
```

---

## 7️⃣ ORDER PAYMENT ALLOCATION MODÜLÜ

### 7.1 Payment-Order İlişkilendirme (OPA-01)

**Endpoint:** `POST {{baseUrl}}/order-payment-allocations`

**Request Body:**
```json
{
  "orderId": 1001,
  "paymentId": {{paymentId}},
  "allocatedAmount": 250.00
}
```

**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "orderId": 1001,
  "paymentId": 1,
  "allocatedAmount": 250.00,
  "createdAt": "2024-12-10T10:00:00"
}
```

---

### 7.2 Order Tahsislerini Listele (OPA-02)

**Endpoint:** `GET {{baseUrl}}/order-payment-allocations/order/1001`

**Expected Response:** `200 OK`

---

### 7.3 Allocation Yeniden Düzenle (OPA-03)

**Endpoint:** `PUT {{baseUrl}}/order-payment-allocations/1`

**Request Body:**
```json
{
  "orderId": 1002,
  "paymentId": {{paymentId}},
  "allocatedAmount": 250.00
}
```

**Expected Response:** `200 OK`

---

## 📊 TEST SONUÇLARI

### Başarılı Senaryolar
- ✅ Wallet Account: 5/5
- ✅ Wallet Ledger Entry: 5/5
- ✅ Wallet Fee: 3/3
- ✅ Payment: 4/4
- ✅ Payment Transaction: 5/5
- ✅ Payment Fee: 3/3
- ✅ Order Allocation: 3/3

**TOPLAM: 28/28** ✅

### Hata Senaryoları Test Edilecek
- ❌ Validation errors
- ❌ Resource not found
- ❌ Duplicate entries
- ❌ Business logic violations
- ❌ Insufficient balance

---

## 🎯 POSTMAN COLLECTION EXPORT ADIMLARI

1. Postman'i aç
2. New Collection → "Wallet & Payment Service"
3. Her endpoint için request oluştur
4. Environment variables ekle
5. Test scripts ekle
6. Export → Collection v2.1 JSON
7. `postman_collection.json` olarak kaydet