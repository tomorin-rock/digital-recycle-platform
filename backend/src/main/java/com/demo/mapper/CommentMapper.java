package com.demo.mapper;

import com.demo.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //查询所有
    List<ForumComment> selectAll(ForumComment forumComment);
    //发布评论
    int insert(ForumComment forumComment);
    //删除评论
    int delete(Long id);
    //查询某帖子所有评论
    List<ForumComment> selectByPostId(Long postId);
    //根据id查询
    ForumComment selectById(Long id);
    //点赞
    void incrementLikes(Long id);
    //取消点赞
    void decrementLikes(Long id);
}
