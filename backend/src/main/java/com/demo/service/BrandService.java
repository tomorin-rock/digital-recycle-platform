package com.demo.service;

import com.demo.entity.ProductBrand;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 品牌管理服务接口
 * 提供商品品牌的维护功能，包括品牌的增删改查
 */
public interface BrandService {
    /**
     * 更新品牌信息
     * @param productBrand 品牌实体
     * @return 受影响行数
     */
    int update(ProductBrand productBrand);

    /**
     * 删除指定品牌
     * @param id 品牌ID
     * @return 受影响行数
     */
    int delete(Long id);

    /**
     * 新增品牌
     * @param productBrand 品牌实体
     * @return 受影响行数
     */
    int insert(ProductBrand productBrand);

    /**
     * 分页查询所有品牌
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的品牌列表信息
     */
    PageInfo<ProductBrand> selectAll(Integer pageNum, Integer pageSize);

    /**
     * 获取所有品牌列表（不分页）
     * @return 品牌列表
     */
    List<ProductBrand> brandList();

    /**
     * 根据ID查询品牌详情
     * @param id 品牌ID
     * @return 品牌实体对象
     */
    ProductBrand selectById(Long id);
}
