-- MySQL 資料庫設定腳本
-- 請在 MySQL 中執行此腳本

-- 1. 創建資料庫
CREATE DATABASE IF NOT EXISTS dnrush_website 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. 創建專用使用者（可選，建議使用）
CREATE USER IF NOT EXISTS 'karter'@'localhost' IDENTIFIED BY 'seal1127';

-- 3. 授權給使用者
GRANT ALL PRIVILEGES ON dnrush_website.* TO 'karter'@'localhost';

-- 4. 重新載入權限
FLUSH PRIVILEGES;

-- 5. 使用資料庫
USE dnrush_website;

-- 6. 顯示資料庫資訊
SHOW DATABASES;
SELECT USER(), DATABASE();
