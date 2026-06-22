# Setup trên macOS

## 1. Cài đặt Prerequisites

### Homebrew (nếu chưa có)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Java 17
```bash
brew install openjdk@17

# Thêm vào PATH (Zsh)
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"' >> ~/.zshrc
source ~/.zshrc

# Kiểm tra
java -version  # -> openjdk 17.x
```

### Maven 3.9
```bash
brew install maven
mvn -version  # -> Apache Maven 3.9.x
```

### PostgreSQL 16
```bash
brew install postgresql@16

# Khởi động PostgreSQL
brew services start postgresql@16

# Kiểm tra
psql --version
```

### Tomcat 9
```bash
brew install tomcat@9

# Khởi động Tomcat
brew services start tomcat@9

# Kiểm tra: http://localhost:8080

# Đường dẫn webapps (để deploy WAR)
# /usr/local/opt/tomcat@9/libexec/webapps/
```

### Eclipse IDE
- Tải từ: https://www.eclipse.org/downloads/packages/release/2025-06/r/eclipse-ide-enterprise-java-and-web-developers
- Chọn file `.dmg` cho macOS (Apple Silicon hoặc Intel tuỳ máy)
- Kéo vào Applications

## 2. Clone & Import

```bash
cd ~/Documents
git clone https://github.com/KeiTran04/java_ck.git
cd java_ck
```

- Mở Eclipse
- **File → Import → Maven → Existing Maven Projects**
- Browse đến thư mục `~/Documents/java_ck`
- Nhấn Finish (Eclipse tự động download dependencies qua m2e)

## 3. Cấu hình Tomcat trong Eclipse

1. **Window → Preferences → Server → Runtime Environments**
2. **Add → Apache → Apache Tomcat v9.0**
3. Browse đến: `/usr/local/opt/tomcat@9/libexec`
4. Finish

## 4. Tạo Database

```bash
# Kết nối PostgreSQL
psql -U postgres

# Tạo database + chạy script
CREATE DATABASE shopdb;
\c shopdb;
\i ~/Documents/java_ck/src/main/resources/database.sql;
\i ~/Documents/java_ck/src/main/resources/migration.sql;

# Kiểm tra
\dt  # -> phải thấy: cart, categories, coupons, order_details, orders,
     #    password_resets, order_details, product_variants, products, users, wishlists

# Thoát
\q
```

> **Lưu ý**: Nếu gặp lỗi `psql: error: connection refused`, chạy:
> ```bash
> brew services restart postgresql@16
> ```

## 5. Sửa db.properties

Mở file `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/shopdb
db.user=postgres
db.password=<mật khẩu PostgreSQL của bạn>
db.poolSize=10

# SMTP (tuỳ chọn, để placeholder nếu không dùng email)
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=your-email@gmail.com
smtp.password=your-app-password
smtp.from=your-email@gmail.com

# Thư mục lưu invoice (dùng đường dẫn macOS)
invoice.dir=/Users/<tên-user>/shop-invoices
```

## 6. Build & Deploy

### Cách 1: Deploy thủ công qua Terminal
```bash
cd ~/Documents/java_ck
mvn clean package -DskipTests

# Copy WAR vào Tomcat
cp target/shop-web.war /usr/local/opt/tomcat@9/libexec/webapps/

# Đợi 10-15s cho Tomcat auto-deploy
# Mở trình duyệt: http://localhost:8080/shop-web/
```

### Cách 2: Deploy qua Eclipse
1. Right-click project → **Run As → Run on Server**
2. Chọn **Tomcat v9.0 Server** → Next → Add all resources → Finish
3. Eclipse tự build + deploy + mở trình duyệt

## 7. Test

```bash
cd ~/Documents/java_ck
mvn test
# -> 51 tests, 0 failures
```

## 8. Tài khoản mặc định

| Vai trò | Username | Password |
|---------|----------|----------|
| Admin | `admin` | `123456` |
| User | `user1` | `123456` |

## Troubleshooting

### "Java not found" khi chạy Maven
```bash
# Kiểm tra JAVA_HOME
echo $JAVA_HOME
# Nếu trống, thêm vào ~/.zshrc (dòng ở bước 1)
```

### "Address already in use" Tomcat
```bash
# Kiểm tra port 8080
lsof -i :8080
# Kill process
kill -9 <PID>
# Hoặc đổi port trong /usr/local/opt/tomcat@9/libexec/conf/server.xml
```

### "Connection refused" PostgreSQL
```bash
# Kiểm tra PostgreSQL đang chạy
brew services list | grep postgresql

# Nếu chưa chạy:
brew services start postgresql@16

# Kiểm tra port:
lsof -i :5432
```

### Eclipse không nhận Maven project
- Right-click project → Maven → Update Project
- Hoặc: Project → Clean
