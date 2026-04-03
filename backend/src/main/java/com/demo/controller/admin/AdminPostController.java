package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.ForumPost;
import com.demo.service.PostService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-论坛帖子管理控制器
 */
@RestController
@RequestMapping("api/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 条件分页查询论坛帖子
     * @param post 查询条件实体
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 帖子列表分页数据
     */
    @GetMapping
    public Result selectAll(
            @RequestParam(required = false) ForumPost post,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.selectAll(post, pageNum, pageSize));
    }

    /**
     * 强制删除帖子（管理员权限）
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        int res = postService.delete(id);
        if (res == 1) {
            return Result.success("删除帖子成功");
        }
        return Result.notFoundId(res);
    }

}
