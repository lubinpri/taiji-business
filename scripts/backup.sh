#!/bin/bash
# 自动备份脚本 - 每天自动备份项目到本地

BACKUP_DIR="$HOME/backups/business-system"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p "$BACKUP_DIR"

# 备份到带时间戳的目录
cp -r ~/.openclaw/workspace/src "$BACKUP_DIR/src_$DATE"
cp -r ~/.openclaw/workspace/docs "$BACKUP_DIR/docs_$DATE" 2>/dev/null
cp -r ~/.openclaw/workspace/supabase "$BACKUP_DIR/supabase_$DATE" 2>/dev/null

# 只保留最近5个备份
cd "$BACKUP_DIR"
ls -dt src_* | tail -n +6 | xargs -r rm -rf

echo "Backup completed: $DATE"
ls -la
