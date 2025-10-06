-- 數據庫更新腳本：為 image_resources 表添加年份字段
-- 執行前請確保已連接到 dnrush_plus 數據庫

USE dnrush_plus;

-- 添加年份字段到 image_resources 表
ALTER TABLE image_resources 
ADD COLUMN `year` INT NULL AFTER `description`;

-- 為隊聚活動照片更新年份（根據現有描述中的年份信息）
-- 如果描述中包含年份格式如 "2023年 活動名稱"，則提取年份
UPDATE image_resources 
SET `year` = CASE 
    WHEN category = 'event' AND description REGEXP '^[0-9]{4}年' THEN 
        CAST(SUBSTRING(description, 1, 4) AS UNSIGNED)
    ELSE NULL
END
WHERE category = 'event' AND description IS NOT NULL;

-- 驗證更新結果
SELECT id, category, description, year, created_at 
FROM image_resources 
WHERE category = 'event'
ORDER BY year DESC, created_at DESC;

-- 顯示不同年份的統計
SELECT 
    category,
    year,
    COUNT(*) as image_count
FROM image_resources 
WHERE is_active = true
GROUP BY category, year
ORDER BY category, year DESC;