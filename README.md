# 事业部业务管理系统

> 基于 Next.js + Supabase 开发的业务管理系统

## 技术栈

- **前端**: Next.js 14 + React + TypeScript + Tailwind CSS
- **后端**: Next.js API Routes
- **数据库**: Supabase (PostgreSQL)
- **认证**: Supabase Auth
- **部署**: Vercel

## 功能模块

1. **商机管理** - 销售漏斗、跟进记录、报价管理
2. **投标管理** - 成本预算、投标审批
3. **合同管理** - 合同签订、回款管理、开票
4. **项目管理** - 进度跟踪、里程碑、工时管理
5. **成本统计** - 项目成本、部门成本、利润分析
6. **工作看板** - KPI统计、数据可视化

## 快速开始

### 1. 环境配置

```bash
# 安装依赖
npm install

# 复制环境变量
cp .env.example .env.local
# 填写 Supabase 配置
```

### 2. Supabase 设置

1. 创建 Supabase 项目: https://supabase.com
2. 在 SQL 编辑器执行 `supabase/schema.sql`
3. 获取 URL 和 Anon Key 填入 `.env.local`

### 3. 启动开发

```bash
npm run dev
```

访问 http://localhost:3000

### 4. 测试账号

| 角色 | 邮箱 | 密码 |
|------|------|------|
| 管理员 | admin@test.com | Test123456 |
| 销售经理 | salesmanager@test.com | Test123456 |
| 销售人员 | sales@test.com | Test123456 |
| 项目经理 | pm@test.com | Test123456 |
| 商务 | business@test.com | Test123456 |

## 项目结构

```
src/
├── app/              # Next.js 页面
├── components/       # 公共组件
├── lib/              # 工具函数
├── types/            # 类型定义
└── scripts/          # 脚本
supabase/
└── schema.sql        # 数据库表结构
```

## 开发规范

- 使用 TypeScript
- 组件使用函数式组件
- 样式使用 Tailwind CSS
- API 使用 Supabase Client

## 部署

推送到 GitHub 后，在 Vercel 导入项目即可自动部署。

## License

MIT
