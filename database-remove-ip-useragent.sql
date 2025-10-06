-- 移除 contact_submissions 表中的 ip_address 和 user_agent 欄位
-- 執行前請先備份資料庫
-- MySQL 連接命令: mysql -u root -proot -h localhost dnrush_plus
-- ✅ 已完成執行

USE dnrush_plus;

-- ✅ 已成功刪除 ip_address 欄位
-- ALTER TABLE contact_submissions DROP COLUMN ip_address;

-- ✅ 已成功刪除 user_agent 欄位
-- ALTER TABLE contact_submissions DROP COLUMN user_agent;

-- 驗證欄位是否已刪除
DESCRIBE contact_submissions;

-- 查看修改後的表結構
SHOW CREATE TABLE contact_submissions;

-- 執行結果說明：
-- ✅ 已成功刪除 ip_address 和 user_agent 欄位
-- ✅ Spring Boot 應用程式的 ContactSubmission 實體類已更新
-- ✅ ContactController 已移除相關的處理邏輯
-- ✅ schema.sql 已更新為新的表結構
--
-- 現在表只包含以下欄位：
-- - id (bigint, 主鍵, 自動遞增)
-- - name (varchar(100), 必填)
-- - email (varchar(255), 必填) 
-- - available_time (varchar(100), 可選)
-- - subject (varchar(255), 可選)
-- - message (text, 必填)
-- - is_processed (bit(1), 預設 false)
-- - processed_at (datetime(6), 可選)
-- - created_at (datetime(6), 自動設定)