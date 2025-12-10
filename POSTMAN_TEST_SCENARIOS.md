# ProductController Postman Test Senaryoları

**Base URL:** `http://localhost:8080/api`

**Headers (Tüm istekler için):**
```
Content-Type: application/json
Accept: application/json
```

---

## 📋 Test Senaryoları

### 🔧 ÖNKOŞULLAR (Setup)

#### 1. Brand Oluşturma
**Endpoint:** `POST /api/brands`

**Request Body:**
```json
{
  "name": "Apple"
}
```

**Beklenen Response:** 201 Created
```json
{
  "id": 1,
  "name": "Apple",
  "slug": "apple",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

**Not:** Response'dan `brandId` değerini kaydedin (ör: 1)

---

#### 2. Category Oluşturma
**Endpoint:** `POST /api/categories`

**Request Body:**
```json
{
  "name": "Elektronik",
  "description": "Elektronik ürünler kategorisi"
}
```

**Beklenen Response:** 201 Created
```json
{
  "id": 1,
  "name": "Elektronik",
  "slug": "elektronik",
  "description": "Elektronik ürünler kategorisi",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

**Not:** Response'dan `categoryId` değerini kaydedin (ör: 1)

---

### ✅ BAŞARILI SENARYOLAR

#### 3. Yeni Product Oluşturma (DRAFT)
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "APPLE-IPHONE-15-001",
  "categoryId": 1,
  "brandId": 1,
  "title": "iPhone 15 Pro Max 256GB",
  "description": "Apple iPhone 15 Pro Max 256GB Titanium Blue",
  "status": "DRAFT"
}
```

**Beklenen Response:** 201 Created
```json
{
  "id": 1,
  "barcode": "APPLE-IPHONE-15-001",
  "categoryId": 1,
  "categoryName": "Elektronik",
  "brandId": 1,
  "brandName": "Apple",
  "title": "iPhone 15 Pro Max 256GB",
  "description": "Apple iPhone 15 Pro Max 256GB Titanium Blue",
  "status": "DRAFT",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "attributes": [],
  "images": [],
  "quality": null
}
```

**Not:** Response'dan `productId` değerini kaydedin (ör: 1)

---

#### 4. Yeni Product Oluşturma (Status belirtilmeden - Default DRAFT)
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "APPLE-IPHONE-14-002",
  "categoryId": 1,
  "brandId": 1,
  "title": "iPhone 14 128GB",
  "description": "Apple iPhone 14 128GB Midnight"
}
```

**Beklenen Response:** 201 Created
- `status` alanı `DRAFT` olmalı

---

