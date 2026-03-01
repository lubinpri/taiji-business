<template>
  <div class="page-container">
    <div class="page-header">
      <h2>脱敏规则管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增规则</el-button>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="规则名称" width="150" />
      <el-table-column prop="fieldType" label="字段类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ row.fieldType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pattern" label="匹配模式" />
      <el-table-column prop="maskType" label="脱敏方式" width="150">
        <template #default="{ row }">
          <el-tag type="success">{{ maskTypeMap[row.maskType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="example" label="示例" width="200">
        <template #default="{ row }">
          <div>{{ row.original }} → {{ row.masked }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="字段类型" prop="fieldType">
          <el-select v-model="form.fieldType" placeholder="请选择">
            <el-option label="手机号" value="phone" />
            <el-option label="身份证号" value="idCard" />
            <el-option label="银行卡号" value="bankCard" />
            <el-option label="邮箱" value="email" />
            <el-option label="姓名" value="name" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配模式" prop="pattern">
          <el-input v-model="form.pattern" placeholder="正则表达式，如 ^1[3-9]\d{9}$" />
        </el-form-item>
        <el-form-item label="脱敏方式" prop="maskType">
          <el-radio-group v-model="form.maskType">
            <el-radio value="mask">遮蔽</el-radio>
            <el-radio value="hash">哈希</el-radio>
            <el-radio value="encrypt">加密</el-radio>
            <el-radio value="truncate">截断</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="替换字符" prop="replacement">
          <el-input v-model="form.replacement" placeholder="默认 *" />
        </el-form-item>
        <el-form-item label="显示前缀" prop="prefixLength">
          <el-input-number v-model="form.prefixLength" :min="0" :max="10" />
        </el-form-item>
        <el-form-item label="显示后缀" prop="suffixLength">
          <el-input-number v-model="form.suffixLength" :min="0" :max="10" />
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

const maskTypeMap = { mask: '遮蔽', hash: '哈希', encrypt: '加密', truncate: '截断' }

const pagination = reactive({ page: 1, size: 10, total: 0 })

const tableData = ref([
  { id: 1, name: '手机号脱敏', fieldType: 'phone', pattern: '^1[3-9]\\d{9}$', maskType: 'mask', original: '13812345678', masked: '138****5678', status: 1 },
  { id: 2, name: '身份证号脱敏', fieldType: 'idCard', pattern: '^\\d{17}[\\dXx]$', maskType: 'mask', original: '110101199001011234', masked: '110101********1234', status: 1 },
  { id: 3, name: '银行卡号脱敏', fieldType: 'bankCard', pattern: '^\\d{16,19}$', maskType: 'mask', original: '6222021234567890123', masked: '6222 **** **** 0123', status: 1 },
  { id: 4, name: '邮箱脱敏', fieldType: 'email', pattern: '^\\w+@\\w+\\.\\w+$', maskType: 'mask', original: 'test@example.com', masked: 't***@example.com', status: 1 }
])

const form = reactive({
  id: null, name: '', fieldType: 'phone', pattern: '', maskType: 'mask', replacement: '*', prefixLength: 3, suffixLength: 4, status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  fieldType: [{ required: true, message: '请选择字段类型', trigger: 'change' }],
  maskType: [{ required: true, message: '请选择脱敏方式', trigger: 'change' }]
}

const dialogTitle = computed(() => form.id ? '编辑规则' : '新增规则')

const loadData = () => { loading.value = true; setTimeout(() => { pagination.total = 4; loading.value = false }, 500) }

const handleAdd = () => { Object.assign(form, { id: null, name: '', fieldType: 'phone', pattern: '', maskType: 'mask', replacement: '*', prefixLength: 3, suffixLength: 4, status: 1 }); dialogVisible.value = true }
const handleEdit = (row) => { Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row) => { await ElMessageBox.confirm('确定要删除该规则吗？', '提示', { type: 'warning' }); ElMessage.success('删除成功') }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success(form.id ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

loadData()
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
