package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.ForumComment;
import com.demo.service.CommentService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-评论管理控制器
 */
@RestController
@RequestMapping("api/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 条件分页查询所有评论
     * @param comment 查询条件实体
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评论列表分页数据
     */
    @GetMapping
    public Result selectAll(
            @RequestParam(required = false) ForumComment comment,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(commentService.selectAll(comment, pageNum, pageSize));
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 当前管理员ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, @CurrentUser Long userId) {
        return Result.success(commentService.delete(id, userId));
    }
}
