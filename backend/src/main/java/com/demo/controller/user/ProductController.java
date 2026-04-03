package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.entity.Product;
import com.demo.entity.ProductCategory;
import com.demo.service.BrandService;
import com.demo.service.CategoryService;
import com.demo.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户端-商品浏览与分类搜索控制器
 */
@RestController
@RequestMapping("/api/user/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductController(ProductService productService, 
                             CategoryService categoryService, 
                             BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    /**
     * 条件查询商品列表
     * @param modelName 型号模糊查询
     * @return 商品列表
     */
    @GetMapping
    public Result selectAll(@RequestParam(required = false) String modelName) {
        return Result.success(productService.selectByUser(modelName));
    }

    /**
     * 获取所有可用品牌列表
     * @return 品牌列表
     */
    @GetMapping("brands")
    public Result selectAllBrand() {
        return Result.success(brandService.brandList());
    }

    /**
     * 获取所有商品分类列表
     * @return 分类列表
     */
    @GetMapping("categories")
    public Result selectAllCategory() {
        return Result.success(categoryService.categoryList());
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情实体
     */
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id) {
        return Result.success(productService.selectById(id));
    }

    /**
     * 根据品牌筛选商品
     * @param brandId 品牌ID
     * @return 商品列表
     */
    @GetMapping("/brand/{brandId}")
    public Result selectByBrandId(@PathVariable Long brandId) {
        return Result.success(productService.selectByBrandId(brandId));
    }

    /**
     * 根据父分类筛选子分类及对应的商品
     * @param parentId 父分类ID
     * @param categoryId 可选：指定选中的子分类ID（默认为第一个）
     * @return 包含子分类列表和当前分类下商品列表的 Map
     */
    @GetMapping("/parent/{parentId}")
    public Result selectByParentId(@PathVariable Long parentId, @RequestParam(required = false) Long categoryId) {
        try {
            // 1. 查询该父分类下的所有二级分类
            List<ProductCategory> subCategories = categoryService.selectAllUser(parentId);
            if (subCategories == null || subCategories.isEmpty()) {
                return Result.fail(1004, "该父分类下没有子分类");
            }

            // 2. 确定当前选中的分类（若未传则选第一个）
            if (categoryId == null) {
                categoryId = subCategories.getFirst().getId();
            }

            // 3. 查询选中分类下的所有商品
            List<Product> products = productService.selectByCategoryId(categoryId);

            // 4. 返回复合数据
            return Result.success(Map.of(
                    "categories", subCategories,
                    "products", products
            ));
        } catch (Exception e) {
            return Result.fail(1003, "获取分类和商品列表失败");
        }
    }
}
