<template>
  <div class="forum-page">
    <div class="page-header">
      <h1>社区论坛</h1>
      <p>分享回收经验，交流数码心得</p>
    </div>
    
    <div class="forum-content">
      <div class="action-bar">
        <el-button type="primary" @click="showPostDialog">
          <el-icon><Plus /></el-icon>
          发布帖子
        </el-button>
      </div>
      
      <div class="post-list" v-loading="loading">
        <el-empty v-if="posts.length === 0 && !loading" description="暂无帖子">
          <el-button type="primary" @click="showPostDialog">发布第一篇帖子</el-button>
        </el-empty>
        
        <div v-else class="posts">
          <div
            v-for="post in posts"
            :key="post.id"
            class="post-card"
            @click="$router.push(`/forum/${post.id}`)"
          >
            <div class="post-header">
              <h3 class="post-title">{{ post.title }}</h3>
              <div class="post-header-right">
                <span class="post-time">{{ formatDate(post.createTime) }}</span>
                <el-button 
                  v-if="userStore.userInfo?.id === post.userId" 
                  type="danger" 
                  text 
                  size="small"
                  @click.stop="deletePost(post.id)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
            <div class="post-content">
              {{ post.content }}
            </div>
            <div class="post-footer">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ post.viewCount }}</span>
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ post.replyCount }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ post.likeCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 发布帖子弹窗 -->
    <el-dialog v-model="postDialogVisible" title="发布帖子" width="600px">
      <el-form ref="postFormRef" :model="postForm" :rules="postRules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入帖子内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { postApi } from '@/api/forum'
import { useUserStore } from '@/stores/user'
import type { ForumPost } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus/es/components/form'
import { Plus, View, ChatDotRound, Star, Delete } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const postDialogVisible = ref(false)

const posts = ref<ForumPost[]>([])
const postFormRef = ref<FormInstance>()

const postForm = reactive({
  title: '',
  content: '',
})

const postRules: FormRules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度为2-50个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, max: 500, message: '内容长度为10-500个字符', trigger: 'blur' },
  ],
}

onMounted(async () => {
  await loadPosts()
})

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await postApi.getList()
    posts.value = res.data
  } catch (error) {
    console.error('Failed to load posts:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const showPostDialog = () => {
  postForm.title = ''
  postForm.content = ''
  postDialogVisible.value = true
}

const submitPost = async () => {
  if (!postFormRef.value) return
  
  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await postApi.create({
          title: postForm.title,
          content: postForm.content,
        })
        ElMessage.success('帖子发布成功')
        postDialogVisible.value = false
        await loadPosts()
      } catch (error) {
        console.error('Failed to submit:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const deletePost = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await postApi.delete(id)
    ElMessage.success('帖子删除成功')
    await loadPosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}
</script>

<style scoped>
.forum-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  border-radius: 12px;
  padding: 40px;
  color: #fff;
  text-align: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.forum-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.action-bar {
  margin-bottom: 20px;
}

.post-card {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-card:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.post-card:last-child {
  margin-bottom: 0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.post-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.post-title {
  font-size: 18px;
  color: #303133;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-time {
  color: #909399;
  font-size: 12px;
}

.post-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.post-footer {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}
</style>