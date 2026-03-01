# 版本管理说明

## 本地Git仓库

项目已初始化Git仓库，每次重要修改后请手动提交：

```bash
cd ~/.openclaw/workspace

# 查看修改状态
git status

# 提交所有修改
git add -A
git commit -m "描述你的修改"
```

## 自动备份

运行备份脚本自动备份到本地：
```bash
~/.openclaw/workspace/scripts/backup.sh
```

备份位置: `~/backups/business-system/`

## 推送到GitHub（网络可用时）

当网络可用时，运行：
```bash
~/.openclaw/workspace/scripts/deploy.sh
```

## 恢复文件

如果文件损坏，可以从Git恢复：
```bash
# 查看提交历史
git log

# 恢复指定版本的文件
git checkout COMMIT_HASH -- src/app/opportunities/page.tsx

# 或者恢复整个项目
git reset --hard COMMIT_HASH
```
