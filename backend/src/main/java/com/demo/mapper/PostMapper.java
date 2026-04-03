package com.demo.mapper;

import com.demo.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    //查询所有（管理员）
    List<ForumPost> selectAll(ForumPost forumPost);
    //查询所有（用户）
    List<ForumPost> selectAllUser(ForumPost forumPost);
    //发布帖子
    int insert(ForumPost forumPost);
    //删除帖子
    int delete(Long id);
    //查看帖子
    ForumPost selectById(Long id);
    //增加浏览量
    void incrementViewCount(Long id);
    //增加点赞量
    void incrementLikeCount(Long id);
    //减少点赞量
    void decrementLikeCount(Long id);
    //增加回复量
    void incrementReplyCount(Long id);
    //减少回复量
    void decrementReplyCount(Long id);

}
