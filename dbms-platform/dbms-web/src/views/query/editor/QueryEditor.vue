<template>
  <div class="query-container">
    <el-row :gutter="16" style="height: 100%">
      <!-- 左侧：SQL编辑器和结果 -->
      <el-col :span="16" style="height: 100%">
        <el-card shadow="hover" class="editor-card">
          <template #header>
            <div class="editor-header">
              <span class="header-title">SQL编辑器</span>
              <div class="header-actions">
                <el-select v-model="selectedInstance" placeholder="选择数据库" style="width: 180px" @change="handleInstanceChange">
                  <el-option v-for="item in instances" :key="item.id" :label="item.instanceName" :value="item.id">
                    <div style="display: flex; justify-content: space-between">
                      <span>{{ item.instanceName }}</span>
                      <el-tag size="small" :type="item.status === 1 ? 'success' : 'danger'">{{ item.status === 1 ? '在线' : '离线' }}</el-tag>
                    </div>
                  </el-option>
                </el-select>
                <el-button type="primary" :icon="Cpu" @click="handleExecute" :loading="executing" :disabled="!selectedInstance">
                  执行 (Ctrl+Enter)
                </el-button>
                <el-button :icon="Brush" @click="handleFormat" title="格式化SQL">格式化</el-button>
                <el-button :icon="CopyDocument" @click="handleCopy" title="复制SQL">复制</el-button>
              </div>
            </div>
          </template>
          
          <div class="sql-editor">
            <textarea
              v-model="sqlContent"
              class="editor-textarea"
              placeholder="请输入SQL语句，支持 SELECT、INSERT、UPDATE、DELETE..."
              @keydown.ctrl.enter="handleExecute"
              @keydown.meta.enter="handleExecute"
            ></textarea>
          </div>
          
          <div class="editor-toolbar">
            <el-dropdown @command="handleSaveTemplate">
              <el-button type="text" :icon="DocumentAdd">
                保存模板
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="save">保存当前SQL</el-dropdown-item>
                  <el-dropdown-item command="saveas">另存为新模板</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-dropdown @command="handleLoadTemplate">
              <el-button type="text" :icon="FolderOpened">
                加载模板
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="tpl in templates" :key="tpl.id" :command="tpl.id">
                    {{ tpl.name }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-divider direction="vertical" />
            <el-button type="text" :icon="Delete" @click="handleClear" title="清空编辑器">清空</el-button>
            <el-divider direction="vertical" />
            <el-tooltip content="显示/隐藏脱敏字段">
              <el-switch v-model="showMasked" active-text="脱敏" inactive-text="" />
            </el-tooltip>
            <el-tooltip content="添加查询水印">
              <el-switch v-model="enableWatermark" active-text="水印" inactive-text="" />
            </el-tooltip>
          </div>
        </el-card>
        
        <!-- 查询结果 -->
        <el-card shadow="hover" class="result-card">
          <template #header>
            <div class="result-header">
              <span>查询结果</span>
              <div class="result-actions">
                <el-tag v-if="queryResult.length > 0" type="success" size="small">
                  {{ queryResult.length }} 条 / {{ executionTime }}ms
                </el-tag>
                <el-tag v-if="affectedRows > 0" type="warning" size="small">
                  影响 {{ affectedRows }} 行
                </el-tag>
                <el-dropdown @command="handleExport" v-if="queryResult.length > 0">
                  <el-button type="text" :icon="Download">
                    导出
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="csv">导出为 CSV</el-dropdown-item>
                      <el-dropdown-item command="excel">导出为 Excel</el-dropdown-item>
                      <el-dropdown-item command="json">导出为 JSON</el-dropdown-item>
                      <el-dropdown-item command="sql">导出为 INSERT SQL</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                <el-button type="text" :icon="FullScreen" @click="handleFullScreen" title="全屏查看">全屏</el-button>
              </div>
            </div>
          </template>
          
          <!-- 表格结果 -->
          <el-table
            v-if="queryResult.length > 0"
            :data="paginatedData"
            border
            stripe
            size="small"
            max-height="350"
            :row-class-name="getRowClassName"
          >
            <el-table-column type="index" width="60" label="#" />
            <el-table-column 
              v-for="col in resultColumns" 
              :key="col" 
              :prop="col" 
              :label="col"
              :formatter="formatCell"
              show-overflow-tooltip
              min-width="120"
            >
              <template #header>
                <div class="column-header">
                  <span>{{ col }}</span>
                  <el-tag v-if="isMaskedColumn(col)" size="small" type="warning" class="mask-tag">已脱敏</el-tag>
                </div>
              </template>
              <template #default="{ row, $index }">
                <span :class="{ 'masked-value': isMaskedValue(row, col) }">
                  {{ getDisplayValue(row, col) }}
                </span>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div v-if="queryResult.length > 0" class="result-pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="queryResult.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
          
          <!-- 空状态 -->
          <el-empty v-else-if="!executing" description="请输入SQL并点击执行">
            <template #image>
              <el-icon :size="60" color="#909399"><Cpu /></el-icon>
            </template>
          </el-empty>
          
          <!-- 加载中 -->
          <div v-if="executing" class="loading-state">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
            <p>查询执行中...</p>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧：历史记录 + 表结构 + 安全配置 -->
      <el-col :span="8" style="height: 100%">
        <el-tabs v-model="activeTab" class="right-tabs">
          <!-- 历史记录 -->
          <el-tab-pane label="历史记录" name="history">
            <div class="history-list">
              <div
                v-for="item in historyList"
                :key="item.id"
                class="history-item"
                @click="handleLoadHistory(item)"
              >
                <div class="history-sql">{{ item.sql }}</div>
                <div class="history-meta">
                  <el-tag size="small" :type="item.success ? 'success' : 'danger'">
                    {{ item.success ? '成功' : '失败' }}
                  </el-tag>
                  <span class="history-time">{{ item.time }}</span>
                </div>
              </div>
              <el-empty v-if="historyList.length === 0" description="暂无历史记录" :image-size="60" />
            </div>
          </el-tab-pane>
          
          <!-- 表结构 -->
          <el-tab-pane label="表结构" name="schema">
            <el-input v-model="schemaSearch" placeholder="搜索表..." prefix-icon="Search" clearable style="margin-bottom: 10px" />
            <el-tree
              :data="filteredSchemaTree"
              :props="{ label: 'name', children: 'columns' }"
              default-expand-all
              @node-click="handleSchemaClick"
              node-key="id"
            >
              <template #default="{ data }">
                <span class="schema-node">
                  <el-icon v-if="data.type === 'table'"><Grid /></el-icon>
                  <el-icon v-else><Document /></el-icon>
                  <span>{{ data.name }}</span>
                  <el-tag v-if="data.type === 'table'" size="small" type="info">{{ data.rowCount }}</el-tag>
                </span>
              </template>
            </el-tree>
          </el-tab-pane>
          
          <!-- 脱敏规则 -->
          <el-tab-pane label="脱敏规则" name="mask">
            <div class="mask-rules">
              <div v-for="rule in maskRules" :key="rule.id" class="mask-rule-item">
                <div class="rule-header">
                  <span class="rule-name">{{ rule.ruleName }}</span>
                  <el-switch v-model="rule.enabled" size="small" />
                </div>
                <div class="rule-info">
                  <el-tag size="small">{{ rule.dataType }}</el-tag>
                  <el-tag size="small" type="info">{{ rule.maskAlgorithm }}</el-tag>
                </div>
                <div class="rule-preview">
                  <span class="label">示例: </span>
                  <span class="original">13812345678</span>
                  <el-icon><ArrowRight /></el-icon>
                  <span class="masked">{{ applyMaskPreview(rule) }}</span>
                </div>
              </div>
              <el-empty v-if="maskRules.length === 0" description="暂无脱敏规则" :image-size="60" />
            </div>
          </el-tab-pane>
          
          <!-- 水印配置 -->
          <el-tab-pane label="水印配置" name="watermark">
            <el-form label-width="80px" class="watermark-form">
              <el-form-item label="水印类型">
                <el-radio-group v-model="watermarkConfig.type">
                  <el-radio label="invisible">不可见水印</el-radio>
                  <el-radio label="visible">可见水印</el-radio>
                  <el-radio label="none">不添加</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item v-if="watermarkConfig.type === 'visible'" label="水印内容">
                <el-input v-model="watermarkConfig.content" placeholder="请输入水印文字" />
              </el-form-item>
              <el-form-item v-if="watermarkConfig.type === 'visible'" label="透明度">
                <el-slider v-model="watermarkConfig.opacity" :min="0.1" :max="1" :step="0.1" show-stops />
              </el-form-item>
              <el-form-item label="包含信息">
                <el-checkbox-group v-model="watermarkConfig.includes">
                  <el-checkbox label="userId">用户ID</el-checkbox>
                  <el-checkbox label="username">用户名</el-checkbox>
                  <el-checkbox label="timestamp">时间戳</el-checkbox>
                  <el-checkbox label="sessionId">会话ID</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveWatermarkConfig">保存配置</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
    
    <!-- 保存模板对话框 -->
    <el-dialog v-model="saveTemplateDialog" title="保存SQL模板" width="400px">
      <el-form :model="templateForm" label-width="80px">
        <el-form-item label="模板名称">
          <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input v-model="templateForm.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveTemplateDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSaveTemplate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Cpu, Brush, CopyDocument, DocumentAdd, FolderOpened, Delete, Download, FullScreen, 
  Loading, Grid, Document, Search, ArrowRight, Key, Lock
} from '@element-plus/icons-vue'

