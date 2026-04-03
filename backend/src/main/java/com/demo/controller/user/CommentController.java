package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.entity.ForumComment;
import com.demo.entity.ForumPost;
import com.demo.service.CommentService;
import com.demo.service.PostService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端-论坛评论互动控制器
 */
@RestController
@RequestMapping("/api/user/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    /**
     * 查询指定帖子下的所有评论
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/{postId}")
    public Result selectByPostId(@PathVariable Long postId) {
        ForumPost post = postService.selectById(postId);
        if (post == null) {
            return Result.fail(4000, "帖子不存在");
        }
        return Result.success(commentService.selectByPostId(postId));
    }

    /**
     * 对帖子发表评论
     * @param comment 评论内容实体
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PostMapping
    public Result insert(@RequestBody ForumComment comment, @CurrentUser Long userId) {
        comment.setUserId(userId);
        commentService.insert(comment);
        return Result.success("评论成功");
    }

    /**
     * 对指定评论进行点赞
     * @param id 评论ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/like")
    public Result incrementLikes(@PathVariable Long id, @CurrentUser Long userId) {
        commentService.incrementLikes(id, userId);
        return Result.success("点赞成功");
    }

    /**
     * 取消对评论的点赞
     * @param id 评论ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/dislike")
    public Result decrementLikes(@PathVariable Long id, @CurrentUser Long userId) {
        commentService.decrementLikes(id, userId);
        return Result.success("取消点赞成功");
    }

    /**
     * 删除自己的评论
     * @param id 评论ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, @CurrentUser Long userId) {
        commentService.delete(id, userId);
        return Result.success("删除成功");
    }
}
