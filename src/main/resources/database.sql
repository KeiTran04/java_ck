CREATE DATABASE shopdb;
\c shopdb;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    register_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500) DEFAULT NULL,
    description TEXT DEFAULT '',
    category_id INT REFERENCES categories(id)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    invoice_path VARCHAR(500) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_details (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO categories (name, slug) VALUES
(N'Điện thoại', 'dien-thoai'),
(N'Laptop', 'laptop'),
(N'Máy tính bảng', 'may-tinh-bang'),
(N'Phụ kiện', 'phu-kien'),
(N'Đồng hồ thông minh', 'dong-ho');

INSERT INTO products (name, price, stock, image_url, description, category_id) VALUES
(N'iPhone 16 Pro Max', 34990000, 15, 'iphone15.jpg', N'Flagship mới nhất từ Apple, chip A18 Pro, camera 48MP', 1),
(N'iPhone 15 Pro Max', 29990000, 10, 'iphone15.jpg', N'Chip A17 Pro, màn hình OLED 6.7 inch', 1),
(N'Samsung Galaxy S24 Ultra', 25990000, 12, 'samsung_s24.jpg', N'Galaxy AI, bút S Pen, camera 200MP', 1),
(N'Samsung Galaxy Z Fold6', 35990000, 5, NULL, N'Màn hình gập 7.6 inch, chip Snapdragon 8 Gen 3', 1),
(N'Xiaomi 14 Pro', 18990000, 8, NULL, N'Leica camera, chip Snapdragon 8 Gen 3, sạc nhanh 120W', 1),
(N'OPPO Find X7 Ultra', 21990000, 6, NULL, N'Camera Hasselblad, màn hình LTPO AMOLED', 1),
(N'MacBook Air M3', 27990000, 8, 'macbook_air_m3.jpg', N'Chip Apple M3, RAM 8GB, SSD 256GB, màn hình 13.6 inch', 2),
(N'MacBook Pro 14 M3 Pro', 42990000, 5, NULL, N'Chip M3 Pro 12-core, RAM 18GB, SSD 512GB', 2),
(N'Dell XPS 15', 35990000, 4, NULL, N'Intel Core i7-13700H, RTX 4060, RAM 16GB, 512GB SSD', 2),
(N'Lenovo ThinkPad X1 Carbon Gen 11', 38990000, 3, NULL, N'Intel Core i7, 16GB RAM, 512GB SSD, màn hình 14 inch 2K', 2),
(N'ASUS ROG Zephyrus G14', 32990000, 6, NULL, N'AMD Ryzen 9, RTX 4070, RAM 32GB, 1TB SSD, gaming', 2),
(N'iPad Pro M4 11 inch', 24990000, 10, NULL, N'Chip Apple M4, màn hình Ultra Retina XDR 120Hz', 3),
(N'Samsung Galaxy Tab S9 Ultra', 22990000, 7, NULL, N'Màn hình 14.6 inch Dynamic AMOLED 2X, bút S Pen', 3),
(N'Xiaomi Pad 6 Pro', 9990000, 15, NULL, N'Chip Snapdragon 8+ Gen 1, màn hình 11 inch 144Hz', 3),
(N'AirPods Pro 2', 5490000, 25, 'airpods_pro2.jpg', N'Chip H2, chống ồn chủ động 2x, Adaptive Audio', 4),
(N'Apple Watch Ultra 2', 19990000, 8, NULL, N'Titanium, màn hình 49mm, GPS + Cellular', 5),
(N'Apple Watch Series 9', 10990000, 12, 'apple_watch_s9.jpg', N'Chip S9, Always-On Retina, oxygen sensor', 5),
(N'Samsung Galaxy Watch 6 Classic', 8990000, 10, NULL, N'Rotating bezel, BioActive sensor, Wear OS', 5),
(N'Loa Bluetooth JBL Flip 6', 2490000, 20, NULL, N'Chống nước IP67, 30W, 12 tiếng pin', 4),
(N'Chuột Logitech MX Master 3S', 1790000, 18, NULL, N'Cảm biến 8000DPI, silent click, pin 70 ngày', 4);
