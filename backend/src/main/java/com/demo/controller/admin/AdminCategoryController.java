package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.ProductCategory;
import com.demo.service.CategoryService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-产品分类管理控制器
 */
@RestController
@RequestMapping("api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 分页查询所有分类
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分类列表分页数据
     */
    @GetMapping
    public Result selectAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(categoryService.selectAll(pageNum, pageSize));
    }

    /**
     * 添加产品分类
     * @param productCategory 分类实体信息
     * @return 操作结果
     */
    @PostMapping
    public Result insert(@RequestBody ProductCategory productCategory) {
        // 验证分类名称不能为空
        if (productCategory.getName() == null || productCategory.getName().trim().isEmpty()) {
            return Result.fail(1005, "分类名称不能为空");
        }
        int res = categoryService.insert(productCategory);
        if (res == 1) {
            return Result.success("添加分类成功");
        }
        return Result.failToAdd();
    }

    /**
     * 修改产品分类
     * @param id 分类ID
     * @param productCategory 修改后的分类信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ProductCategory productCategory) {
        if (!id.equals(productCategory.getId())) {
            return Result.fail(2000, "请求路径 ID 与请求体 ID 不一致");
        }
        int res = categoryService.update(productCategory);
        if (res == 1) {
            return Result.success("修改分类成功");
        }
        return Result.notFoundId(res);
    }

}
