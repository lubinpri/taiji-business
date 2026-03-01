<template>
  <div class="page-container">
    <div class="page-header">
      <h2>数据库目录管理</h2>
    </div>
    
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：实例选择和目录树 -->
      <el-col :xs="24" :md="8" :lg="6">
        <el-card shadow="hover" class="instance-selector">
          <template #header>
            <span>选择数据库实例</span>
          </template>
          <el-select v-model="selectedInstance" placeholder="请选择实例" style="width: 100%" @change="handleInstanceChange">
            <el-option v-for="item in instances" :key="item.id" :label="item.instanceName" :value="item.id" />
          </el-select>
        </el-card>
        
        <el-card shadow="hover" class="catalog-tree">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>目录结构</span>
              <el-button type="primary" link :icon="Plus" @click="handleAddFolder">新增</el-button>
            </div>
          </template>
          
          <div v-if="!selectedInstance" class="empty-tip">
            <el-icon :size="40"><InfoFilled /></el-icon>
            <p>请先选择数据库实例</p>
          </div>
          
          <el-tree
            v-if="selectedInstance"
            :data="treeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <span class="node-label">
                  <el-icon v-if="data.type === 'database'"><Grid /></el-icon>
                  <el-icon v-else-if="data.type === 'table'"><Document /></el-icon>
                  <el-icon v-else><Folder /></el-icon>
                  {{ node.label }}
                </span>
                <span v-if="data.type === 'schema'" class="tree-actions">
                  <el-button type="primary" link :icon="Plus" @click.stop="handleAddTable(data)" />
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>
      
      <!-- 右侧：表结构详情 -->
      <el-col :xs="24" :md="16" :lg="18">
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>表结构详情</span>
              <el-tag v-if="currentNode?.type === 'table'" type="primary">{{ currentNode?.name || '-' }}</el-tag>
              <el-tag v-else type="info">请选择</el-tag>
            </div>
          </template>
          
          <div v-if="currentNode?.type === 'table'" class="table-info">
            <el-descriptions :column="2" border class="info-descriptions">
              <el-descriptions-item label="表名">
                <strong>{{ currentNode.name }}</strong>
              </el-descriptions-item>
              <el-descriptions-item label="引擎">{{ currentNode.engine || 'InnoDB' }}</el-descriptions-item>
              <el-descriptions-item label="字符集">{{ currentNode.charset || 'utf8mb4' }}</el-descriptions-item>
              <el-descriptions-item label="行数">{{ currentNode.rows || '0' }}</el-descriptions-item>
            </el-descriptions>
            
            <el-divider content-position="left">字段信息</el-divider>
            
            <el-table :data="columns" border stripe max-height="400">
              <el-table-column prop="name" label="字段名" width="180" />
              <el-table-column prop="type" label="类型" width="150" />
              <el-table-column prop="nullable" label="可空" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.nullable ? 'success' : 'danger'" size="small">
                    {{ row.nullable ? 'YES' : 'NO' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="key" label="主键" width="80">
                <template #default="{ row }">
                  <el-tag v-if="row.key" type="success" size="small">PK</el-tag>
                  <span v-else class="text-muted">-</span>
                </template>
              </el-table-column>
              <el-table-column prop="default" label="默认值" width="120" />
              <el-table-column prop="comment" label="注释" />
            </el-table>
          </div>
          
          <div v-else class="empty-state">
            <el-empty description="请从左侧选择一个表查看详情">
              <template #image>
                <el-icon :size="80" color="#DCBFDF"><Document /></el-icon>
              </template>
            </el-empty>
            <p class="empty-hint">点击左侧目录树中的表名查看详细信息</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Grid, Document, Folder, InfoFilled } from '@element-plus/icons-vue'

const selectedInstance = ref(null)
const currentNode = ref(null)

const instances = ref([
  { id: 1, instanceName: '生产库-MySQL', host: '192.168.1.100', port: 3306 },
  { id: 2, instanceName: '测试库-PostgreSQL', host: '192.168.1.101', port: 5432 }
])

const treeData = ref([
  {
    id: 1, name: 'production_db', type: 'database', children: [
      {
        id: 11, name: 'information_schema', type: 'schema', children: [
          { id: 111, name: 'user', type: 'table', engine: 'InnoDB', charset: 'utf8mb4', rows: 10 }
        ]
      },
      {
        id: 12, name: 'app_db', type: 'schema', children: [
          { id: 121, name: 'users', type: 'table', engine: 'InnoDB', charset: 'utf8mb4', rows: 1000 },
          { id: 122, name: 'orders', type: 'table', engine: 'InnoDB', charset: 'utf8mb4', rows: 5000 },
          { id: 123, name: 'products', type: 'table', engine: 'InnoDB', charset: 'utf8mb4', rows: 200 }
        ]
      }
    ]
  }
])

const columns = ref([
  { name: 'id', type: 'bigint', nullable: false, key: true, default: null, comment: '主键ID' },
  { name: 'username', type: 'varchar(50)', nullable: false, key: false, default: null, comment: '用户名' },
  { name: 'email', type: 'varchar(100)', nullable: true, key: false, default: null, comment: '邮箱' },
  { name: 'password', type: 'varchar(255)', nullable: false, key: false, default: null, comment: '密码' },
  { name: 'created_at', type: 'datetime', nullable: false, key: false, default: 'CURRENT_TIMESTAMP', comment: '创建时间' },
  { name: 'updated_at', type: 'datetime', nullable: false, key: false, default: 'CURRENT_TIMESTAMP', comment: '更新时间' }
])

const handleInstanceChange = (val) => {
  currentNode.value = null
  ElMessage.success(`已切换到实例: ${instances.value.find(i => i.id === val)?.instanceName}`)
}

const handleNodeClick = (data) => {
  if (data.type === 'table') {
    currentNode.value = data
  }
}

const handleAddFolder = () => {
  ElMessage.info('新增文件夹功能')
}

const handleAddTable = (data) => {
  ElMessage.info('新增表功能')
}

onMounted(() => {
  // 可以在这里加载实例数据
})
</script>

<style scoped>
.page-container {
  height: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.content-row {
  min-height: calc(100vh - 180px);
}

.instance-selector {
  margin-bottom: 20px;
}

.catalog-tree {
  min-height: 300px;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-tip p {
  margin-top: 10px;
}

.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 8px;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 5px;
}

.tree-actions {
  display: none;
}

.el-tree-node:hover .tree-actions {
  display: inline;
}

.detail-card {
  min-height: 500px;
}

.table-info {
  margin-top: 10px;
}

.info-descriptions {
  margin-bottom: 20px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-hint {
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.text-muted {
  color: #C0C4CC;
}
</style>
