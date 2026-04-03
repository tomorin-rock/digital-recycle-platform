<template>
  <div class="admin-posts">
    <div class="page-header">
      <h1>帖子管理</h1>
    </div>
    
    <div class="content-card">
      <el-table :data="posts" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="replyCount" label="回复" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewComments(row)">
              查看评论
            </el-button>
            <el-button type="danger" text size="small" @click="deletePost(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 评论对话框 -->
    <el-dialog v-model="commentDialogVisible" :title="`查看帖子评论 - ${currentPost?.title || ''}`" width="800px">
      <div v-if="comments.length > 0">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="comment-user">{{ userStore.userInfo?.nickname }}</span>
            <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
            <el-button type="danger" text size="small" @click="deleteComment(comment.id)" style="margin-left: auto;">
              删除
            </el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-footer">
            <el-tag size="small">点赞: {{ comment.likeCount }}</el-tag>
          </div>
        </div>
      </div>
      <div v-else class="no-comments">
        该帖子暂无评论
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminPostApi } from '@/api/admin'
import { commentApi } from '@/api/forum'
import type { ForumPost, ForumComment } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import {useUserStore} from "@/stores/user.ts";

const loading = ref(false)
const userStore = useUserStore()
const posts = ref<ForumPost[]>([])

// 评论相关
const commentDialogVisible = ref(false)
const currentPost = ref<ForumPost | null>(null)
const comments = ref<ForumComment[]>([])

onMounted(async () => {
  await loadPosts()
})

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await adminPostApi.getList()
    posts.value = res.data
  } catch (error) {
    console.error('Failed to load posts:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

const deletePost = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await adminPostApi.delete(id)
    ElMessage.success('帖子删除成功')
    await loadPosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

// 查看评论
const viewComments = async (post: ForumPost) => {
  currentPost.value = post
  commentDialogVisible.value = true
  
  try {
    const res = await commentApi.getList(post.id)
    comments.value = res.data
  } catch (error) {
    console.error('Failed to load comments:', error)
    ElMessage.error('加载评论失败')
  }
}

// 删除评论
const deleteComment = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await adminPostApi.deleteComment(id)
    ElMessage.success('评论删除成功')
    
    // 重新加载评论
    if (currentPost.value) {
      const res = await commentApi.getList(currentPost.value.id)
      comments.value = res.data
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete comment:', error)
    }
  }
}
</script>

<style scoped>
.admin-posts { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; color: #303133; }
.content-card { background: #fff; border-radius: 12px; padding: 20px; }

/* 评论样式 */
.comment-item { padding: 16px; border-bottom: 1px solid #f0f0f0; }
.comment-item:last-child { border-bottom: none; }
.comment-header { display: flex; align-items: center; margin-bottom: 8px; }
.comment-user { font-weight: bold; margin-right: 16px; }
.comment-time { color: #909399; font-size: 12px; }
.comment-content { margin-bottom: 8px; line-height: 1.5; }
.comment-footer { margin-top: 8px; }
.no-comments { text-align: center; padding: 40px; color: #909399; }
</style>