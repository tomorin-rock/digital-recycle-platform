package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.Product;
import com.demo.service.FileStorageService;
import com.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员-商品信息管理控制器
 */
@RestController
@RequestMapping("api/admin/products")
@Slf4j
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public AdminProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 条件分页查询商品列表
     * @param modelName 型号名称关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商品列表分页数据
     */
    @GetMapping
    public Result selectAll(
            @RequestParam(required = false) String modelName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.selectByAdmin(modelName, pageNum, pageSize));
    }

    /**
     * 新增商品信息
     * @param product 商品实体
     * @return 操作结果
     */
    @PostMapping
    public Result insert(@RequestBody Product product) {
        int res = productService.insert(product);
        if (res == 1) {
            return Result.success("添加商品成功");
        }
        return Result.failToAdd();
    }

    /**
     * 更新商品状态（上架/下架）
     * @param id 商品ID
     * @param status 状态值 (1:上架, 0:下架)
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return status == 1 ? Result.success("商品已上架") : Result.success("商品已下架");
    }

    /**
     * 修改商品详细信息
     * @param id 商品ID
     * @param product 修改后的商品信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Product product) {
        if (!id.equals(product.getId())) {
            return Result.fail(2000, "请求路径 ID 与请求体 ID 不一致");
        }
        int res = productService.update(product);
        if (res == 1) {
            return Result.success("修改商品成功");
        }
        return Result.notFoundId(res);
    }

    /**
     * 上传商品展示图片
     * @param id 商品ID
     * @param file 图片文件
     * @return 包含图片URL的成功响应
     */
    @PostMapping("/{id}/image")
    public Result uploadImage(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file) {
        try {
            // 验证商品是否存在
            Product product = productService.selectById(id);
            if (product == null) {
                return Result.fail(2001, "商品不存在");
            }

            // 保存文件并更新商品主图
            String imageUrl = fileStorageService.saveFile(file, "product");
            product.setImgUrl(imageUrl);
            productService.update(product);

            Map<String, Object> response = new HashMap<>();
            response.put("productId", id);
            response.put("imageUrl", imageUrl);
            response.put("message", "商品图片上传成功");

            return Result.success(response);
        } catch (IOException e) {
            log.error("商品图片上传失败，商品ID: {}", id, e);
            return Result.fail(4000, "图片保存失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(4001, e.getMessage());
        }

     }
}
