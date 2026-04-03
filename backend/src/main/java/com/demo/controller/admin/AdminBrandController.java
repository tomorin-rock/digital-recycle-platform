package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.ProductBrand;
import com.demo.service.BrandService;
import com.demo.service.FileStorageService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 管理员-产品品牌管理控制器
 */
@RestController
@RequestMapping("/api/admin/brands")
@Slf4j
public class AdminBrandController {

    private final BrandService pBrandService;
    private final FileStorageService fileStorageService;

    public AdminBrandController(BrandService pBrandService, FileStorageService fileStorageService) {
        this.pBrandService = pBrandService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 分页查询所有品牌
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 品牌列表分页数据
     */
    @GetMapping
    public Result selectAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ProductBrand> brands = pBrandService.selectAll(pageNum, pageSize);
        return Result.success(brands);
    }

    /**
     * 添加品牌
     * @param productBrand 品牌信息
     * @return 操作结果
     */
    @PostMapping
    public Result insert(@RequestBody ProductBrand productBrand) {
        // 验证必要字段
        if (productBrand.getName() == null || productBrand.getName().trim().isEmpty()) {
            return Result.fail(1005, "品牌名称不能为空");
        }

        int res = pBrandService.insert(productBrand);
        if (res == 1) {
            return Result.success("添加品牌成功", productBrand);
        }
        return Result.failToAdd();
    }

    /**
     * 修改品牌信息
     * @param id 品牌ID
     * @param productBrand 修改后的品牌信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ProductBrand productBrand) {
        if (!id.equals(productBrand.getId())) {
            return Result.fail(2000, "请求路径 ID 与请求体 ID 不一致");
        }

        // 验证品牌是否存在
        ProductBrand existingBrand = pBrandService.selectById(id);
        if (existingBrand == null) {
            return Result.notFoundId(id);
        }

        // 如果请求体中的 logo 为空，则保持原有 logo 不变
        if (productBrand.getLogo() == null || productBrand.getLogo().isEmpty()) {
            productBrand.setLogo(existingBrand.getLogo());
        }

        int res = pBrandService.update(productBrand);
        if (res == 1) {
            return Result.success("修改品牌成功", productBrand);
        }
        return Result.notFoundId(res);
    }


    /**
     * 上传品牌 Logo
     * @param id 品牌ID
     * @param file Logo图片文件
     * @return 上传后的品牌信息
     */
    @PostMapping("/{id}/logo")
    public Result uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // 验证品牌是否存在
            ProductBrand productBrand = pBrandService.selectById(id);
            if (productBrand == null) {
                return Result.fail(2001, "品牌不存在");
            }

            // 验证文件类型和大小
            if (!fileStorageService.validateFileType(file)) {
                return Result.fail(4001, "不支持的文件类型，仅支持图片格式");
            }

            if (!fileStorageService.validateFileSize(file)) {
                return Result.fail(4002, "文件大小超出限制");
            }

            // 保存文件并更新品牌 Logo URL
            String logoUrl = fileStorageService.saveFile(file, "brand");
            productBrand.setLogo(logoUrl);
            pBrandService.update(productBrand);

            return Result.success("品牌Logo上传成功", productBrand);
        } catch (IOException e) {
            log.error("品牌Logo上传失败，品牌ID: {}", id, e);
            return Result.fail(4000, "图片保存失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        int res = pBrandService.delete(id);
        if (res == 1) {
            return Result.success("删除品牌成功", "第"+id+"号");
        }
        return Result.notFoundId(res);
    }
}