// 状态变量
const selectedInstance = ref(null)
const sqlContent = ref('')
const executing = ref(false)
const executionTime = ref(0)
const queryResult = ref([])
const resultColumns = ref([])
const affectedRows = ref(0)
const showMasked = ref(true)
const enableWatermark = ref(true)
const currentPage = ref(1)
const pageSize = ref(20)
const activeTab = ref('history')
const schemaSearch = ref('')
const saveTemplateDialog = ref(false)

// 模板表单
const templateForm = ref({ name: '', description: '' })

// 数据库实例
const instances = ref([
  { id: 1, instanceName: '生产库-MySQL', host: '192.168.1.100', port: 3306, status: 1 },
  { id: 2, instanceName: '测试库-PostgreSQL', host: '192.168.1.101', port: 5432, status: 1 },
  { id: 3, instanceName: '开发库-Oracle', host: '192.168.1.102', port: 1521, status: 0 }
])

// SQL模板
const templates = ref([
  { id: 1, name: '查询用户', sql: 'SELECT * FROM users WHERE status = 1' },
  { id: 2, name: '统计订单', sql: 'SELECT COUNT(*) FROM orders WHERE created_at > NOW() - INTERVAL 7 DAY' }
])

// 历史记录
const historyList = ref([
  { id: 1, sql: 'SELECT * FROM users LIMIT 10', success: true, time: '10:30:25', rows: 10 },
  { id: 2, sql: 'SELECT COUNT(*) FROM orders', success: true, time: '10:28:15', rows: 1 },
  { id: 3, sql: 'UPDATE products SET price = 100', success: false, time: '10:25:40', rows: 0 },
  { id: 4, sql: 'DELETE FROM temp_data', success: true, time: '10:20:30', rows: 50 }
])

