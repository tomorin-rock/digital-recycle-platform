package com.demo.service.impl;

import com.demo.entity.ForumPost;
import com.demo.mapper.PostMapper;
import com.demo.service.PostService;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Objects;

import static com.demo.utils.RedisKeyUtil.getLikedPostsKey;
import static com.demo.utils.RedisKeyUtil.getViewedPostsKey;

/**
 * 论坛帖子服务实现类
 */
@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final SensitiveWordBs sensitiveWordBs;
    private final StringRedisTemplate redisTemplate;

    public PostServiceImpl(PostMapper postMapper, 
                           SensitiveWordBs sensitiveWordBs, 
                           StringRedisTemplate redisTemplate) {
        this.postMapper = postMapper;
        this.sensitiveWordBs = sensitiveWordBs;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageInfo<ForumPost> selectAll(ForumPost post, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ForumPost> postList = postMapper.selectAll(post);
        return new PageInfo<>(postList);
    }

    @Override
    public List<ForumPost> selectAllName(ForumPost post) {
        return postMapper.selectAllUser(post);
    }

    @Override
    @Transactional
    public int insert(ForumPost post) {
        // 发布前过滤敏感词
        String cleanContent = sensitiveWordBs.replace(post.getContent());
        String cleanTitle = sensitiveWordBs.replace(post.getTitle());
        post.setContent(cleanContent);
        post.setTitle(cleanTitle);
        return postMapper.insert(post);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        return postMapper.delete(id);
    }

    @Override
    public ForumPost selectById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    @Transactional
    public void incrementViews(Long id, Long userId) {
        String key = getViewedPostsKey(userId);
        String postIdStr = String.valueOf(Objects.requireNonNull(id));
        Boolean isViewed = redisTemplate.opsForSet().isMember(key, postIdStr);
        if (Boolean.FALSE.equals(isViewed)) {
            // 记录用户已读该贴，防止刷阅读量
            redisTemplate.opsForSet().add(key, postIdStr);
            redisTemplate.expire(key, 7 * 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
            postMapper.incrementViewCount(id);
        }
    }

    @Override
    @Transactional
    public void incrementLikes(Long id, Long userId) {
        String key = getLikedPostsKey(userId);
        String postIdStr = String.valueOf(Objects.requireNonNull(id));
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, postIdStr);
        if (Boolean.FALSE.equals(isLiked)) {
            redisTemplate.opsForSet().add(key, postIdStr);
            redisTemplate.expire(key, 7 * 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
            postMapper.incrementLikeCount(id);
        }
    }

    @Override
    @Transactional
    public void decrementLikes(Long id, Long userId) {
        String key = getLikedPostsKey(userId);
        String postIdStr = String.valueOf(Objects.requireNonNull(id));
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, postIdStr);
        if (Boolean.TRUE.equals(isLiked)) {
            redisTemplate.opsForSet().remove(key, postIdStr);
            redisTemplate.expire(key, 7 * 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
            postMapper.decrementLikeCount(id);
        }
    }
}
