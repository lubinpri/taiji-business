<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="30"><Connection /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.instances }}</div>
            <div class="stat-label">数据库实例</div>
            <div class="stat-trend up">
              <el-icon><TrendCharts /></el-icon>
              <span>较昨日 +2</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%)">
            <el-icon :size="30"><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.online }}</div>
            <div class="stat-label">在线实例</div>
            <div class="stat-trend up">
              <el-icon><TrendCharts /></el-icon>
              <span>在线率 98%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="30"><Edit /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.queries.toLocaleString() }}</div>
            <div class="stat-label">今日查询</div>
            <div class="stat-trend up">
              <el-icon><TrendCharts /></el-icon>
              <span>较昨日 +12%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)">
            <el-icon :size="30"><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.alerts }}</div>
            <div class="stat-label">待处理告警</div>
            <div class="stat-trend down" v-if="stats.alerts > 0">
              <span>需关注</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 查询趋势图 -->
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>查询趋势 (近7天)</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 查询类型分布 -->
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>查询类型分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container-pie"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 第二行图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 数据库查询排行 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>数据库查询排行</span>
          </template>
          <div ref="barChartRef" class="chart-container-bar"></div>
        </el-card>
      </el-col>
      
      <!-- 快捷操作 + 最近操作 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="quick-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <div class="quick-item" @click="goQuery">
              <div class="quick-icon" style="background: #409EFF">
                <el-icon :size="24"><Edit /></el-icon>
              </div>
              <span>SQL查询</span>
            </div>
            <div class="quick-item" @click="goInstance">
              <div class="quick-icon" style="background: #67C23A">
                <el-icon :size="24"><Plus /></el-icon>
              </div>
              <span>新增实例</span>
            </div>
            <div class="quick-item" @click="goBackup">
              <div class="quick-icon" style="background: #E6A23C">
                <el-icon :size="24"><FolderAdd /></el-icon>
              </div>
              <span>创建备份</span>
            </div>
            <div class="quick-item" @click="goAudit">
              <div class="quick-icon" style="background: #909399">
                <el-icon :size="24"><Document /></el-icon>
              </div>
              <span>审计日志</span>
            </div>
            <div class="quick-item" @click="goMask">
              <div class="quick-icon" style="background: #F56C6C">
                <el-icon :size="24"><Lock /></el-icon>
              </div>
              <span>脱敏规则</span>
            </div>
            <div class="quick-item" @click="goWatermark">
              <div class="quick-icon" style="background: #722ED1">
                <el-icon :size="24"><Coin /></el-icon>
              </div>
              <span>水印配置</span>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="hover" class="recent-card">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>最近操作</span>
              <el-button type="text" @click="goHistory">查看全部</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item 
              v-for="item in recentOps" 
              :key="item.id" 
              :timestamp="item.time" 
              placement="top"
              :icon="getOpIcon(item.type)"
              :type="getOpType(item.type)"
            >
              <el-card class="timeline-card">
                <h4>{{ item.title }}</h4>
                <p>{{ item.desc }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Connection, CircleCheck, Edit, Warning, TrendCharts, Plus, FolderAdd, Document, Lock, Coin, Search, Plus as PlusIcon, DocumentCopy, Clock, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const chartPeriod = ref('week')
const trendChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)

const stats = reactive({
  instances: 12,
  online: 11,
  queries: 15847,
  alerts: 3
})

const recentOps = ref([
  { id: 1, time: '2026-02-27 10:30', title: '执行SQL查询', desc: 'SELECT * FROM users WHERE status = 1', type: 'query' },
  { id: 2, time: '2026-02-27 10:15', title: '新增数据库实例', desc: '生产库-MySQL (192.168.1.100)', type: 'add' },
  { id: 3, time: '2026-02-27 09:45', title: '备份任务完成', desc: '生产库-每日全量备份 (2.5GB)', type: 'backup' },
  { id: 4, time: '2026-02-27 09:30', title: '脱敏规则应用', desc: '手机号脱敏规则已应用到查询结果', type: 'mask' },
  { id: 5, time: '2026-02-27 09:00', title: '用户登录', desc: '管理员 admin 登录系统', type: 'login' },
  { id: 6, time: '2026-02-27 08:30', title: '删除测试数据', desc: '清理测试库临时表', type: 'delete' }
])