#### 5. Yeni Product Oluşturma (ACTIVE)
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "APPLE-IPAD-001",
  "categoryId": 1,
  "brandId": 1,
  "title": "iPad Pro 12.9 inch",
  "description": "Apple iPad Pro 12.9 inch 256GB",
  "status": "ACTIVE"
}
```

**Beklenen Response:** 201 Created
- `status` alanı `ACTIVE` olmalı

---

#### 6. ID ile Product Getirme
**Endpoint:** `GET /api/products/{id}`

**Path Variable:** `id = 1` (önceki adımdan kaydettiğiniz productId)

**Beklenen Response:** 200 OK
- Product detayları dönmeli

---

#### 7. Barcode ile Product Getirme
**Endpoint:** `GET /api/products/barcode/{barcode}`

**Path Variable:** `barcode = APPLE-IPHONE-15-001`

**Beklenen Response:** 200 OK
- İlgili product detayları dönmeli

---

#### 8. Tüm Product'ları Listeleme
**Endpoint:** `GET /api/products`

**Beklenen Response:** 200 OK
```json
[
  {
    "id": 1,
    "barcode": "APPLE-IPHONE-15-001",
    ...
  },
  {
    "id": 2,
    "barcode": "APPLE-IPHONE-14-002",
    ...
  }
]
```

---

#### 9. Status'e Göre Product Filtreleme (DRAFT)
**Endpoint:** `GET /api/products/status/DRAFT`

**Beklenen Response:** 200 OK
- Sadece `status = DRAFT` olan product'lar dönmeli

---

#### 10. Status'e Göre Product Filtreleme (ACTIVE)
**Endpoint:** `GET /api/products/status/ACTIVE`

**Beklenen Response:** 200 OK
- Sadece `status = ACTIVE` olan product'lar dönmeli

---

#### 11. Category'ye Göre Product Filtreleme
**Endpoint:** `GET /api/products/category/{categoryId}`

**Path Variable:** `categoryId = 1`

**Beklenen Response:** 200 OK
- Belirtilen category'ye ait tüm product'lar dönmeli

---

#### 12. Brand'e Göre Product Filtreleme
**Endpoint:** `GET /api/products/brand/{brandId}`

**Path Variable:** `brandId = 1`

**Beklenen Response:** 200 OK
- Belirtilen brand'e ait tüm product'lar dönmeli

---

#### 13. Product Arama (Keyword ile)
**Endpoint:** `GET /api/products/search?keyword=iPhone`

**Query Parameter:** `keyword = iPhone`

**Beklenen Response:** 200 OK
- Title veya description'ında "iPhone" geçen product'lar dönmeli

---

#### 14. Product Arama (Farklı Keyword)
**Endpoint:** `GET /api/products/search?keyword=Pro`

**Query Parameter:** `keyword = Pro`

**Beklenen Response:** 200 OK
- "Pro" içeren product'lar dönmeli

---

#### 15. Product Güncelleme (Full Update)
**Endpoint:** `PUT /api/products/{id}`

**Path Variable:** `id = 1`

**Request Body:**
```json
{
  "barcode": "APPLE-IPHONE-15-001-UPDATED",
  "categoryId": 1,
  "brandId": 1,
  "title": "iPhone 15 Pro Max 512GB (Updated)",
  "description": "Apple iPhone 15 Pro Max 512GB Titanium Blue - Updated Description",
  "status": "ACTIVE"
}
```

**Beklenen Response:** 200 OK
- Tüm alanlar güncellenmiş olmalı
- `updatedAt` değişmiş olmalı

---

#### 16. Product Status Güncelleme (DRAFT → ACTIVE)
**Endpoint:** `PATCH /api/products/{id}/status?status=ACTIVE`

**Path Variable:** `id = 1`
**Query Parameter:** `status = ACTIVE`

**Beklenen Response:** 200 OK
- Sadece `status` alanı `ACTIVE` olarak güncellenmiş olmalı
- Diğer alanlar değişmemeli

---

#### 17. Product Status Güncelleme (ACTIVE → ARCHIVED)
**Endpoint:** `PATCH /api/products/{id}/status?status=ARCHIVED`

**Path Variable:** `id = 1`
**Query Parameter:** `status = ARCHIVED`

**Beklenen Response:** 200 OK
- `status` alanı `ARCHIVED` olmalı

---

### ❌ HATA SENARYOLARı

#### 18. Duplicate Barcode ile Product Oluşturma
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "APPLE-IPHONE-15-001",
  "categoryId": 1,
  "brandId": 1,
  "title": "Duplicate Product",
  "description": "Bu barcode zaten kullanılıyor"
}
```

**Beklenen Response:** 409 Conflict
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Product with barcode 'APPLE-IPHONE-15-001' already exists",
  "path": "/api/products"
}
```

---

#### 19. Geçersiz Category ID ile Product Oluşturma
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "INVALID-CATEGORY-001",
  "categoryId": 99999,
  "brandId": 1,
  "title": "Invalid Category Product",
  "description": "Bu category mevcut değil"
}
```

**Beklenen Response:** 404 Not Found
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Category with id '99999' not found",
  "path": "/api/products"
}
```

---

#### 20. Geçersiz Brand ID ile Product Oluşturma
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "INVALID-BRAND-001",
  "categoryId": 1,
  "brandId": 99999,
  "title": "Invalid Brand Product",
  "description": "Bu brand mevcut değil"
}
```

**Beklenen Response:** 404 Not Found
- Brand bulunamadı hatası

---

#### 21. Validation Hatası - Barcode Boş
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "",
  "categoryId": 1,
  "brandId": 1,
  "title": "Invalid Product",
  "description": "Barcode boş"
}
```

**Beklenen Response:** 400 Bad Request
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": [
    "barcode: Barcode is required"
  ],
  "path": "/api/products"
}
```

---

#### 22. Validation Hatası - Title Çok Kısa
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "SHORT-TITLE-001",
  "categoryId": 1,
  "brandId": 1,
  "title": "AB",
  "description": "Title çok kısa (min 3 karakter)"
}
```

**Beklenen Response:** 400 Bad Request
- Title validation hatası

---

#### 23. Validation Hatası - Barcode Geçersiz Format
**Endpoint:** `POST /api/products`

**Request Body:**
```json
{
  "barcode": "INVALID@BARCODE#001",
  "categoryId": 1,
  "brandId": 1,
  "title": "Invalid Barcode Format",
  "description": "Barcode özel karakter içeriyor"
}
```

**Beklenen Response:** 400 Bad Request
- Barcode format validation hatası

---

#### 24. Olmayan Product ID ile Getirme
**Endpoint:** `GET /api/products/99999`

**Beklenen Response:** 404 Not Found
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id '99999' not found",
  "path": "/api/products/99999"
}
```

