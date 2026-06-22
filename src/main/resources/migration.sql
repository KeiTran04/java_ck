-- Migration: Thêm các bảng và cột cho tính năng mới
-- Chạy lệnh này sau database.sql

-- Profile fields cho users
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(100) DEFAULT '';
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT '';
ALTER TABLE users ADD COLUMN IF NOT EXISTS address TEXT DEFAULT '';

-- Thêm coupon_id và discount_amount cho orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS coupon_id INT DEFAULT NULL;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount DOUBLE PRECISION DEFAULT 0;

-- Thêm payment_method cho orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20) DEFAULT 'COD';

-- Wishlist
CREATE TABLE IF NOT EXISTS wishlists (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    product_id INT NOT NULL REFERENCES products(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

-- Product variants (color, size, capacity)
CREATE TABLE IF NOT EXISTS product_variants (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(id),
    variant_type VARCHAR(20) NOT NULL,
    variant_name VARCHAR(100) NOT NULL,
    price_adjustment DOUBLE PRECISION DEFAULT 0,
    stock INT NOT NULL DEFAULT 0
);

-- Coupons
CREATE TABLE IF NOT EXISTS coupons (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DOUBLE PRECISION NOT NULL,
    min_order_amount DOUBLE PRECISION DEFAULT 0,
    max_usage INT DEFAULT 0,
    used_count INT DEFAULT 0,
    expiry_date TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Password reset tokens
CREATE TABLE IF NOT EXISTS password_resets (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    token VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cart table for persistent cart
CREATE TABLE IF NOT EXISTS cart (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    product_id INT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL DEFAULT 1,
    UNIQUE(user_id, product_id)
);

-- Seed coupons
INSERT INTO coupons (code, discount_type, discount_value, min_order_amount, max_usage, expiry_date) VALUES
('WELCOME10', 'PERCENTAGE', 10, 100000, 100, '2027-12-31 23:59:59'),
('GIAM50K', 'FIXED', 50000, 200000, 50, '2027-12-31 23:59:59'),
('FREESHIP', 'FIXED', 30000, 0, 200, '2027-12-31 23:59:59');

-- Seed product variants for some products
INSERT INTO product_variants (product_id, variant_type, variant_name, price_adjustment, stock) VALUES
-- iPhone 16 Pro Max - colors
(1, 'COLOR', 'Titan tự nhiên', 0, 5),
(1, 'COLOR', 'Titan xanh', 0, 4),
(1, 'COLOR', 'Titan trắng', 0, 3),
(1, 'COLOR', 'Titan đen', 0, 3),
-- iPhone 16 Pro Max - capacities
(1, 'CAPACITY', '256GB', 0, 5),
(1, 'CAPACITY', '512GB', 3000000, 8),
(1, 'CAPACITY', '1TB', 6000000, 2),
-- MacBook Air M3 - colors
(7, 'COLOR', 'Bạc', 0, 3),
(7, 'COLOR', 'Xám', 0, 3),
(7, 'COLOR', 'Vàng', 0, 2),
-- MacBook Air M3 - capacities
(7, 'CAPACITY', '256GB', 0, 4),
(7, 'CAPACITY', '512GB', 3000000, 4);
