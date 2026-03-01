<template>
  <div class="page-container">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="角色名称" width="150" />
      <el-table-column prop="code" label="角色编码" width="150" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link :icon="Key" @click="handlePermission(row)">权限配置</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 权限配置对话框 -->
    <el-dialog v-model="permDialogVisible" title="权限配置" width="500px">
      <el-tree
        ref="permTreeRef"
        :data="permTreeData"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPerms"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Key } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const formRef = ref(null)
const permTreeRef = ref(null)

const isEdit = computed(() => !!form.id)

const tableData = ref([
  { id: 1, name: '超级管理员', code: 'admin', description: '拥有所有权限', status: 1, createTime: '2024-01-01 10:00:00' },
  { id: 2, name: '开发人员', code: 'developer', description: '开发人员角色', status: 1, createTime: '2024-01-05 10:00:00' },
  { id: 3, name: '运维人员', code: 'operator', description: '运维人员角色', status: 1, createTime: '2024-01-10 10:00:00' },
  { id: 4, name: '只读用户', code: 'readonly', description: '只读权限', status: 0, createTime: '2024-01-15 10:00:00' }
])

const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const permTreeData = ref([
  { id: 1, name: '系统管理', children: [
    { id: 11, name: '用户管理' }, { id: 12, name: '角色管理' }, { id: 13, name: '菜单管理' }
  ]},
  { id: 2, name: '数据库管理', children: [
    { id: 21, name: '实例管理' }, { id: 22, name: '目录管理' }
  ]},
  { id: 3, name: '查询管理', children: [
    { id: 31, name: 'SQL查询' }, { id: 32, name: '查询历史' }
  ]},
  { id: 4, name: '数据安全', children: [
    { id: 41, name: '脱敏规则' }, { id: 42, name: '水印配置' }, { id: 43, name: '访问策略' }
  ]},
  { id: 5, name: '运维管理', children: [
    { id: 51, name: '备份策略' }, { id: 52, name: '备份任务' }, { id: 53, name: '审计日志' }
  ]}
])

const checkedPerms = ref([11, 12, 21, 22, 31])

const currentRole = ref(null)

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', code: '', description: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该角色吗？', '提示', { type: 'warning' })
  ElMessage.success('删除成功')
}

const handlePermission = (row) => {
  currentRole.value = row
  permDialogVisible.value = true
}

const handlePermSubmit = () => {
  const checkedKeys = permTreeRef.value.getCheckedKeys()
  ElMessage.success(`已配置 ${checkedKeys.length} 个权限`)
  permDialogVisible.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>
