<template>
  <div class="post-detail-page" v-loading="loading">
    <template v-if="post">
      <div class="post-main">
        <h1 class="post-title">{{ post.title }}</h1>

        <div class="post-meta">
          <span class="author">{{ userStore.userInfo?.nickname }}</span>
          <span class="time">{{ formatDate(post.createTime) }}</span>
          <span class="views">浏览 {{ post.viewCount }}</span>
        </div>

        <div class="post-content">
          {{ post.content }}
        </div>

        <div class="post-actions">
          <el-button type="primary" :icon="Star" @click="toggleLike">
            {{ liked ? '取消点赞' : '点赞' }} ({{ post.likeCount }})
          </el-button>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h2>评论 ({{ comments.length }})</h2>
        <div class="comment-input">
          <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
          />
          <el-button type="primary" :loading="submittingComment" @click="submitComment">
            发布
          </el-button>
        </div>

        <div class="comment-list">
          <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条评论吧" />

          <!-- 评论列表 -->
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="comment-author">{{ userStore.userInfo?.nickname }}</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                  <el-button v-if="canDeleteComment(comment)" type="danger" text size="small" @click="deleteComment(comment.id)">
                    删除
                  </el-button>
                </div>
                <div class="comment-content">
                  <span v-if="comment.parentId" class="reply-to">
                    回复 {{ getCommentAuthor(comment.parentId as number) }}:
                  </span>
                  {{ comment.content }}
                </div>
                <div class="comment-actions">
                  <el-button type="primary" text size="small" @click="toggleCommentLike(comment.id)">
                    <el-icon><Star /></el-icon>
                    {{ comment.liked ? '取消点赞' : '点赞' }} ({{ comment.likeCount }})
                  </el-button>
                  <el-button type="default" text size="small" @click="showReplyInput(comment.id)">
                    回复
                  </el-button>
                </div>

                <!-- 回复输入框 -->
                <div v-if="replyingTo === comment.id" class="reply-input">
                  <el-input
                      v-model="replyContent"
                      type="textarea"
                      :rows="2"
                      placeholder="写下你的回复..."
                  />
                  <div class="reply-buttons">
                    <el-button @click="replyingTo = null">取消</el-button>
                    <el-button type="primary" :loading="submittingReply" @click="submitReply(comment.id)">
                      发布
                    </el-button>
                  </div>
                </div>

                <!-- 子评论 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="replies">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <div class="reply-header">
                      <span class="reply-author">{{ userStore.userInfo?.nickname }}</span>
                      <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                      <el-button v-if="canDeleteComment(reply)" type="danger" text size="small" @click="deleteComment(reply.id)">
                        删除
                      </el-button>
                    </div>
                    <div class="reply-content">
                      <span class="reply-to">回复 {{ getCommentAuthor(reply.parentId as number) }}: </span>
                      {{ reply.content }}
                    </div>
                    <div class="reply-actions">
                      <el-button type="primary" text size="small" @click="toggleCommentLike(reply.id)">
                        <el-icon><Star /></el-icon>
                        {{ reply.liked ? '取消点赞' : '点赞' }} ({{ reply.likeCount }})
                      </el-button>
                    </div>
                  </div>
                </div>
          </div>
        </div>
      </div>
    </template>

    <el-empty v-else-if="!loading" description="帖子不存在" />

    <!-- 编辑帖子弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑帖子" width="600px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入帖子标题" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
              v-model="editForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入帖子内容"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingEdit" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>


  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { postApi, commentApi } from '@/api/forum'
import type { ForumPost, ForumComment } from '@/types'

// 扩展 ForumComment 类型以支持本地状态
type CommentWithState = ForumComment & {
  liked: boolean
  replies?: CommentWithState[]
}



import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus/es/components/form'
import { Star } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submittingComment = ref(false)
const submittingReply = ref(false)
const submittingEdit = ref(false)
const editDialogVisible = ref(false)
const liked = ref(false)

const post = ref<ForumPost | null>(null)
const comments = ref<CommentWithState[]>([])
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref<number | null>(null)

const editFormRef = ref<FormInstance>()
const editForm = reactive({
  title: '',
  content: '',
})

const editRules: FormRules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
  ],
  content: [
    {
      required: true, message: '请输入帖子内容', trigger: 'blur'
    },
  ],
}




onMounted(async () => {
  const id = Number(route.params.id)
  if (id) {
    await loadPost(id)
    await loadComments(id)
  }
})

