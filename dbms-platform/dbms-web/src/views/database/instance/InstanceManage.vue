<template>
  <div class="instance-container">
    <!-- Search Bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="实例名称">
          <el-input v-model="searchForm.instanceName" placeholder="请输入实例名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.instanceType" placeholder="请选择类型" clearable>
            <el-option label="MySQL" value="mysql" />
            <el-option label="PostgreSQL" value="postgresql" />
            <el-option label="Oracle" value="oracle" />
            <el-option label="SQL Server" value="sqlserver" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增实例
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="instanceName" label="实例名称" min-width="150" />
      <el-table-column prop="instanceType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ row.instanceType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="host" label="主机" min-width="120" />
      <el-table-column prop="port" label="端口" width="80" />
      <el-table-column prop="serviceName" label="数据库" width="100" />
      <el-table-column prop="description" label="描述" min-width="150" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleTest(row)">测试连接</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="实例名称" prop="instanceName">
          <el-input v-model="form.instanceName" placeholder="请输入实例名称" />
        </el-form-item>
        <el-form-item label="数据库类型" prop="instanceType">
          <el-select v-model="form.instanceType" placeholder="请选择类型">
            <el-option label="MySQL" value="mysql" />
            <el-option label="PostgreSQL" value="postgresql" />
            <el-option label="Oracle" value="oracle" />
            <el-option label="SQL Server" value="sqlserver" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机" prop="host">
          <el-input v-model="form.host" placeholder="请输入主机地址" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="数据库名" prop="serviceName">
          <el-input v-model="form.serviceName" placeholder="请输入数据库名" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const API_URL = 'http://43.153.189.176:8082'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增实例')
const formRef = ref()

const searchForm = ref({
  instanceName: '',
  instanceType: ''
})

const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const form = ref({
  id: null,
  instanceName: '',
  instanceType: 'mysql',
  host: '',
  port: 3306,
  serviceName: '',
  username: '',
  password: '',
  description: '',
  status: 1
})

const rules = {
  instanceName: [{ required: true, message: '请输入实例名称', trigger: 'blur' }],
  instanceType: [{ required: true, message: '请选择数据库类型', trigger: 'change' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      ...searchForm.value
    }
    const res = await axios.get(`${API_URL}/api/resource/instances`, { params })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records || []
      pagination.value.total = res.data.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { instanceName: '', instanceType: '' }
  handleSearch()
}

const handleAdd = () => {
  form.value = {
    id: null,
    instanceName: '',
    instanceType: 'mysql',
    host: '',
    port: 3306,
    serviceName: '',
    username: '',
    password: '',
    description: '',
    status: 1
  }
  dialogTitle.value = '新增实例'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row, password: '' }
  dialogTitle.value = '编辑实例'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const url = form.value.id 
      ? `${API_URL}/api/resource/instances/${form.value.id}`
      : `${API_URL}/api/resource/instances`
    const method = form.value.id ? 'put' : 'post'
    
    const res = await axios[method](url, form.value)
    if (res.data.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该实例吗？', '提示', { type: 'warning' })
    const res = await axios.delete(`${API_URL}/api/resource/instances/${row.id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleTest = async (row) => {
  try {
    const res = await axios.post(`${API_URL}/api/resource/instances/${row.id}/test`)
    if (res.data.code === 200) {
      ElMessage.success('连接测试成功')
    } else {
      ElMessage.error(res.data.message || '连接测试失败')
    }
  } catch (e) {
    ElMessage.error('连接测试失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.instance-container {
  padding: 20px;
}
.search-bar {
  margin-bottom: 15px;
}
.toolbar {
  margin-bottom: 15px;
}
.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
