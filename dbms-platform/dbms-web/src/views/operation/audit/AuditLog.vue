<template>
  <div class="page-container">
    <div class="page-header">
      <h2>审计日志查询</h2>
      <el-button type="primary" :icon="Download" @click="handleExport">导出</el-button>
    </div>
    
    <el-form :inline="true" class="search-form">
      <el-form-item label="操作人">
        <el-input v-model="searchForm.user" placeholder="请输入操作人" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="searchForm.action" placeholder="请选择" clearable style="width: 150px">
          <el-option label="登录" value="login" />
          <el-option label="查询" value="query" />
          <el-option label="新增" value="insert" />
          <el-option label="更新" value="update" />
          <el-option label="删除" value="delete" />
          <el-option label="导出" value="export" />
          <el-option label="配置" value="config" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作对象">
        <el-input v-model="searchForm.target" placeholder="表名/文件" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" border stripe v-loading="loading" @row-click="handleRowClick">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="user" label="操作人" width="120" />
      <el-table-column prop="action" label="操作类型" width="100">
        <template #default="{ row }">
          <el-tag :type="actionMap[row.action]?.type || 'info'" size="small">
            {{ actionMap[row.action]?.text || row.action }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="method" label="请求方法" width="100">
        <template #default="{ row }">
          <el-tag :type="methodMap[row.method]?.type || 'info'" size="small">
            {{ row.method }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="target" label="操作对象" min-width="150" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="userAgent" label="用户代理" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 200 ? 'success' : 'danger'" size="small">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="耗时" width="80">
        <template #default="{ row }">
          {{ row.duration }}ms
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="操作时间" width="180" />
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[20, 50, 100, 200]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
    />
    
    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailVisible" title="审计日志详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="操作人">{{ currentRow.user }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ currentRow.action }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentRow.method }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentRow.ip }}</el-descriptions-item>
        <el-descriptions-item label="状态码">{{ currentRow.status }}</el-descriptions-item>
        <el-descriptions-item label="操作对象" :span="2">{{ currentRow.target }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="json-content">{{ currentRow.params }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="用户代理" :span="2">{{ currentRow.userAgent }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const detailVisible = ref(false)
const currentRow = ref(null)

const searchForm = reactive({ user: '', action: '', target: '', dateRange: [] })
const pagination = reactive({ page: 1, size: 20, total: 0 })

const actionMap = {
  login: { type: 'primary', text: '登录' },
  query: { type: 'success', text: '查询' },
  insert: { type: 'warning', text: '新增' },
  update: { type: 'warning', text: '更新' },
  delete: { type: 'danger', text: '删除' },
  export: { type: 'info', text: '导出' },
  config: { type: 'info', text: '配置' }
}

const methodMap = {
  GET: { type: 'success' },
  POST: { type: 'primary' },
  PUT: { type: 'warning' },
  DELETE: { type: 'danger' }
}

const tableData = ref([
  { id: 1, user: 'admin', action: 'login', method: 'POST', target: '/api/login', ip: '192.168.1.100', userAgent: 'Mozilla/5.0', status: 200, duration: 120, createTime: '2024-01-15 10:30:25', params: '{"username":"admin"}' },
  { id: 2, user: 'admin', action: 'query', method: 'GET', target: 'users', ip: '192.168.1.100', userAgent: 'Mozilla/5.0', status: 200, duration: 45, createTime: '2024-01-15 10:30:30', params: '{"where":{"status":1}}' },
  { id: 3, user: 'admin', action: 'update', method: 'PUT', target: 'users/1', ip: '192.168.1.100', userAgent: 'Mozilla/5.0', status: 200, duration: 89, createTime: '2024-01-15 10:31:00', params: '{"nickname":"管理员"}' },
  { id: 4, user: 'developer', action: 'query', method: 'GET', target: 'orders', ip: '192.168.1.101', userAgent: 'Mozilla/5.0', status: 200, duration: 230, createTime: '2024-01-15 10:32:15', params: '{"limit":100}' },
  { id: 5, user: 'admin', action: 'delete', method: 'DELETE', target: 'temp/123', ip: '192.168.1.100', userAgent: 'Mozilla/5.0', status: 200, duration: 56, createTime: '2024-01-15 10:33:00', params: '{}' },
  { id: 6, user: 'operator', action: 'config', method: 'POST', target: '/api/backup', ip: '192.168.1.102', userAgent: 'Mozilla/5.0', status: 200, duration: 320, createTime: '2024-01-15 10:35:20', params: '{"type":"full","retention":30}' },
  { id: 7, user: 'developer', action: 'export', method: 'GET', target: '/api/export/users', ip: '192.168.1.101', userAgent: 'Mozilla/5.0', status: 200, duration: 1250, createTime: '2024-01-15 10:40:00', params: '{"format":"xlsx"}' },
  { id: 8, user: 'test', action: 'login', method: 'POST', target: '/api/login', ip: '192.168.1.103', userAgent: 'Mozilla/5.0', status: 401, duration: 30, createTime: '2024-01-15 10:45:00', params: '{"username":"test"}' }
])

const loadData = () => { loading.value = true; setTimeout(() => { pagination.total = 100; loading.value = false }, 500) }
const handleReset = () => { searchForm.user = ''; searchForm.action = ''; searchForm.target = ''; searchForm.dateRange = []; loadData() }
const handleExport = () => { ElMessage.success('导出成功') }
const handleRowClick = (row) => { currentRow.value = row; detailVisible.value = true }

loadData()
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
.search-form { margin-bottom: 20px; }

.json-content { background: #f5f7fa; padding: 10px; border-radius: 4px; font-size: 12px; max-height: 200px; overflow: auto; margin: 0; white-space: pre-wrap; word-break: break-all; }
</style>
