# DN Rush Plus 車隊官方網站

這是一個基於Spring Boot MVC架構的遊戲車隊官方網站，支援前後端整合、圖片Base64處理、內容管理等功能。

## 專案特色

- **Spring Boot MVC架構**: 使用Java 25 + Spring Boot 3.2.0
- **MySQL資料庫**: 完整的資料庫設計和初始化腳本
- **圖片Base64處理**: 支援圖片上傳、轉換為Base64格式存儲
- **動態導航欄**: 後端控制導航欄內容和結構
- **內容管理系統**: 支援網站內容的動態管理
- **管理後台**: 提供完整的管理後台功能
- **響應式設計**: 基於Bootstrap的現代化UI

## 技術棧

### 後端
- Java 25
- Spring Boot 3.2.0
- Spring Data JPA
- Spring MVC
- Thymeleaf
- MySQL 8.0
- Maven

### 前端
- HTML5/CSS3/JavaScript
- Bootstrap 5
- Thymeleaf模板引擎
- 響應式設計

## 專案結構

```
src/
├── main/
│   ├── java/com/dnrush/
│   │   ├── config/          # 配置類
│   │   ├── controller/      # 控制器
│   │   ├── entity/          # 實體類
│   │   ├── repository/      # 資料存取層
│   │   ├── service/         # 業務邏輯層
│   │   └── util/            # 工具類
│   └── resources/
│       ├── static/          # 靜態資源
│       ├── templates/       # Thymeleaf模板
│       ├── application.yml  # 應用配置
│       ├── schema.sql       # 資料庫結構
│       └── data.sql         # 初始資料
```

## 資料庫設計

### 主要資料表
- `navigation_items`: 導航欄項目
- `image_resources`: 圖片資源（支援Base64和年份分類）
- `site_contents`: 網站內容
- `team_members`: 團隊成員
- `contact_submissions`: 聯絡表單提交記錄

## 功能特色

### 1. 動態導航欄
- 支援多層級導航結構
- 後端控制導航內容

### 2. 圖片Base64處理
- 圖片上傳自動轉換為Base64
- 支援多種圖片格式
- 圖片分類管理（hero、about、event、service、rules）
- 年份分類支援（隊聚活動照片）

### 3. 內容管理系統
- 動態網站內容管理
- 支援多種內容類型
- 區塊化管理

### 4. 管理後台
- 完整的後台管理介面
- 支援所有資料的CRUD操作
- 聯絡表單管理

## 安裝與運行

### 環境要求
- Java 25+
- MySQL 8.0+
- Maven 3.6+

### 安裝步驟

1. **克隆專案**
```bash
git clone <repository-url>
cd dnrush-plus-website
```

2. **配置資料庫**
```sql
-- 創建資料庫
CREATE DATABASE dnrush_plus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置**
編輯 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dnrush_plus?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

4. **編譯運行**
```bash
mvn clean install
mvn spring-boot:run
```

5. **訪問網站**
- 前台: http://localhost:8080
- 管理後台: http://localhost:8080/admin

## API 端點

### 前台API
- `GET /api/navigation` - 獲取導航欄
- `GET /api/content/{contentKey}` - 獲取網站內容
- `GET /api/images/category/{category}` - 獲取分類圖片
- `GET /api/team` - 獲取團隊成員

### 後台API
- `POST /contact/submit` - 提交聯絡表單
- `GET /admin/*` - 管理後台頁面
- `POST /admin/*` - 後台資料操作

## 部署說明

### 1. 打包應用
```bash
mvn clean package -DskipTests
```

### 2. 運行JAR檔案
```bash
java -jar target/dnrush-plus-website-0.0.1-SNAPSHOT.jar
```

### 3. Docker部署（可選）
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/dnrush-plus-website-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 開發指南

### 新增功能模組
1. 創建Entity實體類
2. 創建Repository介面
3. 創建Service服務類
4. 創建Controller控制器
5. 創建Thymeleaf模板

### 圖片處理
使用 `ImageService` 進行圖片上傳和Base64轉換：
```java
@Autowired
private ImageService imageService;

// 上傳圖片
ImageResource image = imageService.saveImage(file, "category", "description");
```

### 內容管理
使用 `SiteContentService` 管理網站內容：
```java
@Autowired
private SiteContentService siteContentService;

// 獲取內容
String content = siteContentService.getContentValue("content_key");
```

## 貢獻指南

1. Fork 專案
2. 創建功能分支
3. 提交變更
4. 推送到分支
5. 創建 Pull Request

## 授權

本專案採用 MIT 授權條款。

## 聯絡資訊

- 專案維護者: Karter Lin
- Email: bgca0610@gmail.com
- 網站: [DN車隊官方網站](http://localhost:8080)

## 更新日誌

### v1.2.0 (2025-10-07)
- 數據庫架構優化：移除未使用的表格（event_photos, statistics）
- 圖片管理系統改進：支援年份分類
- 管理後台優化：整合真實數據統計
- 隱私保護：移除IP地址和User-Agent追蹤
- 代碼清理：移除冗餘功能和未使用代碼

### v1.1.0 (2025-10-06)
- 新增年份管理功能
- 優化圖片管理界面
- 改進管理後台功能
- 新增隊聚活動照片分類

### v1.0.0 (2025-10-05)
- 初始版本發布
- 完整的Spring Boot MVC架構
- 支援圖片Base64處理
- 動態導航欄和內容管理
- 管理後台功能
