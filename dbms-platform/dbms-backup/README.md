# DBMS-Backup 模块

数据库管理平台的备份管理服务模块，提供备份策略管理、备份任务执行、备份恢复和任务调度功能。

## 模块结构

```
dbms-backup/
├── pom.xml
└── src/main/
    ├── java/com/dbms/backup/
    │   ├── DbmsBackupApplication.java      # 启动类
    │   ├── config/                        # 配置类
    │   │   ├── MyBatisPlusConfig.java
    │   │   └── WebConfig.java
    │   ├── controller/                     # 控制器
    │   │   └── BackupController.java
    │   ├── dto/                           # 数据传输对象
    │   │   ├── BackupPolicyRequest.java
    │   │   ├── BackupTaskDTO.java
    │   │   └── RestoreRequest.java
    │   ├── entity/                        # 实体类
    │   │   ├── BackupPolicy.java
    │   │   └── BackupTask.java
    │   ├── mapper/                       # Mapper接口
    │   │   ├── BackupPolicyMapper.java
    │   │   └── BackupTaskMapper.java
    │   ├── scheduler/                    # 调度任务
    │   │   └── BackupScheduler.java
    │   └── service/                      # 服务接口和实现
    │       ├── BackupPolicyService.java
    │       ├── BackupTaskService.java
    │       └── impl/
    │           ├── BackupPolicyServiceImpl.java
    │           └── BackupTaskServiceImpl.java
    └── resources/
        └── application.yml               # 应用配置
```

## 核心功能

### 1. 备份策略管理 (CRUD)
- 创建备份策略
- 查询策略列表
- 获取策略详情
- 更新策略
- 删除策略
- 启用/禁用策略

### 2. 备份任务执行
- 手动触发备份
- 自动执行备份（通过调度器）
- 任务状态跟踪

### 3. 备份恢复
- 从备份文件恢复数据
- 指定目标实例和数据库

### 4. 任务调度
- 基于Spring Task的定时任务
- 支持Cron表达式配置
- 每天凌晨清理过期备份

### 5. 任务监控
- 任务状态统计
- 任务列表查询
- 任务详情查看
- 取消运行中的任务

## API接口

### 策略管理
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/backup/policies | 创建备份策略 |
| GET | /api/backup/policies | 获取策略列表 |
| GET | /api/backup/policies/{id} | 获取策略详情 |
| PUT | /api/backup/policies/{id} | 更新策略 |
| DELETE | /api/backup/policies/{id} | 删除策略 |
| PUT | /api/backup/policies/{id}/toggle | 启用/禁用策略 |
| GET | /api/backup/policies/enabled | 获取所有启用的策略 |

### 任务管理
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/backup/tasks/backup | 手动触发备份 |
| GET | /api/backup/tasks | 获取任务列表 |
| GET | /api/backup/tasks/{id} | 获取任务详情 |
| POST | /api/backup/tasks/{id}/cancel | 取消任务 |
| POST | /api/backup/tasks/restore | 执行恢复 |

### 监控统计
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/backup/statistics | 获取任务统计 |

## 配置说明

```yaml
server:
  port: 8084

spring:
  application:
    name: dbms-backup
  datasource:
    # 数据库配置

backup:
  storage:
    path: /data/backup    # 备份文件存储路径
    max-size: 10240      # 最大存储大小(MB)
  schedule:
    enabled: true        # 是否启用定时任务
```

## 任务状态说明

- **pending**: 等待执行
- **running**: 执行中
- **success**: 执行成功
- **failed**: 执行失败

## 备份类型

- **full**: 全量备份
- **increment**: 增量备份

## 使用示例

### 创建备份策略
```bash
curl -X POST http://localhost:8084/api/backup/policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyName": "daily-full-backup",
    "instanceId": 1,
    "backupType": "full",
    "scheduleCron": "0 0 2 * * ?",
    "retainDays": 30,
    "enabled": 1
  }'
```

### 手动触发备份
```bash
curl -X POST "http://localhost:8084/api/backup/tasks/backup?policyId=1"
```

### 执行恢复
```bash
curl -X POST http://localhost:8084/api/backup/tasks/restore \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 1,
    "targetInstanceId": 2,
    "targetDatabase": "restored_db"
  }'
```

## 技术栈

- Spring Boot 3.2.1
- MyBatis Plus 3.5.5
- Spring Cloud Alibaba Nacos
- Spring Task / Quartz
- MySQL
