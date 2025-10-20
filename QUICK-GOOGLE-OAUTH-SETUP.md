# 🚀 Google OAuth2 快速設定指南

## ⚡ 立即設定步驟

### 1️⃣ 前往 Google Cloud Console
打開：https://console.cloud.google.com/

### 2️⃣ 創建專案（如果沒有）
- 點擊頂部的專案選擇器
- 點擊「新增專案」
- 輸入專案名稱：`DNrush-Plus-Website`

### 3️⃣ 設定 OAuth 同意畫面
1. 左側選單 → API 和服務 → OAuth 同意畫面
2. 選擇「外部」
3. 填寫必要資訊：
   - 應用程式名稱：`DN Kartrider Team Website`
   - 使用者支援電子郵件：你的 Email
   - 開發人員聯絡資訊：你的 Email
4. 儲存並繼續

### 4️⃣ 創建 OAuth2 憑證
1. 左側選單 → API 和服務 → 憑證
2. 點擊「+ 建立憑證」→「OAuth 用戶端 ID」
3. 應用程式類型：「網路應用程式」
4. 名稱：`DNrush Website OAuth`
5. 已授權的重新導向 URI：
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
6. 點擊「建立」

### 5️⃣ 複製憑證到專案
1. 複製「用戶端 ID」
2. 複製「用戶端密鑰」
3. 在 `src/main/resources/application.yml` 中替換：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 貼上你的用戶端ID
            client-secret: 貼上你的用戶端密鑰
```

### 6️⃣ 重新啟動應用程式
```bash
mvn spring-boot:run
```

### 7️⃣ 測試
- 打開 http://localhost:8080
- 點擊右上角「Google 登入」
- 應該會跳轉到 Google 登入頁面

---

## 🔧 如果遇到問題

**問題：redirect_uri_mismatch**
→ 確保重新導向 URI 完全相符：`http://localhost:8080/login/oauth2/code/google`

**問題：This app isn't verified**
→ 開發階段點擊「Advanced」→「Go to [Your App] (unsafe)」

**問題：invalid_client**
→ 檢查 Client ID 和 Client Secret 是否正確複製

---

## ⏱️ 預估時間：5-10 分鐘