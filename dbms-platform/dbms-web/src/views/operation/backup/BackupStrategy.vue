<template>
  <div class="backup-container">
    <el-tabs v-model="activeTab" type="border-card" class="backup-tabs">
      <!-- 备份一体机配置 -->
      <el-tab-pane label="设备配置" name="device">
        <div class="device-config">
          <div class="section-header">
            <h3>鼎甲备份一体机配置</h3>
            <el-button type="primary" :icon="Plus" @click="handleAddDevice">添加设备</el-button>
          </div>
          
          <el-table :data="devices" border stripe v-loading="deviceLoading">
            <el-table-column prop="name" label="设备名称" width="150" />
            <el-table-column prop="ip" label="IP地址" width="150" />
            <el-table-column prop="port" label="端口" width="100" />
            <el-table-column prop="version" label="版本" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'online' ? 'success' : 'danger'" size="small">
                  {{ row.status === 'online' ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastSync" label="最后同步" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleTestConnect(row)">测试连接</el-button>
                <el-button type="primary" link @click="handleSyncDevice(row)">同步</el-button>
                <el-button type="danger" link @click="handleDeleteDevice(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <!-- 备份作业管理 -->
      <el-tab-pane label="备份作业" name="jobs">
        <div class="job-management">
          <div class="section-header">
            <h3>备份作业管理</h3>
            <div class="header-actions">
              <el-select v-model="selectedDevice" placeholder="选择设备" style="width: 180px; margin-right: 10px">
                <el-option v-for="d in devices" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
              <el-button type="primary" :icon="Plus" @click="handleAddJob">新建作业</el-button>
            </div>
          </div>
          
          <el-table :data="jobs" border stripe v-loading="jobLoading">
            <el-table-column prop="name" label="作业名称" width="180" />
            <el-table-column prop="jobType" label="作业类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.jobType === 'backup' ? 'primary' : 'success'">
                  {{ row.jobType === 'backup' ? '备份' : '恢复' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="source" label="数据源" width="150" />
            <el-table-column prop="destination" label="目标存储" width="150" />
            <el-table-column prop="schedule" label="执行计划" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getJobStatusType(row.status)" size="small">
                  {{ getJobStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastRunTime" label="上次执行" width="180" />
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleRunJob(row)">立即执行</el-button>
                <el-button type="success" link @click="handleViewHistory(row)">历史</el-button>
                <el-button type="warning" link @click="handleEditJob(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteJob(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <!-- 备份任务监控 -->
      <el-tab-pane label="任务监控" name="monitor">
        <div class="task-monitor">
          <div class="section-header">
            <h3>备份任务监控</h3>
            <div class="header-actions">
              <el-radio-group v-model="monitorFilter" size="small">
                <el-radio-button label="all">全部</el-radio-button>
                <el-radio-button label="running">运行中</el-radio-button>
                <el-radio-button label="success">成功</el-radio-button>
                <el-radio-button label="failed">失败</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <!-- 监控统计卡片 -->
          <el-row :gutter="16" class="monitor-stats">
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon" style="background: #409EFF"><el-icon :size="24"><Clock /></el-icon></div>
                <div class="stat-content">
                  <div class="stat-value">{{ runningTasks }}</div>
                  <div class="stat-label">运行中</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon" style="background: #67C23A"><el-icon :size="24"><CircleCheck /></el-icon></div>
                <div class="stat-content">
                  <div class="stat-value">{{ successTasks }}</div>
                  <div class="stat-label">成功</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon" style="background: #F56C6C"><el-icon :size="24"><Warning /></el-icon></div>
                <div class="stat-content">
                  <div class="stat-value">{{ failedTasks }}</div>
                  <div class="stat-label">失败</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-icon" style="background: #E6A23C"><el-icon :size="24"><DataAnalysis /></el-icon></div>
                <div class="stat-content">
                  <div class="stat-value">{{ totalTasks }}</div>
                  <div class="stat-label">总任务数</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <!-- 任务列表 -->
          <el-table :data="filteredTasks" border stripe v-loading="taskLoading" class="task-table">
            <el-table-column prop="jobName" label="作业名称" width="180" />
            <el-table-column prop="taskId" label="任务ID" width="120" />
            <el-table-column prop="taskType" label="任务类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.taskType === 'backup' ? 'primary' : 'success'">
                  {{ row.taskType === 'backup' ? '备份' : '恢复' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="progress" label="进度" width="180">
              <template #default="{ row }">
                <el-progress :percentage="row.progress" :status="row.status === 'failed' ? 'exception' : ''" />
              </template>
            </el-table-column>
            <el-table-column prop="size" label="数据量" width="120" />
            <el-table-column prop="speed" label="速度" width="100" />
            <el-table-column prop="startTime" label="开始时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getJobStatusType(row.status)" size="small">
                  {{ getJobStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.status === 'running'" type="danger" link @click="handleStopTask(row)">停止</el-button>
                <el-button v-if="row.status === 'failed'" type="warning" link @click="handleRetryTask(row)">重试</el-button>
                <el-button type="primary" link @click="handleViewTaskLog(row)">日志</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <!-- 存储管理 -->
      <el-tab-pane label="存储管理" name="storage">
        <div class="storage-management">
          <div class="section-header">
            <h3>存储池管理</h3>
          </div>
          
          <el-row :gutter="16">
            <el-col :span="8" v-for="pool in storagePools" :key="pool.id">
              <el-card shadow="hover" class="storage-pool-card">
                <template #header>
                  <div class="pool-header">
                    <span class="pool-name">{{ pool.name }}</span>
                    <el-tag size="small" :type="pool.status === 'online' ? 'success' : 'danger'">
                      {{ pool.status === 'online' ? '在线' : '离线' }}
                    </el-tag>
                  </div>
                </template>
                <div class="pool-content">
                  <el-progress :percentage="pool.usedPercent" :stroke-width="10" :color="getStorageColor(pool.usedPercent)" />
                  <div class="pool-info">
                    <span>已用: {{ pool.used }} / {{ pool.total }}</span>
                    <span>可用: {{ pool.available }}</span>
                  </div>
                  <div class="pool-meta">
                    <span>类型: {{ pool.type }}</span>
                    <span>挂载点: {{ pool.mountPoint }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
      
      <!-- 告警中心 -->
      <el-tab-pane label="告警中心" name="alerts">
        <div class="alert-center">
          <div class="section-header">
            <h3>告警信息</h3>
            <div class="header-actions">
              <el-badge :value="unreadAlerts" :hidden="unreadAlerts === 0">
                <el-button @click="handleReadAll">全部已读</el-button>
              </el-badge>
            </div>
          </div>
          
          <el-table :data="alerts" border stripe>
            <el-table-column prop="level" label="级别" width="100">
              <template #default="{ row }">
                <el-tag :type="getAlertLevelType(row.level)" size="small">
                  {{ getAlertLevelText(row.level) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="告警标题" />
            <el-table-column prop="source" label="数据源" width="150" />
            <el-table-column prop="time" label="发生时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'resolved' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'resolved' ? '已处理' : '待处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewAlert(row)">查看</el-button>
                <el-button v-if="row.status !== 'resolved'" type="success" link @click="handleResolveAlert(row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 添加设备对话框 -->
    <el-dialog v-model="deviceDialogVisible" title="添加备份设备" width="500px">
      <el-form ref="deviceFormRef" :model="deviceForm" :rules="deviceFormRules" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="deviceForm.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备IP" prop="ip">
          <el-input v-model="deviceForm.ip" placeholder="请输入设备IP地址" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="deviceForm.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="deviceForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="deviceForm.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="API版本" prop="apiVersion">
          <el-select v-model="deviceForm.apiVersion" placeholder="选择API版本">
            <el-option label="v1 (标准版)" value="v1" />
            <el-option label="v2 (企业版)" value="v2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deviceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDevice">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 新建作业对话框 -->
    <el-dialog v-model="jobDialogVisible" title="新建备份作业" width="600px">
      <el-form ref="jobFormRef" :model="jobForm" :rules="jobFormRules" label-width="100px">
        <el-form-item label="作业名称" prop="name">
          <el-input v-model="jobForm.name" placeholder="请输入作业名称" />
        </el-form-item>
        <el-form-item label="作业类型" prop="jobType">
          <el-radio-group v-model="jobForm.jobType">
            <el-radio value="backup">备份</el-radio>
            <el-radio value="restore">恢复</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数据源" prop="source">
          <el-select v-model="jobForm.source" placeholder="选择数据源">
            <el-option-group v-for="group in dataSources" :key="group.label" :label="group.label">
              <el-option v-for="item in group.options" :key="item.value" :label="item.label" :value="item.value" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="目标存储" prop="destination">
          <el-select v-model="jobForm.destination" placeholder="选择目标存储">
            <el-option v-for="pool in storagePools" :key="pool.id" :label="pool.name" :value="pool.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行计划" prop="schedule">
          <el-cron v-model="jobForm.schedule" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保留天数" prop="retention">
              <el-input-number v-model="jobForm.retention" :min="1" :max="365" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="压缩等级" prop="compression">
              <el-select v-model="jobForm.compression">
                <el-option label="快速压缩" value="fast" />
                <el-option label="标准压缩" value="normal" />
                <el-option label="高压缩比" value="high" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否启用">
          <el-switch v-model="jobForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="jobDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitJob">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Clock, CircleCheck, Warning, DataAnalysis } from '@element-plus/icons-vue'

const activeTab = ref('device')
const selectedDevice = ref(null)
const monitorFilter = ref('all')

// 设备相关
const deviceLoading = ref(false)
const deviceDialogVisible = ref(false)
const deviceFormRef = ref(null)
const devices = ref([
  { id: 1, name: '鼎甲-生产中心', ip: '192.168.1.200', port: 8080, version: 'v2.0', status: 'online', lastSync: '2024-01-15 10:30:00' },
  { id: 2, name: '鼎甲-灾备中心', ip: '192.168.1.201', port: 8080, version: 'v2.0', status: 'online', lastSync: '2024-01-15 10:28:00' }
])
const deviceForm = ref({ name: '', ip: '', port: 8080, username: '', password: '', apiVersion: 'v2' })
const deviceFormRules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  ip: [{ required: true, message: '请输入设备IP', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 作业相关
const jobLoading = ref(false)
const jobDialogVisible = ref(false)
const jobFormRef = ref(null)
const jobs = ref([
  { id: 1, name: '生产库-每日全量', jobType: 'backup', source: '192.168.1.100:3306', destination: '存储池-生产', schedule: '0 2 * * *', status: 'idle', lastRunTime: '2024-01-15 02:00:00' },
  { id: 2, name: '生产库-增量备份', jobType: 'backup', source: '192.168.1.100:3306', destination: '存储池-生产', schedule: '0 */4 * * *', status: 'idle', lastRunTime: '2024-01-15 08:00:00' },
  { id: 3, name: '测试库-日备', jobType: 'backup', source: '192.168.1.101:5432', destination: '存储池-测试', schedule: '0 3 * * *', status: 'idle', lastRunTime: '2024-01-15 03:00:00' },
  { id: 4, name: '生产库-恢复演练', jobType: 'restore', source: '存储池-生产', destination: '192.168.1.102:3306', schedule: '0 4 * * 0', status: 'idle', lastRunTime: '2024-01-14 04:00:00' }
])
const jobForm = ref({ name: '', jobType: 'backup', source: '', destination: '', schedule: '0 2 * * *', retention: 30, compression: 'normal', enabled: true })
const jobFormRules = {
  name: [{ required: true, message: '请输入作业名称', trigger: 'blur' }],
  source: [{ required: true, message: '请选择数据源', trigger: 'change' }],
  destination: [{ required: true, message: '请选择目标存储', trigger: 'change' }]
}

// 任务监控相关
const taskLoading = ref(false)
const tasks = ref([
  { id: 1, jobName: '生产库-每日全量', taskId: 'BK20240115020001', taskType: 'backup', progress: 100, size: '2.5GB', speed: '150MB/s', startTime: '2024-01-15 02:00:00', status: 'success' },
  { id: 2, jobName: '生产库-增量备份', taskId: 'BK20240115060002', taskType: 'backup', progress: 45, size: '500MB', speed: '120MB/s', startTime: '2024-01-15 06:00:00', status: 'running' },
  { id: 3, jobName: '测试库-日备', taskId: 'BK20240115030003', taskType: 'backup', progress: 100, size: '800MB', speed: '80MB/s', startTime: '2024-01-15 03:00:00', status: 'success' },
  { id: 4, jobName: '生产库-恢复演练', taskId: 'RS20240114040001', taskType: 'restore', progress: 100, size: '2.5GB', speed: '100MB/s', startTime: '2024-01-14 04:00:00', status: 'failed' }
])

const runningTasks = computed(() => tasks.value.filter(t => t.status === 'running').length)
const successTasks = computed(() => tasks.value.filter(t => t.status === 'success').length)
const failedTasks = computed(() => tasks.value.filter(t => t.status === 'failed').length)
const totalTasks = computed(() => tasks.value.length)
const filteredTasks = computed(() => {
  if (monitorFilter.value === 'all') return tasks.value
  return tasks.value.filter(t => t.status === monitorFilter.value)
})

// 存储池
const storagePools = ref([
  { id: 1, name: '存储池-生产', type: 'Deduplication', total: '10TB', used: '6TB', available: '4TB', usedPercent: 60, status: 'online', mountPoint: '/backup/prod' },
  { id: 2, name: '存储池-测试', type: 'Deduplication', total: '5TB', used: '2TB', available: '3TB', usedPercent: 40, status: 'online', mountPoint: '/backup/test' },
  { id: 3, name: '存储池-归档', type: 'Tape', total: '20TB', used: '15TB', available: '5TB', usedPercent: 75, status: 'online', mountPoint: '/backup/archive' }
])

// 告警
const unreadAlerts = ref(2)
const alerts = ref([
  { id: 1, level: 'critical', title: '存储池容量不足', source: '存储池-生产', time: '2024-01-15 09:30:00', status: 'pending' },
  { id: 2, level: 'warning', title: '备份任务执行超时', source: '生产库-增量备份', time: '2024-01-15 08:15:00', status: 'pending' },
  { id: 3, level: 'info', title: '设备连接恢复', source: '鼎甲-灾备中心', time: '2024-01-15 08:00:00', status: 'resolved' }
])

// 数据源
const dataSources = ref([
  { label: 'MySQL', options: [{ label: '生产库-MySQL (192.168.1.100:3306)', value: 'mysql-prod' }, { label: '测试库-MySQL (192.168.1.101:3306)', value: 'mysql-test' }] },
  { label: 'PostgreSQL', options: [{ label: '生产库-PG (192.168.1.100:5432)', value: 'pg-prod' }] },
  { label: 'Oracle', options: [{ label: '生产库-Oracle (192.168.1.100:1521)', value: 'oracle-prod' }] }
])

// 方法
const getJobStatusType = (status) => {
  const types = { idle: 'info', running: 'warning', success: 'success', failed: 'danger' }
  return types[status] || 'info'
}

const getJobStatusText = (status) => {
  const texts = { idle: '空闲', running: '运行中', success: '成功', failed: '失败' }
  return texts[status] || status
}

const getStorageColor = (percent) => {
  if (percent >= 90) return '#F56C6C'
  if (percent >= 70) return '#E6A23C'
  return '#67C23A'
}

const getAlertLevelType = (level) => {
  const types = { critical: 'danger', warning: 'warning', info: 'info' }
  return types[level] || 'info'
}

const getAlertLevelText = (level) => {
  const texts = { critical: '严重', warning: '警告', info: '信息' }
  return texts[level] || level
}

// 设备操作
const handleAddDevice = () => {
  deviceForm.value = { name: '', ip: '', port: 8080, username: '', password: '', apiVersion: 'v2' }
  deviceDialogVisible.value = true
}

const handleSubmitDevice = async () => {
  if (!deviceFormRef.value) return
  await deviceFormRef.value.validate()
  devices.value.push({ ...deviceForm.value, id: Date.now(), version: 'v2.0', status: 'online', lastSync: '-' })
  deviceDialogVisible.value = false
  ElMessage.success('设备添加成功')
}

const handleTestConnect = (row) => {
  ElMessage.info(`正在测试连接 ${row.ip}...`)
  setTimeout(() => ElMessage.success('连接成功'), 1000)
}

const handleSyncDevice = (row) => {
  ElMessage.info(`正在同步设备 ${row.name}...`)
  setTimeout(() => {
    row.lastSync = new Date().toLocaleString()
    ElMessage.success('同步成功')
  }, 1000)
}

const handleDeleteDevice = async (row) => {
  await ElMessageBox.confirm('确定要删除该设备吗？', '提示', { type: 'warning' })
  devices.value = devices.value.filter(d => d.id !== row.id)
  ElMessage.success('删除成功')
}

// 作业操作
const handleAddJob = () => {
  jobForm.value = { name: '', jobType: 'backup', source: '', destination: '', schedule: '0 2 * * *', retention: 30, compression: 'normal', enabled: true }
  jobDialogVisible.value = true
}

const handleSubmitJob = async () => {
  if (!jobFormRef.value) return
  await jobFormRef.value.validate()
  jobs.value.push({ ...jobForm.value, id: Date.now(), status: 'idle', lastRunTime: '-' })
  jobDialogVisible.value = false
  ElMessage.success('作业创建成功')
}

const handleRunJob = (row) => {
  ElMessage.success(`已触发作业: ${row.name}`)
}

const handleViewHistory = (row) => {
  ElMessage.info(`查看作业历史: ${row.name}`)
}

const handleEditJob = (row) => {
  jobForm.value = { ...row }
  jobDialogVisible.value = true
}

const handleDeleteJob = async (row) => {
  await ElMessageBox.confirm('确定要删除该作业吗？', '提示', { type: 'warning' })
  jobs.value = jobs.value.filter(j => j.id !== row.id)
  ElMessage.success('删除成功')
}

// 任务操作
const handleStopTask = (row) => {
  ElMessage.warning(`停止任务: ${row.taskId}`)
}

const handleRetryTask = (row) => {
  ElMessage.success(`重试任务: ${row.taskId}`)
}

const handleViewTaskLog = (row) => {
  ElMessage.info(`查看任务日志: ${row.taskId}`)
}

// 告警操作
const handleReadAll = () => {
  unreadAlerts.value = 0
  alerts.value.forEach(a => a.status = 'resolved')
  ElMessage.success('全部已读')
}

const handleViewAlert = (row) => {
  ElMessage.info(`查看告警详情: ${row.title}`)
}

const handleResolveAlert = (row) => {
  row.status = 'resolved'
  unreadAlerts.value = Math.max(0, unreadAlerts.value - 1)
  ElMessage.success('已处理')
}
</script>

<style scoped>
.backup-container {
  height: calc(100vh - 140px);
}

.backup-tabs {
  height: 100%;
}

.backup-tabs .el-tab-pane {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
}

/* 设备配置 */
.device-config {
  padding: 10px;
}

/* 作业管理 */
.job-management {
  padding: 10px;
}

/* 任务监控 */
.task-monitor {
  padding: 10px;
}

.monitor-stats {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-content {
  margin-left: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.task-table {
  margin-top: 20px;
}

/* 存储管理 */
.storage-management {
  padding: 10px;
}

.storage-pool-card {
  margin-bottom: 16px;
}

.pool-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pool-name {
  font-weight: 500;
}

.pool-content {
  padding: 10px 0;
}

.pool-info {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 13px;
  color: #606266;
}

.pool-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

/* 告警中心 */
.alert-center {
  padding: 10px;
}
</style>