const loadPost = async (id: number) => {
  loading.value = true
  try {
    const res = await postApi.getDetail(id)
    post.value = res.data
  } catch (error) {
    console.error('Failed to load post:', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async (postId: number) => {
  try {
    const res = await commentApi.getList(postId)
    // 为每个评论添加 liked 状态和回复
    const allComments = res.data.map(comment => ({
      ...comment,
      liked: false,
      replies: []
    }))

    // 构建评论树
    const commentMap = new Map<number, CommentWithState>()
    allComments.forEach(comment => {
      commentMap.set(comment.id, comment)
    })

    const rootComments: CommentWithState[] = []
    allComments.forEach(comment => {
      if (comment.parentId) {
        const parent = commentMap.get(comment.parentId)
        if (parent) {
          parent.replies?.push(comment)
        }
      } else {
        rootComments.push(comment)
      }
    })

    comments.value = rootComments
  } catch (error) {
    console.error('Failed to load comments:', error)
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const toggleLike = async () => {
  if (!post.value) return

  try {
    if (liked.value) {
      await postApi.dislike(post.value.id)
      post.value.likeCount--
    } else {
      await postApi.like(post.value.id)
      post.value.likeCount++
    }
    liked.value = !liked.value
  } catch (error) {
    console.error('Failed to toggle like:', error)
  }
}

const submitEdit = async () => {
  if (!editFormRef.value || !post.value) return

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submittingEdit.value = true
      try {
        await postApi.update(post.value!.id, {
          title: editForm.title,
          content: editForm.content,
        })
        ElMessage.success('帖子更新成功')
        editDialogVisible.value = false
        await loadPost(post.value!.id)
      } catch (error) {
        console.error('Failed to edit:', error)
      } finally {
        submittingEdit.value = false
      }
    }
  })
}

const submitComment = async () => {
  if (!post.value || !commentContent.value.trim()) return

  submittingComment.value = true
  try {
    await commentApi.create({
      postId: post.value.id,
      content: commentContent.value,
    })
    ElMessage.success('评论发表成功')
    commentContent.value = ''
    await loadComments(post.value.id)
  } catch (error) {
    console.error('Failed to submit comment:', error)
  } finally {
    submittingComment.value = false
  }
}

const showReplyInput = (commentId: number) => {
  replyingTo.value = commentId
  replyContent.value = ''
}

const submitReply = async (parentId: number) => {
  if (!post.value || !replyContent.value.trim()) return

  submittingReply.value = true
  try {
    await commentApi.create({
      postId: post.value.id,
      content: replyContent.value,
      parentId
    })
    ElMessage.success('回复发表成功')
    replyContent.value = ''
    replyingTo.value = null
    await loadComments(post.value.id)
  } catch (error) {
    console.error('Failed to submit reply:', error)
  } finally {
    submittingReply.value = false
  }
}

const toggleCommentLike = async (id: number) => {
  try {
    // 递归查找评论
    const findComment = (comments: CommentWithState[]): CommentWithState | undefined => {
      for (const comment of comments) {
        if (comment.id === id) {
          return comment
        }
        if (comment.replies) {
          const found = findComment(comment.replies)
          if (found) {
            return found
          }
        }
      }
      return undefined
    }

    const comment = findComment(comments.value)
    if (!comment) return

    if (comment.liked) {
      await commentApi.dislike(id)
      comment.likeCount--
    } else {
      await commentApi.like(id)
      comment.likeCount++
    }
    comment.liked = !comment.liked
  } catch (error) {
    console.error('Failed to toggle comment like:', error)
    ElMessage.error('操作失败，请重试')
  }
}



// 获取评论作者
const getCommentAuthor = (commentId: number): string => {
  const findComment = (comments: CommentWithState[]): CommentWithState | undefined => {
    for (const comment of comments) {
      if (comment.id === commentId) {
        return comment
      }
      if (comment.replies) {
        const found = findComment(comment.replies)
        if (found) {
          return found
        }
      }
    }
    return undefined
  }

  const comment = findComment(comments.value)
  return comment ? (userStore.userInfo?.nickname || '') : '未知用户'
}

// 检查是否可以删除评论
const canDeleteComment = (comment: CommentWithState) => {
  return userStore.userInfo?.id === comment.userId
}

// 删除评论
const deleteComment = async (id: number) => {
  try {
    await commentApi.update(id, '')
    ElMessage.success('评论删除成功')
    // 重新加载评论
    if (post.value) {
      await loadComments(post.value.id)
    }
  } catch (error) {
    console.error('Failed to delete comment:', error)
    ElMessage.error('删除失败，请重试')
  }
}
</script>

<style scoped>
.post-detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.post-main {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
}

.post-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 16px;
}

.post-meta {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.post-actions {
  display: flex;
  gap: 12px;
}

.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

.comment-section h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.comment-input {
  margin-bottom: 24px;
  margin-top: 12px;
}



.comment-item {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 500;
  color: #303133;
}

.comment-time {
  color: #909399;
  font-size: 12px;
}

.comment-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

/* 回复相关样式 */
.reply-to {
  color: #909399;
  font-size: 12px;
}

.reply-input {
  margin-top: 12px;
  margin-left: 40px;
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.reply-buttons {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  justify-content: flex-end;
}

.replies {
  margin-left: 40px;
  margin-top: 12px;
}

.reply-item {
  padding: 12px;
  border-left: 2px solid #e4e7ed;
  margin-bottom: 8px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.reply-author {
  font-weight: 500;
  color: #303133;
  font-size: 13px;
}

.reply-time {
  color: #909399;
  font-size: 11px;
}

.reply-content {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 4px;
}

.reply-actions {
  display: flex;
  gap: 8px;
}


</style>