# Google OAuth2 登入設定指南

## 1. 在 Google Cloud Console 設定 OAuth2 應用程式

### 步驟 1: 創建 Google Cloud 專案
1. 前往 [Google Cloud Console](https://console.cloud.google.com/)
2. 登入您的 Google 帳戶
3. 點擊頂部導覽列的專案選擇器
4. 點擊「新增專案」
5. 輸入專案名稱 (例如: "DNrush-Plus-Website")
6. 點擊「建立」

### 步驟 2: 啟用 Google+ API
1. 在左側選單中，點擊「API 和服務」> 「程式庫」
2. 搜尋 "Google+ API" 或 "People API"
3. 點擊並啟用該 API

### 步驟 3: 設定 OAuth 同意畫面
1. 在左側選單中，點擊「API 和服務」> 「OAuth 同意畫面」
2. 選擇「外部」用戶類型 (除非您有 Google Workspace 帳戶)
3. 填寫必要資訊:
   - 應用程式名稱: DN Kartrider Team Website
   - 使用者支援電子郵件: 您的電子郵件地址
   - 開發人員聯絡資訊: 您的電子郵件地址
4. 點擊「儲存並繼續」
5. 在「範圍」頁面，點擊「新增或移除範圍」
6. 添加以下範圍:
   - `../auth/userinfo.email`
   - `../auth/userinfo.profile`
   - `openid`
7. 點擊「儲存並繼續」

### 步驟 4: 創建 OAuth2 憑證
1. 在左側選單中，點擊「API 和服務」> 「憑證」
2. 點擊「+ 建立憑證」> 「OAuth 用戶端 ID」
3. 選擇「網路應用程式」
4. 設定名稱: "DNrush Plus Website OAuth"
5. 在「已授權的 JavaScript 來源」中新增:
   - `http://localhost:8080`
   - 您的實際網域 (如果已部署)
6. 在「已授權的重新導向 URI」中新增:
   - `http://localhost:8080/login/oauth2/code/google`
   - 您的實際網域 + `/login/oauth2/code/google` (如果已部署)
7. 點擊「建立」

### 步驟 5: 複製憑證
1. 創建完成後，會顯示用戶端 ID 和用戶端密鑰
2. 複製這兩個值，你將需要在 application.yml 中使用它們

## 2. 更新 application.yml 配置

在 `src/main/resources/application.yml` 中，將以下部分：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID  # 替換為您的 Client ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET  # 替換為您的 Client Secret
```

替換為您從 Google Cloud Console 獲得的實際值。

## 3. 測試登入功能

1. 啟動應用程式: `mvn spring-boot:run`
2. 打開瀏覽器並前往 `http://localhost:8080`
3. 點擊右上角的「Google 登入」按鈕
4. 完成 Google 登入流程
5. 確認用戶資訊正確顯示在右上角

## 4. 設置管理員權限

第一次登入後，您可能需要手動將您的帳戶設為管理員：

1. 查看資料庫中的 `users` 表格
2. 找到您的用戶記錄
3. 將 `role` 欄位從 `USER` 更改為 `ADMIN`

或者，您可以創建一個臨時的管理介面來設置管理員權限。

## 5. 生產環境部署注意事項

當部署到生產環境時，請記得：

1. 在 Google Cloud Console 中添加您的實際網域到授權來源和重新導向 URI
2. 使用環境變數或安全的配置管理來存儲 Client ID 和 Client Secret
3. 啟用 CSRF 保護 (在 SecurityConfig.java 中)
4. 考慮添加額外的安全措施，如速率限制

## 常見問題

### 問題 1: "Error 400: redirect_uri_mismatch"
**解決方案**: 確保 Google Cloud Console 中的重新導向 URI 與應用程式的 URL 完全匹配。

### 問題 2: "This app isn't verified"
**解決方案**: 在開發階段，點擊「Advanced」> 「Go to [Your App Name] (unsafe)」。在生產環境中，您需要通過 Google 的驗證流程。

### 問題 3: 無法存取管理員功能
**解決方案**: 確保您的用戶在資料庫中的角色設為 `ADMIN`。