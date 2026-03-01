import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/system/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      // 系统管理
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('../views/system/user/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User', permission: 'system:user' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('../views/system/role/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', permission: 'system:role' }
      },
      {
        path: 'system/menu',
        name: 'MenuManage',
        component: () => import('../views/system/menu/MenuManage.vue'),
        meta: { title: '菜单权限', icon: 'Menu', permission: 'system:menu' }
      },
      // 数据库管理
      {
        path: 'database/instance',
        name: 'InstanceManage',
        component: () => import('../views/database/instance/InstanceManage.vue'),
        meta: { title: '数据库实例', icon: 'Connection', permission: 'database:instance' }
      },
      {
        path: 'database/catalog',
        name: 'CatalogManage',
        component: () => import('../views/database/catalog/CatalogManage.vue'),
        meta: { title: '数据库目录', icon: 'FolderOpened', permission: 'database:catalog' }
      },
      // 查询管理
      {
        path: 'query/editor',
        name: 'QueryEditor',
        component: () => import('../views/query/editor/QueryEditor.vue'),
        meta: { title: 'SQL查询', icon: 'Edit', permission: 'query:editor' }
      },
      {
        path: 'query/history',
        name: 'QueryHistory',
        component: () => import('../views/query/history/QueryHistory.vue'),
        meta: { title: '查询历史', icon: 'Clock', permission: 'query:history' }
      },
      // 数据安全
      {
        path: 'security/mask',
        name: 'MaskRule',
        component: () => import('../views/security/mask/MaskRule.vue'),
        meta: { title: '脱敏规则', icon: 'Hide', permission: 'security:mask' }
      },
      {
        path: 'security/watermark',
        name: 'WatermarkConfig',
        component: () => import('../views/security/watermark/WatermarkConfig.vue'),
        meta: { title: '水印配置', icon: 'PictureFilled', permission: 'security:watermark' }
      },
      {
        path: 'security/policy',
        name: 'AccessPolicy',
        component: () => import('../views/security/policy/AccessPolicy.vue'),
        meta: { title: '访问策略', icon: 'Lock', permission: 'security:policy' }
      },
      // 运维管理
      {
        path: 'operation/backup',
        name: 'BackupStrategy',
        component: () => import('../views/operation/backup/BackupStrategy.vue'),
        meta: { title: '备份策略', icon: 'FolderAdd', permission: 'operation:backup' }
      },
      {
        path: 'operation/task',
        name: 'BackupTask',
        component: () => import('../views/operation/task/BackupTask.vue'),
        meta: { title: '备份任务', icon: 'List', permission: 'operation:task' }
      },
      {
        path: 'operation/audit',
        name: 'AuditLog',
        component: () => import('../views/operation/audit/AuditLog.vue'),
        meta: { title: '审计日志', icon: 'Document', permission: 'operation:audit' }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', icon: 'DataAnalysis' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
