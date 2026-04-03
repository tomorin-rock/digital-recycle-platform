package com.demo.service;

import com.demo.entity.ForumPost;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 论坛帖子服务接口
 * 提供帖子的发布、删除、分页查询以及浏览量/点赞量的统计管理
 */
public interface PostService {
    /**
     * 条件分页查询所有帖子
     * @param post 查询条件实体
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的帖子列表
     */
    PageInfo<ForumPost> selectAll(ForumPost post, Integer pageNum, Integer pageSize);

    /**
     * 获取所有帖子的名称/标题列表（通常用于搜索建议）
     * @param post 过滤条件
     * @return 帖子简要信息列表
     */
    List<ForumPost> selectAllName(ForumPost post);

    /**
     * 发布新文章/帖子
     * @param post 帖子实体
     * @return 受影响行数
     */
    int insert(ForumPost post);

    /**
     * 根据ID删除帖子
     * @param id 帖子ID
     * @return 受影响行数
     */
    int delete(Long id);

    /**
     * 根据ID查询帖子详情
     * @param id 帖子ID
     * @return 帖子实体
     */
    ForumPost selectById(Long id);

    /**
     * 增加帖子的浏览量
     * @param id 帖子ID
     * @param userId 阅读者ID（用于防止重复计数）
     */
    void incrementViews(Long id, Long userId);

    /**
     * 给帖子点赞
     * @param id 帖子ID
     * @param userId 点赞者ID
     */
    void incrementLikes(Long id, Long userId);

    /**
     * 取消对帖子的点赞
     * @param id 帖子ID
     * @param userId 操作者ID
     */
    void decrementLikes(Long id, Long userId);
}
