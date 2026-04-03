import { request } from './index'
import type { ForumPost, ForumComment } from '@/types'

// 论坛帖子 API
export const postApi = {
  // 发布帖子
  create(data: { title: string; content: string }) {
    return request.post<ForumPost>('/user/posts', data)
  },
  
  // 查询帖子列表
  getList() {
    return request.get<ForumPost[]>('/user/posts')
  },
  
  // 查看帖子详情
  getDetail(id: number) {
    return request.get<ForumPost>(`/user/posts/${id}`)
  },
  
  // 点赞帖子
  like(id: number) {
    return request.put<string>(`/user/posts/${id}/like`)
  },
  
  // 取消点赞
  dislike(id: number) {
    return request.put<string>(`/user/posts/${id}/dislike`)
  },
  
  // 修改帖子
  update(id: number, data: { title: string; content: string }) {
    return request.put<string>(`/user/posts/${id}`, data)
  },
  // 删除帖子
  delete(id: number) {
    return request.delete<string>(`/user/posts/${id}`)
  },
}

// 论坛评论 API
export const commentApi = {
  // 查询帖子评论
  getList(postId: number) {
    return request.get<ForumComment[]>(`/user/comments/${postId}`)
  },
  
  // 发表评论
  create(data: { postId: number; content: string; parentId?: number }) {
    return request.post<ForumComment>('/user/comments', data)
  },
  
  // 点赞评论
  like(id: number) {
    return request.put<string>(`/user/comments/${id}/like`)
  },
  
  // 取消点赞
  dislike(id: number) {
    return request.put<string>(`/user/comments/${id}/dislike`)
  },
  
  // 修改评论
  update(id: number, content: string) {
    return request.put<string>(`/user/comments/${id}`, { content })
  },
  
  // 删除评论
  delete(id: number) {
    return request.put<string>(`/user/comments/${id}`)
  },
}

// 管理员论坛管理 API
export const adminPostApi = {
  // 删除帖子
  deletePost(id: number) {
    return request.delete<string>(`/admin/posts/${id}`)
  },
  
  // 删除评论
  deleteComment(id: number) {
    return request.delete<string>(`/admin/comments/${id}`)
  },
}