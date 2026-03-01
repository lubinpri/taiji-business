<template>
  <div class="page-container">
    <div class="page-header">
      <h2>访问策略配置</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增策略</el-button>
    </div>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="IP访问控制" name="ip">
        <el-table :data="ipRules" border stripe>
          <el-table-column prop="name" label="规则名称" width="150" />
          <el-table-column prop="ipRange" label="IP范围" width="200" />
          <el-table-column prop="action" label="动作" width="100">
            <template #default="{ row }">
              <el-tag :type="row.action === 'allow' ? 'success' : 'danger'">
                {{ row.action === 'allow' ? '允许' : '拒绝' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="时间访问控制" name="time">
        <el-table :data="timeRules" border stripe>
          <el-table-column prop="name" label="规则名称" width="150" />
          <el-table-column prop="weekdays" label="允许日期" width="150" />
          <el-table-column prop="timeRange" label="时间范围" width="150" />
          <el-table-column prop="action" label="动作" width="100">
            <template #default="{ row }">
              <el-tag :type="row.action === 'allow' ? 'success' : 'danger'">
                {{ row.action === 'allow' ? '允许' : '拒绝' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="敏感操作控制" name="sensitive">
        <el-table :data="sensitiveRules" border stripe>
          <el-table-column prop="name" label="规则名称" width="150" />
          <el-table-column prop="operation" label="操作类型" width="150" />
          <el-table-column prop="requireApproval" label="需要审批" width="100">
            <template #default="{ row }">
              <el-tag :type="row.requireApproval ? 'warning' : 'info'">
                {{ row.requireApproval ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="timeout" label="超时时间" width="100">
            <template #default="{ row }">
              {{ row.timeout }}秒
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        
        <template v-if="activeTab === 'ip'">
          <el-form-item label="IP范围" prop="ipRange">
            <el-input v-model="form.ipRange" placeholder="如: 192.168.1.0/24 或 10.0.0.1" />
          </el-form-item>
        </template>
        
        <template v-if="activeTab === 'time'">
          <el-form-item label="允许日期" prop="weekdays">
            <el-checkbox-group v-model="form.weekdays">
              <el-checkbox label="1">周一</el-checkbox>
              <el-checkbox label="2">周二</el-checkbox>
              <el-checkbox label="3">周三</el-checkbox>
              <el-checkbox label="4">周四</el-checkbox>
              <el-checkbox label="5">周五</el-checkbox>
              <el-checkbox label="6">周六</el-checkbox>
              <el-checkbox label="0">周日</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="时间范围" prop="timeRange">
            <el-time-picker v-model="form.timeStart" placeholder="开始时间" style="margin-right: 10px" />
            <el-time-picker v-model="form.timeEnd" placeholder="结束时间" />
          </el-form-item>
        </template>
        
        <template v-if="activeTab === 'sensitive'">
          <el-form-item label="操作类型" prop="operation">
            <el-select v-model="form.operation" placeholder="请选择">
              <el-option label="DELETE" value="DELETE" />
              <el-option label="DROP" value="DROP" />
              <el-option label="TRUNCATE" value="TRUNCATE" />
              <el-option label="UPDATE" value="UPDATE" />
            </el-select>
          </el-form-item>
          <el-form-item label="需要审批" prop="requireApproval">
            <el-switch v-model="form.requireApproval" />
          </el-form-item>
          <el-form-item label="超时时间" prop="timeout">
            <el-input-number v-model="form.timeout" :min="10" :max="3600" />
          </el-form-item>
        </template>
        
        <el-form-item label="动作" prop="action">
          <el-radio-group v-model="form.action">
            <el-radio value="allow">允许</el-radio>
            <el-radio value="deny">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="2" />
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('ip')
const dialogVisible = ref(false)
const formRef = ref(null)

const ipRules = ref([
  { id: 1, name: '内网访问', ipRange: '192.168.0.0/16', action: 'allow', priority: 1, description: '允许内网访问', status: 1 },
  { id: 2, name: '办公区访问', ipRange: '10.0.0.0/8', action: 'allow', priority: 2, description: '允许办公区访问', status: 1 },
  { id: 3, name: '拒绝外部', ipRange: '0.0.0.0/0', action: 'deny', priority: 99, description: '拒绝其他外部访问', status: 0 }
])

const timeRules = ref([
  { id: 1, name: '工作时间', weekdays: ['1', '2', '3', '4', '5'], timeRange: '09:00-18:00', action: 'allow', description: '允许工作时间访问', status: 1 },
  { id: 2, name: '禁止夜间', weekdays: ['1', '2', '3', '4', '5', '6', '0'], timeRange: '22:00-06:00', action: 'deny', description: '禁止夜间访问', status: 1 }
])

const sensitiveRules = ref([
  { id: 1, name: '删除操作审批', operation: 'DELETE', requireApproval: true, timeout: 30, description: '删除操作需要审批', status: 1 },
  { id: 2, name: '表删除控制', operation: 'DROP', requireApproval: true, timeout: 60, description: '删除表需要审批', status: 1 },
  { id: 3, name: '批量更新控制', operation: 'UPDATE', requireApproval: false, timeout: 10, description: '批量更新需要二次确认', status: 1 }
])

const form = reactive({
  id: null, name: '', ipRange: '', weekdays: [], timeStart: '', timeEnd: '', operation: '', requireApproval: false, timeout: 30, action: 'allow', description: '', status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }]
}

const dialogTitle = ref('新增策略')

const handleAdd = () => { dialogTitle.value = '新增策略'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑策略'; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row) => { await ElMessageBox.confirm('确定要删除该规则吗？', '提示', { type: 'warning' }); ElMessage.success('删除成功') }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  ElMessage.success('保存成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
