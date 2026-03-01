#!/usr/bin/env python3
"""
在用户的Chrome中运行此脚本，自动捕获FineBI的网络请求
使用方法：
1. 打开Chrome，进入FineBI页面
2. 运行此脚本: python3 capture_api.py
3. 在页面上进行操作（翻页、刷新等）
4. 脚本会自动捕获并打印API请求
5. 按 Ctrl+C 结束
"""

from playwright.sync_api import sync_playwright
import json

def capture_api():
    print("=" * 60)
    print("FineBI API 捕获器")
    print("=" * 60)
    print("\n请在浏览器中进行以下操作：")
    print("1. 刷新页面")
    print("2. 点击分页")
    print("3. 滚动表格")
    print("\n脚本将自动捕获API请求...\n")
    print("=" * 60)
    
    with sync_playwright() as p:
        # 启动带开发者工具的浏览器
        browser = p.chromium.launch(
            headless=False,
            devtools=True  # 自动打开开发者工具
        )
        
        page = browser.new_page()
        
        # 提示用户登录
        print("请在浏览器中登录FineBI...")
        input("登录完成后，按回车继续...")
        
        # 监听所有网络请求
        api_requests = []
        
        def handle_request(request):
            url = request.url
            # 只关注可能包含数据的请求
            if any(keyword in url.lower() for keyword in 
                   ['decision', 'query', 'data', 'list', 'table', 'bi', 'report', '.do']):
                api_requests.append({
                    'url': url,
                    'method': request.method,
                    'post_data': request.post_data
                })
                print(f"\n📥 请求捕获:")
                print(f"   方法: {request.method}")
                print(f"   URL: {url[:150]}")
                if request.post_data:
                    print(f"   数据: {request.post_data[:200]}")
        
        page.on("request", handle_request)
        
        print("\n开始捕获... (按 Ctrl+C 停止)")
        print("-" * 60)
        
        try:
            while True:
                pass
        except KeyboardInterrupt:
            print("\n\n" + "=" * 60)
            print(f"共捕获 {len(api_requests)} 个相关请求")
            print("=" * 60)
            
            # 保存到文件
            with open('finebi_api_capture.json', 'w', encoding='utf-8') as f:
                json.dump(api_requests, f, ensure_ascii=False, indent=2)
            print("\n请求已保存到 finebi_api_capture.json")
            
            # 打印可能的API端点
            print("\n可能的API端点:")
            urls = set()
            for req in api_requests:
                # 提取基础URL
                url = req['url']
                if '?' in url:
                    base = url.split('?')[0]
                    urls.add(base)
            
            for url in urls:
                print(f"  - {url}")
        
        browser.close()

if __name__ == "__main__":
    capture_api()
