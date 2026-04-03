package com.demo.service.impl;

import com.demo.entity.ForumComment;
import com.demo.mapper.CommentMapper;
import com.demo.mapper.PostMapper;
import com.demo.service.CommentService;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.demo.utils.RedisKeyUtil.getLikedCommentsKey;

/**
 * 论坛评论服务实现类
 */
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final SensitiveWordBs sensitiveWordBs;
    private final StringRedisTemplate redisTemplate;

    public CommentServiceImpl(CommentMapper commentMapper, 
                              PostMapper postMapper, 
                              SensitiveWordBs sensitiveWordBs, 
                              StringRedisTemplate redisTemplate) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
        this.sensitiveWordBs = sensitiveWordBs;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageInfo<ForumComment> selectAll(ForumComment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ForumComment> commentList = commentMapper.selectAll(comment);
        return new PageInfo<>(commentList);
    }

    @Override
    public List<ForumComment> selectByPostId(Long postId) {
        return commentMapper.selectByPostId(postId);
    }

    @Override
    @Transactional
    public int delete(Long id, Long userId) {
        ForumComment comment = commentMapper.selectById(id);
        if (comment == null) {
            return 0;
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限");
        }
        int result = commentMapper.delete(id);
        if (result > 0) {
            // 回复数减 1
            postMapper.decrementReplyCount(comment.getPostId());
        }
        return result;
    }

    @Override
    @Transactional
    public int insert(ForumComment comment) {
        // 过滤敏感词
        comment.setContent(sensitiveWordBs.replace(comment.getContent()));
        int result = commentMapper.insert(comment);
        if (result > 0) {
            // 回复数加 1
            postMapper.incrementReplyCount(comment.getPostId());
        }
        return result;
    }

    @Override
    @Transactional
    public void incrementLikes(Long id, Long userId) {
        String key = getLikedCommentsKey(userId);
        String commentIdStr = String.valueOf(Objects.requireNonNull(id));
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, commentIdStr);
        if (Boolean.FALSE.equals(isLiked)) {
            redisTemplate.opsForSet().add(key, commentIdStr);
            // 点赞记录有效期 7 天
            redisTemplate.expire(key, 7 * 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
            commentMapper.incrementLikes(id);
        }
    }

    @Override
    @Transactional
    public void decrementLikes(Long id, Long userId) {
        String key = getLikedCommentsKey(userId);
        String commentIdStr = String.valueOf(Objects.requireNonNull(id));
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, commentIdStr);
        if (Boolean.TRUE.equals(isLiked)) {
            redisTemplate.opsForSet().remove(key, commentIdStr);
            commentMapper.decrementLikes(id);
        }
    }
}