const getOpIcon = (type) => {
  const icons = {
    query: Search,
    add: PlusIcon,
    backup: DocumentCopy,
    login: Clock,
    mask: Lock,
    delete: Delete
  }
  return icons[type] || Clock
}

const getOpType = (type) => {
  const types = {
    query: 'primary',
    add: 'success',
    backup: 'warning',
    mask: 'danger',
    login: 'info',
    delete: 'danger'
  }
  return types[type] || 'info'
}

// 路由跳转
const goQuery = () => router.push('/query/editor')
const goInstance = () => router.push('/database/instance')
const goBackup = () => router.push('/operation/backup')
const goAudit = () => router.push('/operation/audit')
const goHistory = () => router.push('/query/history')
const goMask = () => router.push('/security/mask')
const goWatermark = () => router.push('/security/watermark')

// 初始化图表
const initCharts = () => {
  // 查询趋势图
  const trendChart = echarts.init(trendChartRef.value)
  const trendOption = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['查询次数', '平均耗时(ms)'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['02-21', '02-22', '02-23', '02-24', '02-25', '02-26', '02-27'],
      boundaryGap: false
    },
    yAxis: [
      { type: 'value', name: '查询次数', position: 'left' },
      { type: 'value', name: '耗时(ms)', position: 'right' }
    ],
    series: [
      {
        name: '查询次数',
        type: 'line',
        smooth: true,
        data: [820, 932, 901, 1290, 1520, 1680, 1850],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '平均耗时(ms)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: [45, 52, 48, 55, 42, 38, 35],
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }
  trendChart.setOption(trendOption)

  // 查询类型分布饼图
  const pieChart = echarts.init(pieChartRef.value)
  const pieOption = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: [
          { value: 1048, name: 'SELECT', itemStyle: { color: '#409EFF' } },
          { value: 300, name: 'INSERT', itemStyle: { color: '#67C23A' } },
          { value: 180, name: 'UPDATE', itemStyle: { color: '#E6A23C' } },
          { value: 120, name: 'DELETE', itemStyle: { color: '#F56C6C' } },
          { value: 80, name: '其他', itemStyle: { color: '#909399' } }
        ]
      }
    ]
  }
  pieChart.setOption(pieOption)

  // 数据库查询排行柱状图
  const barChart = echarts.init(barChartRef.value)
  const barOption = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: ['测试库-PostgreSQL', '开发库-Oracle', '分析库-MySQL', '生产库-MySQL']
    },
    series: [
      {
        type: 'bar',
        data: [3200, 4500, 6800, 12500],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        label: { show: true, position: 'right', formatter: '{c} 次' }
      }
    ]
  }
  barChart.setOption(barOption)
  
  // 窗口大小变化时自适应
  window.addEventListener('resize', () => {
    trendChart.resize()
    pieChart.resize()
    barChart.resize()
  })
}

onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  margin-bottom: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
  border: none;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-content {
  margin-left: 16px;
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 8px;
}

.stat-trend.up {
  color: #67C23A;
}

.stat-trend.down {
  color: #F56C6C;
}

.chart-card {
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 320px;
  width: 100%;
}

.chart-container-pie {
  height: 320px;
  width: 100%;
}

.chart-container-bar {
  height: 320px;
  width: 100%;
}

.quick-card {
  margin-bottom: 20px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
}

.quick-item:hover {
  background: #f5f7fa;
  transform: translateY(-3px);
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 8px;
}

.quick-item span {
  font-size: 13px;
  color: #606266;
}

.recent-card {
  max-height: 280px;
  overflow-y: auto;
}

.timeline-card {
  padding: 10px;
}

.timeline-card h4 {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #303133;
}

.timeline-card p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
</style>
