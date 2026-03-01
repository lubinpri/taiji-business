#!/usr/bin/env python3
"""
FineBI 虚拟滚动表格数据抓取脚本
使用 Playwright 连接到已运行的 Chrome 浏览器
"""

import json
import time
from playwright.sync_api import sync_playwright

def scrape_finebi_table():
    all_data = []
    page_size = 100
    
    with sync_playwright() as p:
        # 连接到已有的 Chrome 浏览器（通过CDP）
        try:
            browser = p.chromium.connect_over_cdp("http://127.0.0.1:18800")
            print("✅ 成功连接到浏览器")
        except Exception as e:
            print(f"❌ 连接失败: {e}")
            print("请确保 OpenClaw 浏览器正在运行")
            return
        
        # 获取所有上下文和页面
        contexts = browser.contexts
        if not contexts:
            print("❌ 没有找到浏览器上下文")
            return
        
        # 找到包含 FineBI 的页面
        target_page = None
        for ctx in contexts:
            for page in ctx.pages:
                if "databoard" in page.url:
                    target_page = page
                    break
            if target_page:
                break
        
        if not target_page:
            print("❌ 没有找到 FineBI 页面，请确保页面已打开")
            return
        
        print(f"✅ 找到页面: {target_page.url}")
        
        # 设置网络监听来捕获数据请求
        api_data = []
        
        def handle_response(response):
            """监听网络响应，查找数据接口"""
            url = response.url
            # 查找可能的数据接口
            if "data" in url.lower() or "query" in url.lower() or "list" in url.lower():
                try:
                    body = response.json()
                    if isinstance(body, dict):
                        # 检查是否是数据响应
                        if "data" in body or "list" in body or "rows" in body:
                            print(f"📡 捕获到数据请求: {url}")
                            api_data.append(body)
                except:
                    pass
        
        target_page.on("response", handle_response)
        
        # 等待页面加载完成
        target_page.wait_for_load_state("networkidle")
        time.sleep(2)
        
        print("\n开始滚动表格以触发懒加载...")
        
        # 循环滚动以触发 FineBI 的懒加载
        scroll_count = 0
        max_scrolls = 50  # 最多滚动50次
        
        for i in range(max_scrolls):
            scroll_count += 1
            # 滚动页面
            target_page.evaluate("""
                () => {
                    const tableContainer = document.querySelector('.ant-table-body');
                    if (tableContainer) {
                        tableContainer.scrollTop = tableContainer.scrollTop + 500;
                    } else {
                        window.scrollBy(0, 500);
                    }
                }
            """)
            
            time.sleep(0.5)  # 等待数据加载
            
            # 尝试从DOM获取当前可见的数据
            rows = target_page.evaluate("""
                () => {
                    const rows = document.querySelectorAll('.ant-table-tbody tr');
                    const data = [];
                    rows.forEach(row => {
                        const cells = row.querySelectorAll('td');
                        const rowData = [];
                        cells.forEach(cell => rowData.push(cell.textContent.trim()));
                        if (rowData.length > 0) data.push(rowData);
                    });
                    return data;
                }
            """)
            
            if rows:
                print(f"  滚动 #{i+1}: 获取到 {len(rows)} 行数据")
                all_data.extend(rows)
            
            # 每10次滚动检查一次是否有新的API数据
            if (i + 1) % 10 == 0 and api_data:
                print(f"\n📊 已捕获 {len(api_data)} 个API响应")
                # 保存API数据
                with open('/Users/aaa/.openclaw/workspace/data/api_responses.json', 'w', encoding='utf-8') as f:
                    json.dump(api_data, f, ensure_ascii=False, indent=2)
                print("✅ API响应已保存到 api_responses.json")
        
        print(f"\n✅ 滚动完成，共滚动 {scroll_count} 次")
        print(f"📊 DOM中获取的总行数: {len(all_data)}")
        
        # 去重
        unique_data = []
        seen = set()
        for row in all_data:
            row_key = str(row)
            if row_key not in seen:
                seen.add(row_key)
                unique_data.append(row)
        
        print(f"📊 去重后的行数: {len(unique_data)}")
        
        # 保存DOM数据
        with open('/Users/aaa/.openclaw/workspace/data/scraped_data.json', 'w', encoding='utf-8') as f:
            json.dump(unique_data, f, ensure_ascii=False, indent=2)
        
        print("✅ 数据已保存到 scraped_data.json")
        
        browser.close()

if __name__ == "__main__":
    scrape_finebi_table()
