package com.demo.service;

import com.demo.entity.ForumComment;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 论坛评论服务接口
 * 提供帖子的评论功能，包括评论的发布、删除、分页查询以及点赞互动
 */
public interface CommentService {
    /**
     * 条件分页查询所有评论
     * @param comment 查询条件实体
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的评论列表
     */
    PageInfo<ForumComment> selectAll(ForumComment comment, Integer pageNum, Integer pageSize);

    /**
     * 获取指定帖子的所有评论
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<ForumComment> selectByPostId(Long postId);

    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 操作用户ID（用于权限校验）
     * @return 受影响行数
     */
    int delete(Long id, Long userId);

    /**
     * 发布新评论
     * @param comment 评论实体
     * @return 受影响行数
     */
    int insert(ForumComment comment);

    /**
     * 评论点赞
     * @param id 评论ID
     * @param userId 点赞用户ID
     */
    void incrementLikes(Long id, Long userId);

    /**
     * 取消评论点赞
     * @param id 评论ID
     * @param userId 操作用户ID
     */
    void decrementLikes(Long id, Long userId);
}