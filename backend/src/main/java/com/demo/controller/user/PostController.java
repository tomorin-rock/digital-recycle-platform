package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.entity.ForumPost;
import com.demo.service.PostService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端-论坛帖子发布与互动控制器
 */
@RestController
@RequestMapping("/api/user/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 用户发布新帖子
     * @param post 帖子实体信息
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PostMapping
    public Result insert(@RequestBody ForumPost post, @CurrentUser Long userId) {
        post.setUserId(userId);
        return Result.success(postService.insert(post));
    }

    /**
     * 查询所有公开帖子列表
     * @param post 查询条件实体
     * @return 帖子列表（包含发布人昵称）
     */
    @GetMapping
    public Result selectAll(@RequestParam(required = false) ForumPost post) {
        return Result.success(postService.selectAllName(post));
    }

    /**
     * 查看帖子详情（增加阅读量）
     * @param id 帖子ID
     * @param userId 当前用户ID（用于记录防刷阅读量）
     * @return 帖子详细信息
     */
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id, @CurrentUser Long userId) {
        // 增加浏览量记录
        postService.incrementViews(id, userId);
        ForumPost post = postService.selectById(id);
        if (post != null) {
            return Result.success(post);
        }
        return Result.fail(4000, "帖子不存在");
    }

    /**
     * 对帖子点赞
     * @param id 帖子ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/like")
    public Result incrementLikes(@PathVariable Long id, @CurrentUser Long userId) {
        postService.incrementLikes(id, userId);
        return Result.success("点赞成功");
    }

    /**
     * 取消对帖子的点赞
     * @param id 帖子ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/dislike")
    public Result decrementLikes(@PathVariable Long id, @CurrentUser Long userId) {
        postService.decrementLikes(id, userId);
        return Result.success("取消点赞成功");
    }

    /**
     * 用户删除自己的帖子
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.success(postService.delete(id));
    }
}