// 表结构
const schemaTree = ref([
  { 
    id: 'db1', name: 'app_db', type: 'database', 
    columns: [
      { id: 't1', name: 'users', type: 'table', rowCount: 1000, columns: [
        { name: 'id', type: 'bigint' },
        { name: 'username', type: 'varchar(50)' },
        { name: 'password', type: 'varchar(255)' },
        { name: 'id_card', type: 'varchar(18)' },
        { name: 'phone', type: 'varchar(11)' },
        { name: 'bank_card', type: 'varchar(20)' },
        { name: 'email', type: 'varchar(100)' },
        { name: 'created_at', type: 'datetime' }
      ]},
      { id: 't2', name: 'orders', type: 'table', rowCount: 5000, columns: [
        { name: 'id', type: 'bigint' },
        { name: 'user_id', type: 'bigint' },
        { name: 'total', type: 'decimal(10,2)' },
        { name: 'status', type: 'varchar(20)' }
      ]}
    ]
  }
])

// 脱敏规则
const maskRules = ref([
  { id: 1, ruleName: '手机号脱敏', dataType: 'phone', maskAlgorithm: 'mask', config: { enabled: true, showLast4: true } },
  { id: 2, ruleName: '身份证号脱敏', dataType: 'idcard', maskAlgorithm: 'mask', enabled: true, config: { showLast4: true } },
  { id: 3, ruleName: '银行卡号脱敏', dataType: 'bank', maskAlgorithm: 'mask', enabled: true, config: { showFirst6: true, showLast4: true } },
  { id: 4, ruleName: '邮箱脱敏', dataType: 'email', maskAlgorithm: 'replace', enabled: true, config: { replaceChar: '*' } }
])

// 水印配置
const watermarkConfig = ref({
  type: 'invisible',
  content: '',
  opacity: 0.3,
  includes: ['userId', 'timestamp']
})

// 脱敏列定义
const maskedColumns = ['password', 'id_card', 'phone', 'bank_card', 'email']

// 计算属性
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return queryResult.value.slice(start, end)
})

const filteredSchemaTree = computed(() => {
  if (!schemaSearch.value) return schemaTree.value
  const search = schemaSearch.value.toLowerCase()
  return schemaTree.value.map(db => ({
    ...db,
    columns: db.columns.filter(table => 
      table.name.toLowerCase().includes(search) ||
      table.columns?.some(col => col.name.toLowerCase().includes(search))
    )
  })).filter(db => db.columns?.length > 0)
})

