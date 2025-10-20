#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
圖片匯入腳本
將 uploads 資料夾中的圖片檔案匯入到 MySQL 資料庫
"""

import os
import base64
import mysql.connector
from datetime import datetime
import uuid
from pathlib import Path

# 資料庫配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'karter',
    'password': 'seal1127',
    'database': 'dnrush_plus',
    'charset': 'utf8mb4',
    'autocommit': True,
    'use_unicode': True
}

# 上傳資料夾路徑
UPLOADS_DIR = r'c:\Users\bgca0\OneDrive\桌面\DNrushPlus-Full-Stack-Project\uploads'

def get_file_size(file_path):
    """獲取檔案大小"""
    return os.path.getsize(file_path)

def get_mime_type(file_extension):
    """根據副檔名獲取 MIME 類型"""
    mime_types = {
        '.jpg': 'image/jpeg',
        '.jpeg': 'image/jpeg',
        '.png': 'image/png',
        '.gif': 'image/gif',
        '.webp': 'image/webp'
    }
    return mime_types.get(file_extension.lower(), 'image/jpeg')

def file_to_base64(file_path):
    """將檔案轉換為 base64"""
    try:
        # 檢查檔案大小，如果超過 10MB 則跳過
        file_size = os.path.getsize(file_path)
        if file_size > 10 * 1024 * 1024:  # 10MB
            print(f"檔案 {file_path} 太大 ({file_size/1024/1024:.1f}MB)，跳過")
            return None
            
        with open(file_path, 'rb') as file:
            file_data = file.read()
            base64_data = base64.b64encode(file_data).decode('utf-8')
            return base64_data
    except Exception as e:
        print(f"轉換 {file_path} 到 base64 時發生錯誤: {e}")
        return None

def categorize_image(filename):
    """根據檔案名稱或其他規則分類圖片"""
    # 這裡可以根據您的需求調整分類邏輯
    if 'team' in filename.lower():
        return '團隊', 2024
    elif 'event' in filename.lower():
        return '活動', 2024
    elif 'award' in filename.lower():
        return '獎項', 2024
    else:
        return '隊聚', 2024  # 預設分類

def import_images():
    """匯入圖片到資料庫"""
    try:
        # 連接資料庫
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("開始匯入圖片...")
        
        # 獲取 uploads 資料夾中的所有圖片檔案
        image_extensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp']
        image_files = []
        
        for file in os.listdir(UPLOADS_DIR):
            file_path = os.path.join(UPLOADS_DIR, file)
            if os.path.isfile(file_path):
                file_ext = Path(file).suffix
                if file_ext.lower() in image_extensions:
                    image_files.append(file)
        
        print(f"找到 {len(image_files)} 個圖片檔案")
        
        imported_count = 0
        
        for filename in image_files:
            file_path = os.path.join(UPLOADS_DIR, filename)
            
            # 檢查檔案是否已經在資料庫中
            cursor.execute("SELECT id FROM image_resources WHERE original_name = %s", (filename,))
            existing = cursor.fetchone()
            
            if existing:
                print(f"跳過已存在的檔案: {filename}")
                continue
            
            print(f"處理檔案: {filename}")
            
            # 獲取檔案資訊
            file_size = get_file_size(file_path)
            file_ext = Path(filename).suffix
            mime_type = get_mime_type(file_ext)
            category, year = categorize_image(filename)
            
            # 轉換為 base64
            base64_data = file_to_base64(file_path)
            if base64_data is None:
                continue
            
            # 生成顯示名稱 (移除 UUID 部分)
            display_name = filename
            if '-' in filename and len(filename.split('-')[0]) == 8:
                # 如果檔案名稱是 UUID 格式，使用分類作為顯示名稱
                display_name = f"{category}圖片_{imported_count + 1}"
            
            # 插入到資料庫
            insert_query = """
            INSERT INTO image_resources 
            (name, original_name, file_path, file_size, mime_type, base64_data, 
             category, year, description, is_active, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            
            current_time = datetime.now()
            values = (
                display_name,               # name
                filename,                   # original_name
                f"uploads/{filename}",      # file_path
                file_size,                  # file_size
                mime_type,                  # mime_type
                base64_data,               # base64_data
                category,                  # category
                year,                      # year
                f"匯入的{category}圖片",     # description
                True,                      # is_active
                current_time,              # created_at
                current_time               # updated_at
            )
            
            try:
                cursor.execute(insert_query, values)
                connection.commit()  # 立即提交每個插入
                imported_count += 1
                print(f"✅ 成功匯入: {filename} (分類: {category}, 大小: {file_size/1024:.1f}KB)")
            except Exception as e:
                print(f"❌ 匯入 {filename} 時發生錯誤: {e}")
                # 如果連接斷開，重新連接
                if "Lost connection" in str(e):
                    print("重新連接資料庫...")
                    try:
                        connection = mysql.connector.connect(**DB_CONFIG)
                        cursor = connection.cursor()
                    except Exception as reconnect_error:
                        print(f"重新連接失敗: {reconnect_error}")
                        break
        
        # 提交變更 (已在上面每個插入後立即提交)
        print(f"\n🎉 匯入完成！共匯入 {imported_count} 張圖片")
        
        # 顯示統計資訊
        cursor.execute("SELECT category, COUNT(*) FROM image_resources GROUP BY category")
        categories = cursor.fetchall()
        print("\n📊 分類統計:")
        for category, count in categories:
            print(f"  {category}: {count} 張")
        
    except mysql.connector.Error as e:
        print(f"資料庫錯誤: {e}")
    except Exception as e:
        print(f"發生錯誤: {e}")
    finally:
        if 'connection' in locals() and connection.is_connected():
            cursor.close()
            connection.close()
            print("\n資料庫連接已關閉")

if __name__ == "__main__":
    import_images()