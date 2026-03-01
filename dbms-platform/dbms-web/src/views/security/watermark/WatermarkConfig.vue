<template>
  <div class="page-container">
    <div class="page-header">
      <h2>水印配置</h2>
      <el-button type="primary" @click="handleSave">保存配置</el-button>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>水印设置</span>
          </template>
          
          <el-form :model="form" label-width="120px">
            <el-form-item label="水印开关">
              <el-switch v-model="form.enabled" />
            </el-form-item>
            
            <el-form-item label="水印类型">
              <el-radio-group v-model="form.type">
                <el-radio value="text">文字水印</el-radio>
                <el-radio value="image">图片水印</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <template v-if="form.type === 'text'">
              <el-form-item label="水印内容">
                <el-input v-model="form.text" placeholder="请输入水印文字" />
              </el-form-item>
              
              <el-form-item label="字体大小">
                <el-input-number v-model="form.fontSize" :min="12" :max="72" />
              </el-form-item>
              
              <el-form-item label="字体颜色">
                <el-color-picker v-model="form.color" />
              </el-form-item>
            </template>
            
            <template v-else>
              <el-form-item label="水印图片">
                <el-upload action="#" :auto-upload="false" :show-file-list="false">
                  <el-button>选择图片</el-button>
                </el-upload>
              </el-form-item>
            </template>
            
            <el-form-item label="透明度">
              <el-slider v-model="form.opacity" :min="0.1" :max="1" :step="0.1" show-stops />
            </el-form-item>
            
            <el-form-item label="旋转角度">
              <el-input-number v-model="form.rotation" :min="-180" :max="180" />
            </el-form-item>
            
            <el-form-item label="水印位置">
              <el-select v-model="form.position">
                <el-option label="全屏铺满" value="full" />
                <el-option label="右下角" value="bottomRight" />
                <el-option label="左下角" value="bottomLeft" />
                <el-option label="平铺" value="tiled" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="水平间距">
              <el-input-number v-model="form.horizontalGap" :min="0" :max="500" />
            </el-form-item>
            
            <el-form-item label="垂直间距">
              <el-input-number v-model="form.verticalGap" :min="0" :max="500" />
            </el-form-item>
            
            <el-form-item label="仅管理员可见">
              <el-switch v-model="form.adminOnly" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>预览效果</span>
          </template>
          
          <div class="preview-container" :style="previewStyle">
            <div class="preview-content">
              <h3>数据预览</h3>
              <el-table :data="previewData" border size="small">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="姓名" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="idCard" label="身份证号" />
              </el-table>
            </div>
            
            <div v-if="form.enabled && form.type === 'text'" class="watermark-tiled">
              <span v-for="i in 20" :key="i" :style="watermarkStyle">{{ form.text }}</span>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>
            <span>变量说明</span>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="{username}">当前登录用户名</el-descriptions-item>
            <el-descriptions-item label="{datetime}">当前日期时间</el-descriptions-item>
            <el-descriptions-item label="{ip}">用户IP地址</el-descriptions-item>
            <el-descriptions-item label="{department}">用户部门</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({
  enabled: true,
  type: 'text',
  text: '内部资料 · 禁止外传 · {username}',
  fontSize: 14,
  color: '#000000',
  opacity: 0.3,
  rotation: -30,
  position: 'tiled',
  horizontalGap: 100,
  verticalGap: 80,
  adminOnly: false
})

const previewData = ref([
  { id: 1, name: '张三', phone: '13812345678', idCard: '110101199001011234' },
  { id: 2, name: '李四', phone: '13998765432', idCard: '310101199002022345' }
])

const previewStyle = computed(() => ({
  position: 'relative',
  minHeight: '300px',
  background: '#fff',
  border: '1px solid #eee'
}))

const watermarkStyle = computed(() => ({
  position: 'absolute',
  fontSize: `${form.fontSize}px`,
  color: form.color,
  opacity: form.opacity,
  transform: `rotate(${form.rotation}deg)`,
  whiteSpace: 'nowrap',
  pointerEvents: 'none'
}))

const handleSave = () => {
  ElMessage.success('水印配置已保存')
}
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }

.preview-container { position: relative; overflow: hidden; }
.preview-content { position: relative; z-index: 1; padding: 20px; }
.watermark-tiled { position: absolute; top: 0; left: 0; right: 0; bottom: 0; overflow: hidden; pointer-events: none; }
</style>
