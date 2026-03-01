<template>
  <div class="page-container">
    <div class="page-header">
      <h2>备份任务列表</h2>
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新</el-button>
    </div>
    
    <el-form :inline="true" class="search-form">
      <el-form-item label="任务名称">
        <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
          <el-option label="运行中" :value="2" />
          <el-option label="等待中" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="任务名称" width="180" />
      <el-table-column prop="strategy" label="所属策略" width="150" />
      <el-table-column prop="database" label="数据库" width="120" />
      <el-table-column prop="type" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.type === 'full' ? 'primary' : 'success'" size="small">
            {{ row.type === 'full' ? '全量' : '增量' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status].type" size="small">
            {{ statusMap[row.status].text }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="备份大小" width="120" />
      <el-table-column prop="duration" label="耗时" width="100">
        <template #default="{ row }">
          {{ row.duration }}秒
        </template>
      </el-table-column>
      <el-table-column prop="filePath" label="备份文件" min-width="200" show-overflow-tooltip />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDownload(row)">下载</el-button>
          <el-button type="success" link @click="handleRestore(row)" v-if="row.status === 1">恢复</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      style="margin-top: 20px; justify-content: flex-end"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'

const loading = ref(false)
const searchForm = reactive({ name: '', status: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })

const statusMap = {
  1: { type: 'success', text: '成功' },
  0: { type: 'danger', text: '失败' },
  2: { type: 'warning', text: '运行中' },
  3: { type: 'info', text: '等待中' }
}

const tableData = ref([
  { id: 1, name: '每日全量备份-20240115', strategy: '每日全量备份', database: 'production_db', type: 'full', status: 1, size: '2.5GB', duration: 320, filePath: '/backup/production_db/full_20240115_020000.sql.gz', startTime: '2024-01-15 02:00:00', endTime: '2024-01-15 02:05:20' },
  { id: 2, name: '每小时增量-2024011510', strategy: '每小时增量', database: 'production_db', type: 'incremental', status: 1, size: '150MB', duration: 45, filePath: '/backup/production_db/incr_20240115_100000.sql.gz', startTime: '2024-01-15 10:00:00', endTime: '2024-01-15 10:00:45' },
  { id: 3, name: '每小时增量-2024011511', strategy: '每小时增量', database: 'production_db', type: 'incremental', status: 2, size: '-', duration: 0, filePath: '', startTime: '2024-01-15 11:00:00', endTime: '-' },
  { id: 4, name: '测试库日备-20240115', strategy: '测试库日备', database: 'test_db', type: 'full', status: 0, size: '-', duration: 0, filePath: '', startTime: '2024-01-15 03:00:00', endTime: '2024-01-15 03:00:15', error: 'Connection refused' }
])

const loadData = () => { loading.value = true; setTimeout(() => { pagination.total = 4; loading.value = false }, 500) }

const handleDownload = (row) => { ElMessage.success('开始下载备份文件') }
const handleRestore = (row) => { ElMessage.warning('开始恢复数据') }
const handleDelete = async (row) => { await ElMessageBox.confirm('确定要删除该备份记录吗？', '提示', { type: 'warning' }); ElMessage.success('删除成功') }

loadData()
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
.search-form { margin-bottom: 20px; }
</style>
