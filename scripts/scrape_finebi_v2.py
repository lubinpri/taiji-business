#!/usr/bin/env python3
"""
FineBI 虚拟滚动表格数据抓取脚本 - V2
使用 Playwright 监听所有网络请求来捕获数据
"""

import json
import time
from playwright.sync_api import sync_playwright

def scrape_finebi_network():
    all_requests = []
    
    with sync_playwright() as p:
        try:
            browser = p.chromium.connect_over_cdp("http://127.0.0.1:18800")
            print("✅ 成功连接到浏览器")
        except Exception as e:
            print(f"❌ 连接失败: {e}")
            return
        
        contexts = browser.contexts
        target_page = None
        for ctx in contexts:
            for page in ctx.pages:
                if "databoard" in page.url:
                    target_page = page
                    break
            if target_page:
                break
        
        if not target_page:
            print("❌ 没有找到 FineBI 页面")
            return
        
        print(f"✅ 找到页面: {target_page.url}")
        
        # 监听所有请求和响应
        data_requests = []
        
        def handle_request(request):
            """监听所有请求"""
            url = request.url
            # 打印请求URL
            if any(keyword in url for keyword in ['data', 'query', 'list', 'table', 'decision', 'api']):
                print(f"📤 请求: {url[:100]}")
        
        def handle_response(response):
            """监听所有响应，查找数据"""
            try:
                url = response.url
                # 检查响应内容
                if any(keyword in url for keyword in ['data', 'query', 'list', 'table', 'decision']):
                    # 尝试获取响应体
                    body = response.body()
                    if body and len(body) > 100:
                        # 检查是否包含数据关键字
                        body_str = body.decode('utf-8', errors='ignore')
                        if '用户姓名' in body_str or '建档' in body_str or 'SID' in body_str:
                            print(f"\n📥 找到数据响应: {url}")
                            print(f"   大小: {len(body)} bytes")
                            data_requests.append({
                                'url': url,
                                'body': body_str[:5000]  # 保存前5000字符
                            })
            except Exception as e:
                pass
        
        target_page.on("request", handle_request)
        target_page.on("response", handle_response)
        
        print("\n开始监听网络请求...")
        print("请手动在页面上进行一些操作（如翻页、滚动）来触发数据加载\n")
        
        # 监听60秒
        for i in range(60):
            time.sleep(1)
            if (i + 1) % 10 == 0:
                print(f"已监听 {i+1} 秒...")
            
            # 如果捕获到足够数据就提前结束
            if len(data_requests) >= 5:
                print(f"\n✅ 已捕获 {len(data_requests)} 个数据响应")
                break
        
        # 保存捕获的数据
        if data_requests:
            with open('/Users/aaa/.openclaw/workspace/data/finebi_network_data.json', 'w', encoding='utf-8') as f:
                json.dump(data_requests, f, ensure_ascii=False, indent=2)
            print("✅ 网络数据已保存到 finebi_network_data.json")
        else:
            print("❌ 未捕获到任何数据响应")
        
        browser.close()

if __name__ == "__main__":
    scrape_finebi_network()
