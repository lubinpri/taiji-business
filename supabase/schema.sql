-- 事业部业务管理系统 - 数据库表结构
-- 在Supabase SQL编辑器中执行

-- =============================================
-- 用户与权限表
-- =============================================

-- 用户表
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email TEXT UNIQUE NOT NULL,
  name TEXT,
  role TEXT NOT NULL CHECK (role IN ('admin', 'sales_manager', 'sales', 'project_manager', 'business')),
  department TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- 商机管理表
-- =============================================

CREATE TABLE opportunities (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  customer_name TEXT NOT NULL,
  amount DECIMAL(15,2),
  stage TEXT NOT NULL DEFAULT '潜在客户' CHECK (stage IN ('潜在客户', '需求确认', '方案报价', '商务谈判', '已签约', '已失效')),
  probability INTEGER DEFAULT 0,
  owner_id UUID REFERENCES users(id),
  contact_person TEXT,
  contact_phone TEXT,
  customer_need TEXT,
  competition_analysis TEXT,
  expected_sign_date DATE,
  priority TEXT DEFAULT '中' CHECK (priority IN ('高', '中', '低')),
  tags TEXT[],
  is_large_confirmed BOOLEAN DEFAULT FALSE,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 报价记录表
CREATE TABLE quotes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  opportunity_id UUID REFERENCES opportunities(id) ON DELETE CASCADE,
  amount DECIMAL(15,2) NOT NULL,
  description TEXT,
  file_url TEXT,
  quote_date DATE NOT NULL,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 跟进记录表
CREATE TABLE follow_ups (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  opportunity_id UUID REFERENCES opportunities(id) ON DELETE CASCADE,
  method TEXT NOT NULL CHECK (method IN ('电话', '面谈')),
  content TEXT NOT NULL,
  attachment_url TEXT,
  follow_date TIMESTAMPTZ DEFAULT NOW(),
  next_follow_date DATE,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- 投标管理表
-- =============================================

CREATE TABLE bids (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  opportunity_id UUID REFERENCES opportunities(id),
  customer_name TEXT NOT NULL,
  bid_amount DECIMAL(15,2),
  bid_deadline DATE,
  project_type TEXT,
  planned_manager_id UUID REFERENCES users(id),
  sales_id UUID REFERENCES users(id),
  status TEXT DEFAULT '准备中' CHECK (status IN ('准备中', '已提交', '已开标', '已中标', '已失标')),
  
  -- 成本预算
  budget_labor DECIMAL(15,2) DEFAULT 0,
  budget_purchase DECIMAL(15,2) DEFAULT 0,
  budget_outsource DECIMAL(15,2) DEFAULT 0,
  budget_travel DECIMAL(15,2) DEFAULT 0,
  budget_other DECIMAL(15,2) DEFAULT 0,
  total_budget DECIMAL(15,2) GENERATED ALWAYS AS (budget_labor + budget_purchase + budget_outsource + budget_travel + budget_other) STORED,
  
  -- 审批
  approval_status TEXT DEFAULT '待审批' CHECK (approval_status IN ('待审批', '已通过', '已拒绝')),
  approved_by UUID REFERENCES users(id),
  approval_note TEXT,
  approval_time TIMESTAMPTZ,
  
  -- 结果
  bid_result TEXT,
  win_amount DECIMAL(15,2),
  win_date DATE,
  lose_reason TEXT,
  contract_status TEXT DEFAULT '未签约' CHECK (contract_status IN ('未签约', '已签约', '已转项目')),
  
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 投标人员明细（人力成本预算）
CREATE TABLE bid_team_members (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  bid_id UUID REFERENCES bids(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id),
  role TEXT,
  planned_days DECIMAL(10,2),
  day_rate DECIMAL(10,2),
  subtotal DECIMAL(15,2) GENERATED ALWAYS AS (planned_days * day_rate) STORED
);

-- =============================================
-- 合同管理表
-- =============================================

CREATE TABLE contracts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  contract_no TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  customer_name TEXT NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  sign_date DATE,
  start_date DATE,
  end_date DATE,
  contract_type TEXT,
  payment_terms TEXT,
  acceptance_criteria TEXT,
  warranty_months INTEGER,
  status TEXT DEFAULT '执行中' CHECK (status IN ('执行中', '待验收', '待结项', '已完成', '已终止')),
  
  opportunity_id UUID REFERENCES opportunities(id),
  bid_id UUID REFERENCES bids(id),
  owner_id UUID REFERENCES users(id),
  
  file_url TEXT,
  current_version INTEGER DEFAULT 1,
  
  -- 回款
  total_received DECIMAL(15,2) DEFAULT 0,
  
  -- 开票
  total_invoiced DECIMAL(15,2) DEFAULT 0,
  
  remark TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 回款记录
CREATE TABLE payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  contract_id UUID REFERENCES contracts(id) ON DELETE CASCADE,
  amount DECIMAL(15,2) NOT NULL,
  payment_date DATE NOT NULL,
  payment_method TEXT,
  remark TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 开票记录
CREATE TABLE invoices (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  contract_id UUID REFERENCES contracts(id) ON DELETE CASCADE,
  amount DECIMAL(15,2) NOT NULL,
  invoice_date DATE NOT NULL,
  invoice_no TEXT,
  invoice_file_url TEXT,
  remark TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 合同变更记录
CREATE TABLE contract_changes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  contract_id UUID REFERENCES contracts(id) ON DELETE CASCADE,
  change_type TEXT NOT NULL,
  field_name TEXT NOT NULL,
  old_value TEXT,
  new_value TEXT,
  reason TEXT,
  approval_status TEXT DEFAULT '待审批',
  approved_by UUID REFERENCES users(id),
  approval_time TIMESTAMPTZ,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- 项目管理表
-- =============================================

CREATE TABLE projects (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_no TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  contract_id UUID REFERENCES contracts(id),
  customer_name TEXT NOT NULL,
  project_type TEXT,
  manager_id UUID REFERENCES users(id),
  sales_id UUID REFERENCES users(id),
  
  status TEXT DEFAULT '筹备中' CHECK (status IN ('筹备中', '进行中', '验收中', '已验收', '已结项', '已终止')),
  progress INTEGER DEFAULT 0,
  risk_level TEXT DEFAULT '正常' CHECK (risk_level IN ('正常', '黄色预警', '红色预警')),
  
  start_date DATE,
  planned_end_date DATE,
  actual_end_date DATE,
  
  -- 成本
  budget_cost DECIMAL(15,2),
  confirmed_cost DECIMAL(15,2) DEFAULT 0,
  cost_confirm_status TEXT DEFAULT '待确认',
  cost_confirmed_by UUID REFERENCES users(id),
  cost_confirmed_at TIMESTAMPTZ,
  
  -- 结项
  completion_status TEXT DEFAULT '待结项',
  completion_file_url TEXT,
  completion_attachments JSONB,
  completed_by UUID REFERENCES users(id),
  completed_at TIMESTAMPTZ,
  
  remark TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 项目参与人员
CREATE TABLE project_members (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id),
  role TEXT,
  planned_days DECIMAL(10,2),
  confirmed_days DECIMAL(10,2) DEFAULT 0
);

-- 里程碑
CREATE TABLE milestones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  planned_date DATE,
  actual_date DATE,
  status TEXT DEFAULT '未开始' CHECK (status IN ('未开始', '进行中', '已完成')),
  source TEXT DEFAULT '模板',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 进度记录
CREATE TABLE progress_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  progress INTEGER NOT NULL,
  note TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 工时确认（每周）
CREATE TABLE time_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id),
  year INTEGER NOT NULL,
  week INTEGER NOT NULL,
  days DECIMAL(10,2) NOT NULL,
  note TEXT,
  confirmed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(project_id, user_id, year, week)
);

-- =============================================
-- 项目成本表
-- =============================================

CREATE TABLE project_costs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
  cost_type TEXT NOT NULL,
  cost_subtype TEXT,
  amount DECIMAL(15,2) NOT NULL,
  cost_date DATE,
  description TEXT,
  invoice_url TEXT,
  created_by UUID REFERENCES users(id),
  approval_status TEXT DEFAULT '待审批',
  approved_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- 部门成本表
-- =============================================

CREATE TABLE department_costs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  department TEXT NOT NULL CHECK (department IN ('销售', '售前', '交付', '集成', '商管')),
  cost_type TEXT NOT NULL,
  cost_subtype TEXT,
  amount DECIMAL(15,2) NOT NULL,
  cost_date DATE NOT NULL,
  description TEXT,
  invoice_url TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- 索引
-- =============================================

CREATE INDEX idx_opportunities_stage ON opportunities(stage);
CREATE INDEX idx_opportunities_owner ON opportunities(owner_id);
CREATE INDEX idx_contracts_customer ON contracts(customer_name);
CREATE INDEX idx_contracts_status ON contracts(status);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_manager ON projects(manager_id);
CREATE INDEX idx_projects_contract ON projects(contract_id);
CREATE INDEX idx_time_logs_project ON time_logs(project_id);
CREATE INDEX idx_time_logs_user ON time_logs(user_id);
CREATE INDEX idx_project_costs_project ON project_costs(project_id);

-- =============================================
-- RLS策略（行级安全）
-- =============================================

ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE opportunities ENABLE ROW LEVEL SECURITY;
ALTER TABLE contracts ENABLE ROW LEVEL SECURITY;
ALTER TABLE projects ENABLE ROW LEVEL SECURITY;
ALTER TABLE bids ENABLE ROW LEVEL SECURITY;

-- 管理员可见全部
CREATE POLICY "admin_full_access" ON users FOR ALL USING (true);

-- 商机：销售只能看自己，销售经理看全部
CREATE POLICY "opportunities_own" ON opportunities FOR SELECT USING (auth.uid() = owner_id);
CREATE POLICY "opportunities_manager" ON opportunities FOR SELECT USING (
  EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND role IN ('admin', 'sales_manager', 'business'))
);

-- 更多RLS策略根据实际需求添加
