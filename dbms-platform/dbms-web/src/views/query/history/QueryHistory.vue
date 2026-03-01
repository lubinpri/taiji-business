<template>
  <div class="page-container">
    <div class="page-header">
      <h2>查询历史记录</h2>
      <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
    </div>
    
    <el-form :inline="true" class="search-form">
      <el-form-item label="查询内容">
        <el-input v-model="searchForm.sql" placeholder="请输入查询内容" clearable style="width: 300px" />
      </el-form-item>
      <el-form-item label="执行结果">
        <el-select v-model="searchForm.result" placeholder="请选择" clearable>
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="执行时间">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table
      :data="tableData"
      border
      stripe
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="sql" label="SQL语句" min-width="300">
        <template #default="{ row }">
          <div class="sql-content">{{ row.sql }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="database" label="数据库" width="120" />
      <el-table-column prop="result" label="执行结果" width="100">
        <template #default="{ row }">
          <el-tag :type="row.result ? 'success' : 'danger'" size="small">
            {{ row.result ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rows" label="影响行数" width="100" />
      <el-table-column prop="duration" label="执行时长" width="100">
        <template #default="{ row }">
          {{ row.duration }}ms
        </template>
      </el-table-column>
      <el-table-column prop="user" label="执行人" width="100" />
      <el-table-column prop="executeTime" label="执行时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button type="primary" link @click="handleReRun(row)">重新执行</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
    />
    
    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查询详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="SQL语句" :span="2">
          <div class="detail-sql">{{ currentRow?.sql }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="数据库">{{ currentRow?.database }}</el-descriptions-item>
        <el-descriptions-item label="执行结果">
          <el-tag :type="currentRow?.result ? 'success' : 'danger'">
            {{ currentRow?.result ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="影响行数">{{ currentRow?.rows }}</el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ currentRow?.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="执行人">{{ currentRow?.user }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ currentRow?.executeTime }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="!currentRow?.result">
          <div class="error-message">{{ currentRow?.error }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Search, Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const viewDialogVisible = ref(false)
const currentRow = ref(null)
const selectedRows = ref([])

const searchForm = reactive({ sql: '', result: '', dateRange: [] })
const pagination = reactive({ page: 1, size: 10, total: 0 })

const tableData = ref([
  { id: 1, sql: 'SELECT * FROM users WHERE status = 1 LIMIT 10', database: 'production_db', result: 1, rows: 10, duration: 25, user: 'admin', executeTime: '2024-01-15 10:30:25' },
  { id: 2, sql: 'SELECT COUNT(*) FROM orders WHERE created_at > "2024-01-01"', database: 'production_db', result: 1, rows: 1, duration: 120, user: 'admin', executeTime: '2024-01-15 10:28:15' },
  { id: 3, sql: 'UPDATE products SET price = price * 0.9 WHERE category = "electronics"', database: 'production_db', result: 0, rows: 0, duration: 5000, user: 'admin', executeTime: '2024-01-15 10:25:40', error: 'Table \'products\' doesn\'t exist' },
  { id: 4, sql: 'DELETE FROM temp_data WHERE created_at < "2023-12-01"', database: 'production_db', result: 1, rows: 156, duration: 89, user: 'developer', executeTime: '2024-01-15 10:20:30' },
  { id: 5, sql: 'INSERT INTO logs (level, message) VALUES ("info", "test")', database: 'production_db', result: 1, rows: 1, duration: 12, user: 'admin', executeTime: '2024-01-15 10:15:20' }
])

const loadData = () => {
  loading.value = true
  setTimeout(() => { pagination.total = 5; loading.value = false }, 500)
}

const handleReset = () => {
  searchForm.sql = ''
  searchForm.result = ''
  searchForm.dateRange = []
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleReRun = (row) => {
  router.push({ path: '/query/editor', query: { sql: encodeURIComponent(row.sql) } })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
  ElMessage.success('删除成功')
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '提示', { type: 'warning' })
  ElMessage.success('批量删除成功')
}

loadData()
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
.search-form { margin-bottom: 20px; }

.sql-content { max-width: 400px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-family: monospace; }
.detail-sql { padding: 10px; background: #f5f7fa; border-radius: 4px; font-family: monospace; white-space: pre-wrap; word-break: break-all; }
.error-message { color: #f56c6c; padding: 10px; background: #fef0f0; border-radius: 4px; }
</style>