// 方法
const handleInstanceChange = (val) => {
  const instance = instances.value.find(i => i.id === val)
  ElMessage.success(`已切换到: ${instance?.instanceName}`)
}

const handleExecute = async () => {
  if (!sqlContent.value.trim()) {
    ElMessage.warning('请输入SQL语句')
    return
  }
  if (!selectedInstance.value) {
    ElMessage.warning('请先选择数据库实例')
    return
  }
  
  executing.value = true
  queryResult.value = []
  
  // 模拟执行
  await new Promise(resolve => setTimeout(resolve, 800))
  
  executionTime.value = Math.floor(Math.random() * 100) + 20
  
  // 判断SQL类型
  const sql = sqlContent.value.trim().toUpperCase()
  if (sql.startsWith('SELECT')) {
    resultColumns.value = ['id', 'username', 'password', 'id_card', 'phone', 'bank_card', 'email', 'created_at']
    queryResult.value = [
      { id: 1, username: '张三', password: '******', id_card: '110101199001011234', phone: '13812345678', bank_card: '6222021234567890', email: 'zhangsan@example.com', created_at: '2024-01-01 10:00:00' },
      { id: 2, username: '李四', password: '******', id_card: '110101199002022345', phone: '13923456789', bank_card: '6222021234567891', email: 'lisi@example.com', created_at: '2024-01-02 10:00:00' },
      { id: 3, username: '王五', password: '******', id_card: '110101199003033456', phone: '13734567890', bank_card: '6222021234567892', email: 'wangwu@example.com', created_at: '2024-01-03 10:00:00' }
    ]
    affectedRows.value = queryResult.value.length
  } else {
    affectedRows.value = Math.floor(Math.random() * 100)
    resultColumns.value = []
    queryResult.value = []
  }
  
  // 添加到历史记录
  historyList.value.unshift({
    id: Date.now(),
    sql: sqlContent.value.substring(0, 50),
    success: true,
    time: new Date().toLocaleTimeString(),
    rows: affectedRows.value
  })
  
  executing.value = false
  ElMessage.success(`查询成功，返回 ${affectedRows.value} 行`)
}

const handleFormat = () => {
  if (!sqlContent.value.trim()) {
    ElMessage.warning('没有可格式化的内容')
    return
  }
  // 简单格式化
  let sql = sqlContent.value.trim()
  sql = sql.replace(/\s+/g, ' ')
  sql = sql.replace(/,\s*/g, ',\n  ')
  sql = sql.replace(/\bSELECT\b/gi, 'SELECT\n  ')
  sql = sql.replace(/\bFROM\b/gi, '\nFROM')
  sql = sql.replace(/\bWHERE\b/gi, '\nWHERE')
  sql = sql.replace(/\bAND\b/gi, '\n  AND')
  sql = sql.replace(/\bORDER BY\b/gi, '\nORDER BY')
  sql = sql.replace(/\bLIMIT\b/gi, '\nLIMIT')
  sqlContent.value = sql
  ElMessage.success('格式化完成')
}

const handleCopy = async () => {
  if (!sqlContent.value.trim()) {
    ElMessage.warning('没有可复制的内容')
    return
  }
  await navigator.clipboard.writeText(sqlContent.value)
  ElMessage.success('已复制到剪贴板')
}

const handleSaveTemplate = (command) => {
  if (command === 'save' && !sqlContent.value.trim()) {
    ElMessage.warning('没有可保存的内容')
    return
  }
  if (command === 'saveas') {
    templateForm.value = { name: '', description: '' }
    saveTemplateDialog.value = true
  }
}

