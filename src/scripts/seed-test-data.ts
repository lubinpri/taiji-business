// 测试数据生成脚本
// 运行: npx tsx src/scripts/seed-test-data.ts

interface TestData {
  users: any[];
  opportunities: any[];
  contracts: any[];
  projects: any[];
  bids: any[];
}

export const testData: TestData = {
  // 5个测试用户（不同角色）
  users: [
    { email: 'admin@test.com', name: '系统管理员', role: 'admin', department: '技术部' },
    { email: 'salesmanager@test.com', name: '张销售经理', role: 'sales_manager', department: '销售部' },
    { email: 'sales@test.com', name: '李销售', role: 'sales', department: '销售部' },
    { email: 'pm@test.com', name: '王项目经理', role: 'project_manager', department: '交付部' },
    { email: 'business@test.com', name: '赵商务', role: 'business', department: '商管部' },
  ],

  // 商机数据
  opportunities: [
    { name: '某医院信息化建设', customer_name: '某市人民医院', amount: 800000, stage: '潜在客户', priority: '高' },
    { name: '某企业OA系统', customer_name: '某科技公司', amount: 350000, stage: '需求确认', priority: '中' },
    { name: '某局网络安全改造', customer_name: '某公安局', amount: 1200000, stage: '方案报价', priority: '高' },
    { name: '某高校智慧校园', customer_name: '某大学', amount: 2000000, stage: '商务谈判', priority: '高' },
    { name: '某企业ERP实施', customer_name: '某制造企业', amount: 600000, stage: '已签约', priority: '中' },
    { name: '某政府网站升级', customer_name: '某政府办', amount: 450000, stage: '潜在客户', priority: '低' },
    { name: '某银行数据治理', customer_name: '某银行', amount: 3000000, stage: '需求确认', priority: '高' },
    { name: '某医院his系统', customer_name: '某专科医院', amount: 550000, stage: '方案报价', priority: '中' },
  ],

  // 合同数据
  contracts: [
    { contract_no: 'HT-2024-001', name: '某企业信息化合同', customer_name: '某企业', amount: 500000, status: '执行中' },
    { contract_no: 'HT-2024-002', name: '某医院网络安全合同', customer_name: '某医院', amount: 800000, status: '已完成' },
    { contract_no: 'HT-2024-003', name: '某局系统集成合同', customer_name: '某局', amount: 1200000, status: '执行中' },
  ],

  // 项目数据
  projects: [
    { project_no: 'XM-2024-001', name: '某企业ERP项目', customer_name: '某企业', status: '进行中', progress: 65, risk_level: '正常' },
    { project_no: 'XM-2024-002', name: '某医院网络安全项目', customer_name: '某医院', status: '已结项', progress: 100, risk_level: '正常' },
    { project_no: 'XM-2024-003', name: '某局集成项目', customer_name: '某局', status: '进行中', progress: 30, risk_level: '黄色预警' },
    { project_no: 'XM-2024-004', name: '某高校智慧校园项目', customer_name: '某大学', status: '筹备中', progress: 0, risk_level: '正常' },
  ],

  // 投标数据
  bids: [
    { name: '某医院信息系统投标', customer_name: '某医院', bid_amount: 900000, status: '已中标', bid_result: '已中标', win_amount: 880000 },
    { name: '某企业数字化投标', customer_name: '某企业', bid_amount: 450000, status: '已失标', bid_result: '已失标', lose_reason: '价格过高' },
    { name: '某政府云平台投标', customer_name: '某政府', bid_amount: 2500000, status: '已提交', bid_result: null },
  ],
};

// 测试账号密码统一为: Test123456
