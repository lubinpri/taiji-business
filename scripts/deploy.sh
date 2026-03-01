#!/bin/bash
# 当网络可用时，一键推送到GitHub

echo "请在GitHub上创建空仓库后运行以下命令："
echo ""
echo "  git remote set-url origin https://github.com/YOUR_USERNAME/YOUR_REPO.git"
echo "  git push -u origin main"
echo ""
echo "当前状态："
cd ~/.openclaw/workspace && git log --oneline -3
