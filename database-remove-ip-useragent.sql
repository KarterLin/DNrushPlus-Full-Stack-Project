-- 移除 contact_submissions 表中的 ip_address 和 user_agent 欄位
-- 執行前請先備份資料庫
-- 請在 MySQL 客戶端中執行以下命令

USE dnrush_plus;

-- 刪除 ip_address 欄位
ALTER TABLE contact_submissions DROP COLUMN IF EXISTS ip_address;

-- 刪除 user_agent 欄位  
ALTER TABLE contact_submissions DROP COLUMN IF EXISTS user_agent;

-- 驗證欄位是否已刪除
DESCRIBE contact_submissions;

-- 查看修改後的表結構
SHOW CREATE TABLE contact_submissions;