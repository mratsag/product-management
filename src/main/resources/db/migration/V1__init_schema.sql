-- Brands Table
CREATE TABLE brands (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        slug VARCHAR(120) NOT NULL UNIQUE,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        INDEX idx_brands_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categories Table
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            parent_category_id BIGINT,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            slug VARCHAR(120) NOT NULL UNIQUE,
                            display_order INT NOT NULL DEFAULT 0,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,
                            INDEX idx_categories_parent (parent_category_id),
                            INDEX idx_categories_slug (slug),
                            FOREIGN KEY (parent_category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Products Table
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          barcode VARCHAR(50) NOT NULL UNIQUE,
                          category_id BIGINT NOT NULL,
                          brand_id BIGINT NOT NULL,
                          title VARCHAR(200) NOT NULL,
                          description TEXT,
                          status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,
                          INDEX idx_products_barcode (barcode),
                          INDEX idx_products_category (category_id),
                          INDEX idx_products_brand (brand_id),
                          INDEX idx_products_status (status),
                          FOREIGN KEY (category_id) REFERENCES categories(id),
                          FOREIGN KEY (brand_id) REFERENCES brands(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product Attributes Table
CREATE TABLE product_attributes (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    product_id BIGINT NOT NULL,
                                    attribute_key VARCHAR(100) NOT NULL,
                                    attribute_value TEXT,
                                    INDEX idx_product_attributes_product (product_id),
                                    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product Images Table
CREATE TABLE product_images (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                image_url VARCHAR(500) NOT NULL,
                                alt_text VARCHAR(200),
                                display_order INT NOT NULL DEFAULT 0,
                                INDEX idx_product_images_product (product_id),
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quality Table
CREATE TABLE quality (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         product_id BIGINT NOT NULL UNIQUE,
                         score INT NOT NULL DEFAULT 0,
                         result JSON,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         INDEX idx_quality_product (product_id),
                         FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;