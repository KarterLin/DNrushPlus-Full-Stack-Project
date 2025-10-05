# MySQL 資料庫配置指南

## 方法一：使用 MySQL 命令列

### 1. 連接到 MySQL
```bash
# Windows
mysql -u root -p

# macOS/Linux
sudo mysql -u root -p
```

### 2. 執行資料庫設定腳本
```sql
-- 複製並貼上 database-setup.sql 的內容
-- 或直接執行檔案
source database-setup.sql;
```

### 3. 驗證設定
```sql
-- 檢查資料庫是否創建成功
SHOW DATABASES;

-- 檢查使用者權限
SHOW GRANTS FOR 'dnrush_user'@'localhost';

-- 測試連線
USE dnrush_website;
SELECT 'Database connection successful!' as status;
```

## 方法二：使用 MySQL Workbench

### 1. 開啟 MySQL Workbench
- 連接到本地 MySQL 伺服器

### 2. 創建資料庫
```sql
CREATE DATABASE dnrush_website 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 3. 創建使用者（可選）
```sql
CREATE USER 'dnrush_user'@'localhost' IDENTIFIED BY 'dnrush_password_2024';
GRANT ALL PRIVILEGES ON dnrush_website.* TO 'dnrush_user'@'localhost';
FLUSH PRIVILEGES;
```

### 4. 執行初始化腳本
- 開啟 `src/main/resources/schema.sql`
- 在 MySQL Workbench 中執行

## 方法三：使用 phpMyAdmin

### 1. 開啟 phpMyAdmin
- 通常位於 `http://localhost/phpmyadmin`

### 2. 創建資料庫
- 點擊「新增」
- 資料庫名稱：`dnrush_website`
- 排序規則：`utf8mb4_unicode_ci`

### 3. 創建使用者
- 進入「使用者帳號」頁面
- 點擊「新增使用者帳號」
- 使用者名稱：`dnrush_user`
- 密碼：`dnrush_password_2024`
- 授權：選擇 `dnrush_website` 資料庫，給予所有權限

## 配置選項說明

### application.yml 配置參數

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dnrush_website?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: dnrush_user
    password: dnrush_password_2024
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 參數說明：
- `url`: 資料庫連線 URL
  - `localhost:3306`: MySQL 伺服器位址和埠號
  - `dnrush_website`: 資料庫名稱
  - `useSSL=false`: 關閉 SSL（開發環境）
  - `serverTimezone=UTC`: 設定時區
  - `allowPublicKeyRetrieval=true`: 允許公鑰檢索

- `username`: 資料庫使用者名稱
- `password`: 資料庫密碼
- `driver-class-name`: MySQL JDBC 驅動程式

## 常見問題解決

### 1. 連線被拒絕
```
Error: Access denied for user 'dnrush_user'@'localhost'
```
**解決方案：**
- 檢查使用者名稱和密碼是否正確
- 確認使用者有適當的權限
- 重新執行授權命令

### 2. 資料庫不存在
```
Error: Unknown database 'dnrush_website'
```
**解決方案：**
- 確認資料庫已創建
- 檢查資料庫名稱拼寫
- 執行 `CREATE DATABASE` 命令

### 3. 時區問題
```
Error: The server time zone value 'CST' is unrecognized
```
**解決方案：**
- 在 URL 中加入 `serverTimezone=UTC`
- 或設定 MySQL 時區：`SET time_zone = '+00:00';`

### 4. SSL 連線問題
```
Error: SSL connection error
```
**解決方案：**
- 在 URL 中加入 `useSSL=false`
- 或配置 SSL 憑證

## 測試連線

### 1. 啟動應用程式
```bash
mvn spring-boot:run
```

### 2. 檢查啟動日誌
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

### 3. 訪問應用程式
- 前台：http://localhost:8080
- 管理後台：http://localhost:8080/admin

## 安全建議

### 1. 生產環境配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://your-server:3306/dnrush_website?useSSL=true&serverTimezone=UTC
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### 2. 環境變數
```bash
# 設定環境變數
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

### 3. 防火牆設定
- 限制 MySQL 埠號 3306 的訪問
- 只允許必要的 IP 位址連線

## 備份與還原

### 備份資料庫
```bash
mysqldump -u dnrush_user -p dnrush_website > backup.sql
```

### 還原資料庫
```bash
mysql -u dnrush_user -p dnrush_website < backup.sql
```