const confirmSaveTemplate = () => {
  if (!templateForm.value.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  templates.value.push({
    id: Date.now(),
    name: templateForm.value.name,
    sql: sqlContent.value
  })
  saveTemplateDialog.value = false
  ElMessage.success('模板保存成功')
}

const handleLoadTemplate = (command) => {
  const tpl = templates.value.find(t => t.id === command)
  if (tpl) {
    sqlContent.value = tpl.sql
    ElMessage.success(`已加载模板: ${tpl.name}`)
  }
}

const handleClear = () => {
  sqlContent.value = ''
  queryResult.value = []
  resultColumns.value = []
  affectedRows.value = 0
}

const handleExport = (command) => {
  if (queryResult.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  let content = ''
  const filename = `query_result_${Date.now()}`
  
  switch (command) {
    case 'csv':
      content = resultColumns.value.join(',') + '\n'
      queryResult.value.forEach(row => {
        content += resultColumns.value.map(col => row[col]).join(',') + '\n'
      })
      downloadFile(content, filename + '.csv', 'text/csv')
      break
    case 'json':
      content = JSON.stringify(queryResult.value, null, 2)
      downloadFile(content, filename + '.json', 'application/json')
      break
    case 'sql':
      content = queryResult.value.map(row => {
        const values = resultColumns.value.map(col => {
          const val = row[col]
          return typeof val === 'string' ? `'${val}'` : val
        }).join(', ')
        return `INSERT INTO table_name (${resultColumns.value.join(', ')}) VALUES (${values});`
      }).join('\n')
      downloadFile(content, filename + '.sql', 'text/plain')
      break
  }
  ElMessage.success('导出成功')
}

const downloadFile = (content, filename, type) => {
  const blob = new Blob([content], { type })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

const handleFullScreen = () => {
  ElMessage.info('全屏功能')
}

const handleLoadHistory = (item) => {
  sqlContent.value = item.sql
  ElMessage.success('已加载历史SQL')
}

const handleSchemaClick = (data) => {
  if (data.type === 'table') {
    sqlContent.value = `SELECT * FROM ${data.name} LIMIT 100`
    ElMessage.success(`已生成查询: ${data.name}`)
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handlePageChange = (val) => {
  currentPage.value = val
}

// 脱敏相关方法
const isMaskedColumn = (col) => {
  return maskedColumns.includes(col) && showMasked.value
}

const isMaskedValue = (row, col) => {
  return maskedColumns.includes(col) && showMasked.value && row[col + '_masked']
}

const getDisplayValue = (row, col) => {
  if (showMasked.value && maskedColumns.includes(col)) {
    return row[col + '_display'] || row[col]
  }
  return row[col]
}

const formatCell = (row, column) => {
  const col = column.property
  if (showMasked.value && maskedColumns.includes(col)) {
    return row[col + '_display'] || row[col]
  }
  return row[col]
}

const applyMaskPreview = (rule) => {
  switch (rule.dataType) {
    case 'phone': return '138****5678'
    case 'idcard': return '110101****0112'
    case 'bank': return '622202****7890'
    case 'email': return 'z***n@example.com'
    default: return '******'
  }
}

const getRowClassName = ({ row, rowIndex }) => {
  return ''
}

const saveWatermarkConfig = () => {
  ElMessage.success('水印配置已保存')
}
</script>

<style scoped>
.query-container {
  height: calc(100vh - 140px);
  padding: 0;
}

.query-container .el-row {
  height: 100%;
}

.query-container .el-col {
  height: 100%;
  overflow: hidden;
}

.editor-card {
  height: 45%;
  display: flex;
  flex-direction: column;
}

.editor-card .el-card__body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-title {
  font-weight: 600;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.sql-editor {
  flex: 1;
  min-height: 120px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.editor-textarea {
  width: 100%;
  height: 100%;
  padding: 12px;
  border: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
  background: #f8f9fa;
}

.editor-textarea:focus {
  background: #fff;
}

.editor-toolbar {
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  border-top: 1px solid #eee;
}

.result-card {
  height: calc(55% - 10px);
  display: flex;
  flex-direction: column;
}

.result-card .el-card__body {
  flex: 1;
  overflow: auto;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.result-pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.column-header {
  display: flex;
  align-items: center;
  gap: 5px;
}

.masked-value {
  color: #E6A23C;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}

.right-tabs {
  height: 100%;
}

.right-tabs .el-tab-pane {
  height: calc(100vh - 220px);
  overflow-y: auto;
}

.history-list {
  max-height: 100%;
  overflow-y: auto;
}

.history-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.history-item:hover {
  background: #f5f7fa;
}

.history-sql {
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
  font-family: monospace;
}

.history-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-time {
  font-size: 12px;
  color: #909399;
}

.schema-node {
  display: flex;
  align-items: center;
  gap: 5px;
}

.mask-rules {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mask-rule-item {
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 6px;
  background: #fafafa;
}

.rule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.rule-name {
  font-weight: 500;
  color: #303133;
}

.rule-info {
  display: flex;
  gap: 5px;
  margin-bottom: 8px;
}

.rule-preview {
  font-size: 12px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 5px;
}

.rule-preview .original {
  color: #909399;
  text-decoration: line-through;
}

.rule-preview .masked {
  color: #E6A23C;
  font-weight: 500;
}

.watermark-form {
  padding: 10px;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}
</style>
