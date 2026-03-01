<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜单权限管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd(null)">新增菜单</el-button>
    </div>
    
    <el-table
      :data="tableData"
      row-key="id"
      border
      stripe
      :tree-props="{ children: 'children' }"
    >
      <el-table-column prop="name" label="菜单名称" width="200" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" />
      <el-table-column prop="component" label="组件路径" />
      <el-table-column prop="permission" label="权限标识" />
      <el-table-column prop="type" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="typeMap[row.type]">{{ typeName[row.type] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleAdd(row)">新增子菜单</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 菜单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeSelectData"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择上级菜单"
            clearable
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.type !== 2">
          <el-input v-model="form.icon" placeholder="请输入图标名称">
            <template #prefix>
              <el-icon><component :is="form.icon" /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="form.type !== 2">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.type === 1">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission" v-if="form.type === 2">
          <el-input v-model="form.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const parentMenu = ref(null)

const typeMap = { 0: '', 1: 'success', 2: 'warning' }
const typeName = { 0: '目录', 1: '菜单', 2: '按钮' }

const tableData = ref([
  {
    id: 1, name: '系统管理', icon: 'Setting', path: '/system', status: 1, sort: 1, type: 0,
    children: [
      { id: 11, name: '用户管理', icon: 'User', path: '/system/user', component: 'system/user/index', status: 1, sort: 1, type: 1, permission: 'system:user' },
      { id: 12, name: '角色管理', icon: 'UserFilled', path: '/system/role', component: 'system/role/index', status: 1, sort: 2, type: 1, permission: 'system:role' },
      { id: 13, name: '菜单管理', icon: 'Menu', path: '/system/menu', component: 'system/menu/index', status: 1, sort: 3, type: 1, permission: 'system:menu' }
    ]
  },
  {
    id: 2, name: '数据库管理', icon: 'Grid', path: '/database', status: 1, sort: 2, type: 0,
    children: [
      { id: 21, name: '实例管理', icon: 'Connection', path: '/database/instance', component: 'database/instance/index', status: 1, sort: 1, type: 1, permission: 'database:instance' },
      { id: 22, name: '目录管理', icon: 'FolderOpened', path: '/database/catalog', component: 'database/catalog/index', status: 1, sort: 2, type: 1, permission: 'database:catalog' }
    ]
  },
  {
    id: 3, name: '查询管理', icon: 'Edit', path: '/query', status: 1, sort: 3, type: 0,
    children: [
      { id: 31, name: 'SQL查询', icon: 'EditPen', path: '/query/editor', component: 'query/editor/index', status: 1, sort: 1, type: 1, permission: 'query:editor' },
      { id: 32, name: '查询历史', icon: 'Clock', path: '/query/history', component: 'query/history/index', status: 1, sort: 2, type: 1, permission: 'query:history' }
    ]
  },
  {
    id: 4, name: '数据安全', icon: 'Lock', path: '/security', status: 1, sort: 4, type: 0,
    children: [
      { id: 41, name: '脱敏规则', icon: 'Hide', path: '/security/mask', component: 'security/mask/index', status: 1, sort: 1, type: 1, permission: 'security:mask' },
      { id: 42, name: '水印配置', icon: 'PictureFilled', path: '/security/watermark', component: 'security/watermark/index', status: 1, sort: 2, type: 1, permission: 'security:watermark' },
      { id: 43, name: '访问策略', icon: 'Key', path: '/security/policy', component: 'security/policy/index', status: 1, sort: 3, type: 1, permission: 'security:policy' }
    ]
  },
  {
    id: 5, name: '运维管理', icon: 'Tools', path: '/operation', status: 1, sort: 5, type: 0,
    children: [
      { id: 51, name: '备份策略', icon: 'FolderAdd', path: '/operation/backup', component: 'operation/backup/index', status: 1, sort: 1, type: 1, permission: 'operation:backup' },
      { id: 52, name: '备份任务', icon: 'List', path: '/operation/task', component: 'operation/task/index', status: 1, sort: 2, type: 1, permission: 'operation:task' },
      { id: 53, name: '审计日志', icon: 'Document', path: '/operation/audit', component: 'operation/audit/index', status: 1, sort: 3, type: 1, permission: 'operation:audit' }
    ]
  }
])

const treeSelectData = computed(() => {
  const addRoot = [{ id: 0, name: '顶级菜单', children: tableData.value }]
  return addRoot
})

const form = reactive({
  id: null,
  parentId: 0,
  type: 1,
  name: '',
  icon: '',
  path: '',
  component: '',
  permission: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const dialogTitle = computed(() => form.id ? '编辑菜单' : '新增菜单')

const handleAdd = (row) => {
  parentMenu.value = row
  Object.assign(form, { id: null, parentId: row?.id || 0, type: 1, name: '', icon: '', path: '', component: '', permission: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', { type: 'warning' })
  ElMessage.success('删除成功')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success(form.id ? '编辑成功' : '新增成功')
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
