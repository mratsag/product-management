# 🛍️ Product Information Management (PIM) System

Modern, ölçeklenebilir ve RESTful bir Ürün Bilgi Yönetim Sistemi. E-ticaret ve pazaryeri platformları için tasarlanmış, kapsamlı ürün katalog yönetimi sağlar.

## 📋 İçindekiler

- [Özellikler](#özellikler)
- [Teknoloji Stack](#teknoloji-stack)
- [Kurulum](#kurulum)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Proje Yapısı](#proje-yapısı)
- [Geliştirme Standartları](#geliştirme-standartları)

---

## ✨ Özellikler

### ✅ Tamamlanan Modüller

#### 🏷️ Brand Yönetimi
- Marka oluşturma, güncelleme, silme
- Slug tabanlı URL yapısı
- Otomatik slug oluşturma (Türkçe karakter desteği)
- Duplicate kontrolü

#### 📁 Category Yönetimi (Hiyerarşik)
- Sınırsız seviye kategori ağacı
- Parent-child ilişki yönetimi
- Kategori taşıma (move) özelliği
- Circular reference koruması
- Alt kategori kontrolü ile güvenli silme

---

## 🛠️ Teknoloji Stack

### Backend
- **Java 21** - Modern Java özellikleri
- **Spring Boot 4.0** - Application framework
- **Spring Data JPA** - ORM ve database işlemleri
- **Hibernate 7.1.8** - JPA implementation
- **MySQL 8.0** - Relational database
- **Flyway** - Database migration
- **Lombok** - Boilerplate kod azaltma
- **Bean Validation** - Input validation

### DevOps & Tools
- **Docker** - Containerization
- **Maven** - Dependency management
- **Git** - Version control

---

## 🚀 Kurulum

### Gereksinimler

- Java 21+
- Docker & Docker Compose
- Maven 3.8+
- Git

### 1️⃣ Projeyi Klonlayın
```bash
git clone <repository-url>
cd product-management
```

### 2️⃣ MySQL Container'ı Başlatın
```bash
docker-compose up -d
```

MySQL şu bilgilerle çalışacak:
- **Host:** localhost:3307
- **Database:** pim_db
- **Username:** pim_user
- **Password:** pim_pass123

### 3️⃣ Uygulamayı Çalıştırın
```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışmaya başlayacak.

### 4️⃣ Database Migration

Flyway otomatik olarak database schema'yı oluşturacak. İlk çalıştırmada 7 tablo oluşturulur.

---

## 📡 API Endpoints

### Brand API

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/brands` | Yeni marka oluştur |
| GET | `/api/brands` | Tüm markaları listele |
| GET | `/api/brands/{id}` | ID ile marka getir |
| GET | `/api/brands/slug/{slug}` | Slug ile marka getir |
| PUT | `/api/brands/{id}` | Marka güncelle |
| DELETE | `/api/brands/{id}` | Marka sil |

#### Brand Oluşturma Örneği
```bash
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Apple"
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Apple",
  "slug": "apple",
  "createdAt": "2025-12-10T11:00:00",
  "updatedAt": "2025-12-10T11:00:00"
}
```

---

### Category API

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/categories` | Yeni kategori oluştur |
| GET | `/api/categories` | Tüm kategorileri listele (flat) |
| GET | `/api/categories/tree` | Kategori ağacı (hiyerarşik) |
| GET | `/api/categories/{id}` | ID ile kategori getir |
| GET | `/api/categories/slug/{slug}` | Slug ile kategori getir |
| GET | `/api/categories/{parentId}/subcategories` | Alt kategorileri getir |
| PUT | `/api/categories/{id}` | Kategori güncelle |
| PATCH | `/api/categories/{id}/move` | Kategori taşı |
| DELETE | `/api/categories/{id}` | Kategori sil |

#### Kategori Oluşturma Örneği

**Ana Kategori:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Elektronik",
    "description": "Elektronik ürünler kategorisi",
    "order": 1
  }'
```

**Alt Kategori:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "parentCategoryId": 1,
    "name": "Bilgisayar",
    "description": "Bilgisayar ve aksesuarları",
    "order": 1
  }'
```

#### Kategori Ağacı Örneği
```bash
curl http://localhost:8080/api/categories/tree
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Elektronik",
    "slug": "elektronik",
    "subCategories": [
      {
        "id": 2,
        "parentCategoryId": 1,
        "name": "Bilgisayar",
        "slug": "bilgisayar",
        "subCategories": [
          {
            "id": 3,
            "parentCategoryId": 2,
            "name": "Laptop",
            "slug": "laptop",
            "subCategories": []
          }
        ]
      }
    ]
  }
]
```

---

## 🗄️ Database Schema

### Tablolar

#### brands
```sql
- id (PK, AUTO_INCREMENT)
- name (VARCHAR, NOT NULL)
- slug (VARCHAR, UNIQUE, NOT NULL)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### categories
```sql
- id (PK, AUTO_INCREMENT)
- parent_category_id (FK -> categories.id)
- name (VARCHAR, NOT NULL)
- description (TEXT)
- slug (VARCHAR, UNIQUE, NOT NULL)
- display_order (INT)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### products
```sql
- id (PK, AUTO_INCREMENT)
- barcode (VARCHAR, UNIQUE, NOT NULL)
- category_id (FK -> categories.id)
- brand_id (FK -> brands.id)
- title (VARCHAR, NOT NULL)
- description (TEXT)
- status (ENUM: DRAFT, ACTIVE, ARCHIVED)
- created_at (DATETIME)
- updated_at (DATETIME)
```

#### product_attributes
```sql
- id (PK, AUTO_INCREMENT)
- product_id (FK -> products.id)
- attribute_key (VARCHAR)
- attribute_value (TEXT)
```

#### product_images
```sql
- id (PK, AUTO_INCREMENT)
- product_id (FK -> products.id)
- image_url (VARCHAR)
- alt_text (VARCHAR)
- display_order (INT)
```

#### quality
```sql
- id (PK, AUTO_INCREMENT)
- product_id (FK -> products.id, UNIQUE)
- score (INT)
- result (JSON)
- created_at (DATETIME)
- updated_at (DATETIME)
```

---

## 📂 Proje Yapısı
```
src/main/java/com/pim/product_management/
├── config/              # Konfigürasyon sınıfları
│   └── JpaConfig.java
├── controller/          # REST Controllers
│   ├── BrandController.java
│   └── CategoryController.java
├── dto/                 # Data Transfer Objects
│   ├── request/
│   │   ├── BrandRequest.java
│   │   └── CategoryRequest.java
│   └── response/
│       ├── BrandResponse.java
│       ├── CategoryResponse.java
│       └── ErrorResponse.java
├── entity/              # JPA Entities
│   ├── BaseEntity.java
│   ├── Brand.java
│   ├── Category.java
│   ├── Product.java
│   ├── ProductAttribute.java
│   ├── ProductImage.java
│   └── Quality.java
├── enums/               # Enum sınıfları
│   └── ProductStatus.java
├── exception/           # Custom Exceptions
│   ├── DuplicateResourceException.java
│   ├── ResourceNotFoundException.java
│   └── handler/
│       └── GlobalExceptionHandler.java
├── repository/          # Spring Data JPA Repositories
│   ├── BrandRepository.java
│   ├── CategoryRepository.java
│   ├── ProductRepository.java
│   ├── ProductAttributeRepository.java
│   ├── ProductImageRepository.java
│   └── QualityRepository.java
├── service/             # Business Logic
│   ├── BrandService.java
│   ├── CategoryService.java
│   └── impl/
│       ├── BrandServiceImpl.java
│       └── CategoryServiceImpl.java
└── util/                # Utility Classes
    └── SlugGenerator.java

src/main/resources/
├── application.yml      # Application configuration
└── db/migration/        # Flyway migrations
    └── V1__init_schema.sql
```

---

## 🎯 Geliştirme Standartları

### Code Style

- ✅ **Clean Code:** Anlamlı değişken isimleri, kısa metodlar (20-30 satır)
- ✅ **SOLID Principles:** Her sınıf tek sorumluluk
- ✅ **DRY:** Kod tekrarından kaçınma
- ✅ **Logging:** Her önemli işlem loglanır

### Git Commit Standartları
```
feat(brand): add barcode validation
fix(category): resolve circular reference bug
refactor(product): extract slug generation to utility
docs(readme): update API documentation
test(brand): add unit tests for create service
```

**Format:**
```
<type>(<scope>): <subject>

[optional body]
```

**Types:**
- `feat`: Yeni özellik
- `fix`: Bug düzeltme
- `refactor`: Kod iyileştirme
- `docs`: Dokümantasyon
- `test`: Test ekleme/düzenleme
- `chore`: Build, config değişiklikleri

---

## 🔜 Gelecek Özellikler

### 🚧 Yapılacaklar (Roadmap)

#### Phase 3 - Product Yönetimi
- [ ] Product CRUD operations
- [ ] Barcode validation
- [ ] Product status management (DRAFT, ACTIVE, ARCHIVED)
- [ ] Bulk operations

#### Phase 4 - Product Attributes
- [ ] Dynamic attribute ekleme
- [ ] Attribute search
- [ ] Attribute validation

#### Phase 5 - Product Images
- [ ] Image upload (File storage)
- [ ] Multiple image support
- [ ] Image ordering
- [ ] Thumbnail generation

#### Phase 6 - Quality Management
- [ ] Quality score calculation
- [ ] Quality rules engine
- [ ] Quality reports

#### Phase 7 - Search & Filter
- [ ] Full-text search (Elasticsearch)
- [ ] Advanced filtering
- [ ] Faceted search

#### Phase 8 - API Documentation
- [ ] Swagger/OpenAPI integration
- [ ] API versioning
- [ ] Rate limiting

#### Phase 9 - Security
- [ ] JWT Authentication
- [ ] Role-based authorization (RBAC)
- [ ] API key management

#### Phase 10 - Testing
- [ ] Unit tests (JUnit 5)
- [ ] Integration tests
- [ ] Test coverage >80%

---

## 📊 Mevcut Durum

### ✅ Tamamlanan Özellikler

| Modül | CRUD | Validation | Exception Handling | Tests |
|-------|------|------------|-------------------|-------|
| Brand | ✅ | ✅ | ✅ | ⏳ |
| Category | ✅ | ✅ | ✅ | ⏳ |
| Product | ⏳ | ⏳ | ⏳ | ⏳ |
| ProductAttribute | ⏳ | ⏳ | ⏳ | ⏳ |
| ProductImage | ⏳ | ⏳ | ⏳ | ⏳ |
| Quality | ⏳ | ⏳ | ⏳ | ⏳ |

### 📈 İstatistikler

- **Total Endpoints:** 14
- **Database Tables:** 7
- **Entities:** 7
- **Repositories:** 6
- **Services:** 2
- **Controllers:** 2

---

## 🧪 Test Etme

### Postman Collection

Projeye Postman collection eklenecek. Şimdilik manuel test için curl komutları kullanabilirsiniz.

### Test Senaryosu Örnekleri

#### Brand Testi
```bash
# 1. Brand oluştur
curl -X POST http://localhost:8080/api/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "Samsung"}'

# 2. Tüm markaları listele
curl http://localhost:8080/api/brands

# 3. Slug ile getir
curl http://localhost:8080/api/brands/slug/samsung
```

#### Category Hiyerarşi Testi
```bash
# 1. Ana kategori
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Elektronik", "order": 1}'

# 2. Alt kategori
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"parentCategoryId": 1, "name": "Telefon", "order": 1}'

# 3. Kategori ağacını görüntüle
curl http://localhost:8080/api/categories/tree
```

---

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'feat: add amazing feature'`)
4. Branch'i push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

---

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

---

## 👨‍💻 Geliştirici

**Murat Sağ**

---

## 📞 İletişim

Sorularınız için issue açabilirsiniz.

---

## 🙏 Teşekkürler

Bu projeyi geliştirirken kullanılan teknolojiler ve açık kaynak topluluğa teşekkürler!

---

**Son Güncelleme:** 10 Aralık 2025