# Shop Web - Java E-Commerce

Full-stack e-commerce web application built with Jakarta EE (Servlet/JSP), PostgreSQL 16, Bootstrap 5.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | JSP + JSTL/EL, Bootstrap 5, Chart.js, DataTables |
| **Backend** | Jakarta EE (Servlet 4, JSP 2.3), Java 17 |
| **Database** | PostgreSQL 16 + HikariCP |
| **Build** | Maven 3.9, WAR deployment |
| **Auth** | SHA-256 hashing, session-based |
| **Async** | ExecutorService for order processing + email |
| **XML** | JAXB for invoice generation |
| **Logging** | SLF4J + Logback |

## Features

### Customer
- **Product listing** — pagination (12/page), search, category filter, sort (price ↑↓, name A→Z), price range filter
- **Product detail** — variant selection (color/capacity/size)
- **AJAX cart** — add/update/remove without page reload, persisted to DB across sessions
- **Checkout** — coupon discount (WELCOME10, GIAM50K, FREESHIP), payment method (COD/BANK/MOMO)
- **Orders** — view history, cancel pending orders, download invoice XML
- **Wishlist** — toggle products via AJAX
- **Profile** — edit full name/phone/address, change password
- **Forgot password** — email reset link with token
- **Dark mode** — persisted in localStorage

### Admin (`/admin/*`)
- **Dashboard** — revenue chart, order/product/user counts
- **Products** — CRUD with image upload
- **Orders** — update status with confirmation
- **Coupons** — create/edit/toggle coupons
- **Users** — view registered users

---

## Setup trên macOS

### 1. Cài đặt Prerequisites (Homebrew)

```bash
# Homebrew (nếu chưa có)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Java 17
brew install openjdk@17
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"' >> ~/.zshrc
source ~/.zshrc
java -version   # openjdk 17.x

# Maven
brew install maven
mvn -version    # Apache Maven 3.9.x

# PostgreSQL 16
brew install postgresql@16
brew services start postgresql@16

# Tomcat 9
brew install tomcat@9
brew services start tomcat@9
# Kiểm tra: http://localhost:8080
```

### 2. Clone project

```bash
cd ~/Documents
git clone https://github.com/KeiTran04/java_ck.git
cd java_ck
```

### 3. Tạo Database

```bash
psql -U postgres
```

```sql
CREATE DATABASE shopdb;
\c shopdb;
\i ~/Documents/java_ck/src/main/resources/database.sql;
\i ~/Documents/java_ck/src/main/resources/migration.sql;
\dt   -- kiểm tra: cart, categories, coupons, order_details, orders,
       -- password_resets, product_variants, products, users, wishlists
\q
```

### 4. Cấu hình

Sửa `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/shopdb
db.user=postgres
db.password=<mật-khẩu-của-bạn>
db.poolSize=10

# SMTP (tuỳ chọn)
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=your-email@gmail.com
smtp.password=your-app-password
smtp.from=your-email@gmail.com

# Invoice storage
invoice.dir=/Users/<tên-user>/shop-invoices
```

### 5. Build & Deploy

```bash
# Build WAR
mvn clean package -DskipTests

# Copy vào Tomcat
cp target/shop-web.war /usr/local/opt/tomcat@9/libexec/webapps/

# Đợi 10-15s cho Tomcat auto-deploy
# Mở: http://localhost:8080/shop-web/
```

### 6. Chạy Tests

```bash
mvn test
# Kết quả: 51 tests, 0 failures
```

### 7. Tài khoản mặc định

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `123456` |
| User | `user1` | `123456` |

---

## Cấu trúc project

```
java_ck/
├── pom.xml
├── src/main/java/com/shop/
│   ├── config/       # DataSourceConfig (HikariCP)
│   ├── controller/   # 14 Servlets
│   ├── dao/          # 8 DAOs (Cart, Category, Coupon, Order, Product, ProductVariant, User, Wishlist)
│   ├── dto/          # Invoice DTO (JAXB)
│   ├── filter/       # SecurityFilter
│   ├── model/        # 7 POJOs
│   ├── service/      # OrderService, ProductService, UserService
│   ├── task/         # OrderProcessingTask, XmlGenerator
│   └── util/         # EmailUtil, PasswordUtil
├── src/main/resources/
│   ├── db.properties
│   ├── database.sql      # Schema + seed
│   ├── migration.sql     # Wishlist, cart, variants, coupons, password_resets
│   └── logback.xml
├── src/main/webapp/
│   ├── admin/            # 5 JSP quản trị
│   ├── assets/css/style.css
│   ├── assets/js/main.js
│   ├── assets/images/
│   └── *.jsp             # Customer pages
└── src/test/             # 51 unit tests
```

## API Endpoints

### Public
| Method | Path | Mô tả |
|--------|------|-------|
| GET | `/` | Redirect → `/home` |
| GET | `/home` | Danh sách sản phẩm (search, category, sort, price filter, phân trang) |
| GET | `/product?id=N` | Chi tiết sản phẩm + variants |
| POST | `/login` | Đăng nhập |
| POST | `/register` | Đăng ký |
| GET | `/forgot-password` | Form quên mật khẩu |
| POST | `/forgot-password` | Gửi email reset |
| GET | `/reset-password` | Form đặt lại mật khẩu |

### Authenticated
| Method | Path | Mô tả |
|--------|------|-------|
| GET/POST | `/cart` | Xem/Thêm/Sửa/Xoá giỏ hàng (AJAX) |
| GET/POST | `/checkout` | Áp dụng coupon / Đặt hàng |
| GET | `/orders` | Lịch sử đơn hàng |
| POST | `/orders` | Huỷ đơn (PENDING) |
| GET | `/orders?action=download-invoice&id=N` | Tải hoá đơn XML |
| GET/POST | `/wishlist` | Danh sách/Thêm/Xoá yêu thích (AJAX) |
| GET/POST | `/profile` | Xem/Sửa thông tin |
| GET/POST | `/profile?action=change-password` | Đổi mật khẩu |
| GET | `/logout` | Đăng xuất |

### Admin
| Method | Path | Mô tả |
|--------|------|-------|
| GET | `/admin/dashboard` | Thống kê (biểu đồ doanh thu) |
| GET | `/admin/products` | Danh sách sản phẩm |
| GET/POST | `/admin/products?action=create` | Thêm sản phẩm |
| GET/POST | `/admin/products?action=edit&id=N` | Sửa sản phẩm |
| GET | `/admin/orders` | Danh sách đơn hàng |
| POST | `/admin/orders` | Cập nhật trạng thái |
| GET | `/admin/coupons` | Danh sách mã giảm giá |
| GET/POST | `/admin/coupons?action=create` | Thêm mã |
| GET/POST | `/admin/coupons?action=edit&id=N` | Sửa mã |
| GET | `/admin/coupons?action=toggle&id=N` | Bật/Tắt mã |
| GET | `/admin/users` | Danh sách người dùng |

## Troubleshooting

### Java not found
```bash
echo $JAVA_HOME   # phải trỏ đến openjdk@17
```

### Tomcat port conflict
```bash
lsof -i :8080
kill -9 <PID>
# hoặc sửa port trong /usr/local/opt/tomcat@9/libexec/conf/server.xml
```

### PostgreSQL connection refused
```bash
brew services restart postgresql@16
lsof -i :5432
```
