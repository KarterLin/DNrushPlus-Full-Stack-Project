-- 檢查並創建資料庫
CREATE DATABASE IF NOT EXISTS dnrush_plus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dnrush_plus;

-- 導航欄項目表
DROP TABLE IF EXISTS navigation_items;
CREATE TABLE navigation_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '導航標題',
    url VARCHAR(255) COMMENT '連結URL',
    open_in_new_tab BOOLEAN DEFAULT FALSE COMMENT '是否在新分頁開啟',
    sort_order INT DEFAULT 0 COMMENT '排序順序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 圖片資源表
CREATE TABLE IF NOT EXISTS image_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '圖片名稱',
    original_name VARCHAR(255) COMMENT '原始檔名',
    file_path VARCHAR(500) COMMENT '檔案路徑',
    base64_data LONGTEXT COMMENT 'Base64編碼的圖片資料',
    mime_type VARCHAR(100) COMMENT 'MIME類型',
    file_size BIGINT COMMENT '檔案大小(位元組)',
    category VARCHAR(100) COMMENT '圖片分類(hero, about, team, portfolio等)',
    description TEXT COMMENT '圖片描述',
    year INT COMMENT '年份',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 網站內容表
CREATE TABLE IF NOT EXISTS site_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_key VARCHAR(100) NOT NULL UNIQUE COMMENT '內容鍵值',
    title VARCHAR(255) COMMENT '標題',
    content TEXT COMMENT '內容',
    content_type ENUM('TEXT', 'HTML', 'MARKDOWN') DEFAULT 'TEXT' COMMENT '內容類型',
    section VARCHAR(100) COMMENT '所屬區塊(hero, about, stats等)',
    sort_order INT DEFAULT 0 COMMENT '排序順序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 團隊成員表
CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '成員姓名',
    nickname VARCHAR(100) COMMENT '暱稱',
    position VARCHAR(100) COMMENT '職位',
    description TEXT COMMENT '描述',
    avatar_image_id BIGINT COMMENT '頭像圖片ID',
    facebook_url VARCHAR(255) COMMENT 'Facebook連結',
    instagram_url VARCHAR(255) COMMENT 'Instagram連結',
    youtube_url VARCHAR(255) COMMENT 'YouTube連結',
    github_url VARCHAR(255) COMMENT 'GitHub連結',
    sort_order INT DEFAULT 0 COMMENT '排序順序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (avatar_image_id) REFERENCES image_resources(id) ON DELETE SET NULL
);

-- 聯絡表單提交記錄表
CREATE TABLE IF NOT EXISTS contact_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    email VARCHAR(255) NOT NULL COMMENT '電子郵件',
    available_time VARCHAR(100) COMMENT '可聯繫時間',
    subject VARCHAR(255) COMMENT '主旨',
    message TEXT NOT NULL COMMENT '留言內容',
    is_processed BOOLEAN DEFAULT FALSE COMMENT '是否已處理',
    processed_at TIMESTAMP NULL COMMENT '處理時間',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
