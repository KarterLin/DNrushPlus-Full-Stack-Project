-- 初始化導航欄資料
INSERT INTO navigation_items (title, url, sort_order, is_active, open_in_new_tab) VALUES
('首頁', '#hero', 1, TRUE, FALSE),
('車隊介紹', '#about', 2, TRUE, FALSE),
('隊史館', '#services', 3, TRUE, FALSE),
('隊聚活動', '#portfolio', 4, TRUE, FALSE),
('Team', '#team', 5, TRUE, FALSE),
('隊規、入隊標準', '#rules', 6, TRUE, FALSE),
('入隊申請', '#contact', 7, TRUE, FALSE);


-- 初始化網站內容
INSERT INTO site_contents (content_key, title, content, content_type, section, sort_order, is_active) VALUES
('hero_title', 'DiviNe Kartrider Team.', 'DiviNe Kartrider Team.', 'text', 'hero', 1, TRUE),
('hero_subtitle', '車隊介紹', 'DiviNe車隊前身為競速組oDriFTo與道具組oNigHTo，成立於2007年1月3日，為跑跑卡丁車歷史最悠久的車隊之一。', 'text', 'hero', 2, TRUE),
('about_title', '車隊介紹', '車隊介紹', 'text', 'about', 1, TRUE),
('about_content_1', '車隊歷史', 'DiviNe車隊前身為競速組 oDriFTo 與道具組 oNigHTo ，成立於2007年1月3日，為跑跑卡丁車史上歷史最悠久的車隊之一。', 'text', 'about', 2, TRUE),
('about_content_2', '車隊發展', '2007年8月，為避免不同番號下的DN競速組與道具組所造成對外之不同解讀，統一更名為DiviNe，從此之後不再區分競速道具，為現今DiviNe車隊之由來，簡稱為DN車隊。', 'text', 'about', 3, TRUE),
('about_content_3', 'Rush Plus', '跑跑卡丁車Rush+於2020年5月12日正式上線，同時DiviNe車隊由原班人馬於同日正式創立Rush+車隊分部。', 'text', 'about', 4, TRUE),
('services_title', '團隊取得的佳績', '團隊取得的佳績', 'text', 'services', 1, TRUE),
('services_subtitle', '隊史館', '隊史館', 'text', 'services', 2, TRUE),
('portfolio_title', '團隊情誼不僅僅只是線上', '團隊情誼不僅僅只是線上', 'text', 'portfolio', 1, TRUE),
('portfolio_subtitle', '線下隊聚活動', '線下隊聚活動', 'text', 'portfolio', 2, TRUE),
('team_title', 'Team', 'Team', 'text', 'team', 1, TRUE),
('team_subtitle', 'CHECK OUR TEAM', 'CHECK OUR TEAM', 'text', 'team', 2, TRUE),
('rules_title', '填寫入隊申請前請先詳閱', '填寫入隊申請前請先詳閱', 'text', 'rules', 1, TRUE),
('rules_subtitle', '隊規、入隊標準', '隊規、入隊標準', 'text', 'rules', 2, TRUE),
('contact_title', '請留下您的聯繫方式', '請留下您的聯繫方式', 'text', 'contact', 1, TRUE),
('contact_subtitle', '入隊申請', '入隊申請', 'text', 'contact', 2, TRUE);

-- 初始化統計數據
INSERT INTO statistics (stat_key, stat_value, stat_label, icon_class, sort_order, is_active) VALUES
('years_history', '18', 'years of history', 'bi bi-calendar color-blue', 1, TRUE),
('members_count', '100+', 'of members', 'bi bi-people color-orange', 2, TRUE),
('awards_count', '∞', '獲得無數獎項', 'bi bi-trophy color-green', 3, TRUE);

-- 初始化團隊成員
INSERT INTO team_members (name, nickname, position, description, sort_order, is_active) VALUES
('呆虎(虎哥)', '呆虎', '車隊總負責人', '車隊總負責人', 1, TRUE),
('小草Yue', 'Yue', '前職業選手/競速組隊員', '前職業選手/競速組隊員', 2, TRUE),
('小雨', '小雨', '競速組隊長', '競速組隊長', 3, TRUE),
('柴犬', '柴犬', '競速組主考官', '競速組主考官', 4, TRUE);

-- 更新團隊成員的社群連結
UPDATE team_members SET 
    facebook_url = 'https://www.facebook.com/shi.ci.zhan.2025',
    instagram_url = 'https://www.instagram.com/cizhanshi/'
WHERE nickname = '呆虎';

UPDATE team_members SET 
    facebook_url = 'https://www.facebook.com/yue19930929',
    instagram_url = 'https://www.instagram.com/luyue0929/',
    youtube_url = 'https://www.youtube.com/user/spideryue'
WHERE nickname = 'Yue';

UPDATE team_members SET 
    facebook_url = 'https://www.facebook.com/Love.Chang.Wei.Chao',
    instagram_url = 'https://www.instagram.com/loverain_1113/'
WHERE nickname = '小雨';

UPDATE team_members SET 
    facebook_url = 'https://www.facebook.com/chao.n.shu/',
    instagram_url = 'https://www.instagram.com/yunhong_7k/',
    github_url = 'https://github.com/KarterLin/'
WHERE nickname = '柴犬';
