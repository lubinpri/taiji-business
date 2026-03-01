#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import markdown
from weasyprint import HTML
import sys

# Read markdown file
with open('/Users/aaa/.openclaw/workspace/docs/架构设计文档.md', 'r', encoding='utf-8') as f:
    md_content = f.read()

# Convert markdown to HTML
html_content = markdown.markdown(md_content, extensions=['extra', 'codehilite'])

# Full HTML with CSS
full_html = f'''<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>多元异构数据决策分析平台架构设计文档</title>
    <style>
        body {{
            font-family: "SimSun", "Noto Sans CJK SC", "Microsoft YaHei", sans-serif;
            margin: 40px;
            line-height: 1.6;
            color: #333;
        }}
        h1 {{
            text-align: center;
            color: #1a1a2e;
            font-size: 28px;
            margin-bottom: 30px;
        }}
        h2 {{
            color: #0066cc;
            border-bottom: 2px solid #0066cc;
            padding-bottom: 8px;
            margin-top: 30px;
        }}
        h3 {{
            color: #0099cc;
            margin-top: 20px;
        }}
        h4 {{
            color: #666;
        }}
        table {{
            border-collapse: collapse;
            width: 100%;
            margin: 15px 0;
        }}
        th, td {{
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }}
        th {{
            background-color: #f5f7fa;
            font-weight: bold;
        }}
        code {{
            background-color: #f5f7fa;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: "Consolas", monospace;
        }}
        pre {{
            background-color: #f5f7fa;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            font-size: 12px;
        }}
        blockquote {{
            border-left: 4px solid #0066cc;
            margin: 15px 0;
            padding-left: 15px;
            color: #666;
        }}
        ul, ol {{
            padding-left: 25px;
        }}
        li {{
            margin: 5px 0;
        }}
        hr {{
            border: none;
            border-top: 1px solid #ddd;
            margin: 20px 0;
        }}
    </style>
</head>
<body>
{html_content}
</body>
</html>'''

# Convert to PDF
output_path = '/Users/aaa/.openclaw/workspace/docs/架构设计文档.pdf'
HTML(string=full_html).write_pdf(output_path)

print(f'PDF generated: {output_path}')