---

#### 25. Olmayan Barcode ile Getirme
**Endpoint:** `GET /api/products/barcode/NON-EXISTENT-BARCODE`

**Beklenen Response:** 404 Not Found
- Product bulunamadı hatası

---

#### 26. Olmayan Product ID ile Güncelleme
**Endpoint:** `PUT /api/products/99999`

**Request Body:**
```json
{
  "barcode": "UPDATE-NON-EXISTENT",
  "categoryId": 1,
  "brandId": 1,
  "title": "Update Non Existent",
  "description": "Bu product mevcut değil"
}
```

**Beklenen Response:** 404 Not Found
- Product bulunamadı hatası

---

#### 27. Olmayan Product ID ile Silme
**Endpoint:** `DELETE /api/products/99999`

**Beklenen Response:** 404 Not Found
- Product bulunamadı hatası

---

#### 28. Product Silme (Başarılı)
**Endpoint:** `DELETE /api/products/{id}`

**Path Variable:** `id = 1` (mevcut bir product ID)

**Beklenen Response:** 204 No Content
- Response body boş olmalı

---

#### 29. Silinen Product'ı Tekrar Getirme
**Endpoint:** `GET /api/products/{id}`

**Path Variable:** `id = 1` (az önce sildiğiniz product ID)

**Beklenen Response:** 404 Not Found
- Product artık bulunamamalı

---

## 📝 Test Senaryoları Özeti

### Başarılı Senaryolar (17 adet)
1. ✅ Brand oluşturma
2. ✅ Category oluşturma
3. ✅ Product oluşturma (DRAFT)
4. ✅ Product oluşturma (default status)
5. ✅ Product oluşturma (ACTIVE)
6. ✅ ID ile product getirme
7. ✅ Barcode ile product getirme
8. ✅ Tüm product'ları listeleme
9. ✅ Status'e göre filtreleme (DRAFT)
10. ✅ Status'e göre filtreleme (ACTIVE)
11. ✅ Category'ye göre filtreleme
12. ✅ Brand'e göre filtreleme
13. ✅ Keyword ile arama
14. ✅ Farklı keyword ile arama
15. ✅ Product güncelleme
16. ✅ Status güncelleme (DRAFT → ACTIVE)
17. ✅ Status güncelleme (ACTIVE → ARCHIVED)

### Hata Senaryoları (12 adet)
18. ❌ Duplicate barcode
19. ❌ Geçersiz category ID
20. ❌ Geçersiz brand ID
21. ❌ Barcode boş
22. ❌ Title çok kısa
23. ❌ Barcode geçersiz format
24. ❌ Olmayan product ID ile getirme
25. ❌ Olmayan barcode ile getirme
26. ❌ Olmayan product ID ile güncelleme
27. ❌ Olmayan product ID ile silme
28. ✅ Product silme (başarılı)
29. ❌ Silinen product'ı tekrar getirme

---

## 🚀 Postman Collection Oluşturma İpuçları

1. **Environment Variables Oluşturun:**
   - `baseUrl`: `http://localhost:8080`
   - `brandId`: `{{brandId}}`
   - `categoryId`: `{{categoryId}}`
   - `productId`: `{{productId}}`

2. **Test Scripts Ekleyin:**
   ```javascript
   // Response'dan ID'yi kaydetme
   if (pm.response.code === 201 || pm.response.code === 200) {
       const response = pm.response.json();
       if (response.id) {
           pm.environment.set("productId", response.id);
       }
   }
   ```

3. **Pre-request Scripts:**
   - Brand ve Category oluşturma işlemlerini collection seviyesinde pre-request script olarak ekleyebilirsiniz

4. **Test Assertions:**
   ```javascript
   pm.test("Status code is 201", function () {
       pm.response.to.have.status(201);
   });
   
   pm.test("Response has product data", function () {
       const jsonData = pm.response.json();
       pm.expect(jsonData).to.have.property('id');
       pm.expect(jsonData).to.have.property('barcode');
   });
   ```

---

## 📌 Önemli Notlar

- Test sırası önemlidir! Önce Brand ve Category oluşturmalısınız
- Her test sonrası oluşturulan ID'leri kaydedin
- Silme işlemlerini en sona bırakın
- Aynı barcode ile iki kez product oluşturmayı denemeyin (duplicate test hariç)
- Status değerleri: `DRAFT`, `ACTIVE`, `ARCHIVED` (büyük/küçük harf duyarlı)

