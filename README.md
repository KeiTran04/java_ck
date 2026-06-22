# Shop Web - Java E-Commerce

Full-stack e-commerce web application built with Jakarta EE (Servlet/JSP), PostgreSQL, and Bootstrap 5.

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
- **Product listing** with pagination (12/page), search, category filter, **sort** (price ↑↓, name A→Z), **price range** filter
- **Product detail** with variant selection (color/capacity/size)
- **AJAX cart** — add/update/remove without page reload, persisted to DB across sessions
- **Checkout** with coupon discount (WELCOME10, GIAM50K, FREESHIP) and payment method (COD/BANK/MOMO)
- **Order management** — view order history, cancel pending orders, **download invoice XML**
- **Wishlist** — toggle products via AJAX
- **User profile** — edit full name/phone/address, change password
- **Forgot password** — email reset link with token
- **Dark mode** toggle — persisted in localStorage

### Admin (`/admin/*`)
- **Dashboard** — revenue chart, order/product/user counts
- **Product management** — CRUD with image upload (`@MultipartConfig`)
- **Order management** — update status with confirmation
- **Coupon management** — create/edit/toggle coupons
- **User management** — view registered users

### Technical Highlights
- MVC layered: Controller → Service → DAO → JDBC
- `SecurityFilter` protects all `/admin/*` routes
- `OrderProcessingTask` runs asynchronously: generates invoice XML + sends confirmation email
- Cart synced to `cart` table on every mutation (survives logout/login)
- Responsive design (Bootstrap 5)
- Toast notifications for AJAX operations
- Client-side form validation (Bootstrap + custom JS)

## Database Setup

1. Install PostgreSQL 16
2. Create database and run migration:

```sql
CREATE DATABASE shopdb;
\c shopdb;
\i src/main/resources/database.sql
\i src/main/resources/migration.sql
```

Default admin account: `admin` / `123456`

## Configuration

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/shopdb
db.user=postgres
db.password=020204
db.poolSize=10

# SMTP for order confirmation emails
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=your-email@gmail.com
smtp.password=your-app-password
smtp.from=your-email@gmail.com

# Invoice storage (outside webapp to survive redeployment)
invoice.dir=C:\\shop-data\\invoices
```

## Build & Run

### Prerequisites
- **Java 17** (`JAVA_HOME` set)
- **Maven 3.9+** installed
- **PostgreSQL 16** running
- **Tomcat 9** (Servlet 4 compatible)

### Build
```bash
mvn clean package
```

### Deploy to Tomcat
```bash
# Copy WAR to Tomcat webapps
copy target\shop-web.war C:\tools\apache-tomcat-9.0.118\webapps\
```

Or use Tomcat Manager UI.

### Run
```bash
# Start Tomcat
C:\tools\apache-tomcat-9.0.118\bin\startup.bat
```

App will be available at `http://localhost:8080/shop-web/`

### Test
```bash
mvn test
```

## Project Structure

```
D:\java_ck\
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/shop/
│   │   │   ├── config/       # DataSourceConfig (HikariCP)
│   │   │   ├── controller/   # Servlets (14 files)
│   │   │   ├── dao/          # Data access (8 files)
│   │   │   ├── dto/          # Invoice DTO (JAXB)
│   │   │   ├── filter/       # SecurityFilter
│   │   │   ├── model/        # POJOs (7 files)
│   │   │   ├── service/      # OrderService
│   │   │   ├── task/         # OrderProcessingTask, XmlGenerator
│   │   │   └── util/         # EmailUtil, PasswordUtil
│   │   ├── resources/
│   │   │   ├── db.properties
│   │   │   ├── database.sql  # Schema + seed data
│   │   │   ├── migration.sql # New tables (wishlist, cart, variants, coupons, password_resets)
│   │   │   └── logback.xml
│   │   └── webapp/
│   │       ├── WEB-INF/footer.jsp
│   │       ├── assets/css/style.css
│   │       ├── assets/js/main.js
│   │       ├── admin/        # Admin JSPs (5 files)
│   │       ├── home.jsp, cart.jsp, checkout.jsp, orders.jsp,
│   │       │   product-detail.jsp, wishlist.jsp, profile.jsp,
│   │       │   change-password.jsp, forgot-password.jsp,
│   │       │   reset-password.jsp, login.jsp, register.jsp,
│   │       │   order-success.jsp, error.jsp
│   │       └── assets/images/
│   └── test/java/com/shop/   # Unit tests (51 tests)
└── .gitignore
```

## API Endpoints

### Public
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Redirect to `/home` |
| GET | `/home` | Product listing (search, category, sort, price filter, pagination) |
| GET | `/product?id=N` | Product detail + variants |
| POST | `/login` | Login |
| POST | `/register` | Register |
| GET | `/forgot-password` | Forgot password form |
| POST | `/forgot-password` | Send reset email |
| GET | `/reset-password` | Reset password form (with token) |

### Authenticated
| Method | Path | Description |
|--------|------|-------------|
| GET/POST | `/cart` | View/Add/Update/Remove cart items (AJAX) |
| GET/POST | `/checkout` | Apply coupon / Place order |
| GET | `/orders` | Order history |
| POST | `/orders` | Cancel order |
| GET | `/orders?action=download-invoice&id=N` | Download invoice XML |
| GET/POST | `/wishlist` | List/Toggle wishlist (AJAX) |
| GET/POST | `/profile` | View/Update profile |
| GET/POST | `/profile?action=change-password` | Change password |
| GET | `/logout` | Logout |

### Admin
| Method | Path | Description |
|--------|------|-------------|
| GET | `/admin/dashboard` | Dashboard with charts |
| GET | `/admin/products` | Product list |
| GET/POST | `/admin/products?action=create` | Add product |
| GET/POST | `/admin/products?action=edit&id=N` | Edit product |
| POST | `/admin/products?action=delete&id=N` | Delete product |
| GET | `/admin/orders` | Order list |
| POST | `/admin/orders` | Update order status |
| GET | `/admin/coupons` | Coupon list |
| GET/POST | `/admin/coupons?action=create` | Add coupon |
| GET/POST | `/admin/coupons?action=edit&id=N` | Edit coupon |
| GET | `/admin/coupons?action=toggle&id=N` | Toggle active |
| GET | `/admin/users` | User list |
