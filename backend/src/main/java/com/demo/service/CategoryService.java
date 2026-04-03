package com.demo.service;

import com.demo.entity.ProductCategory;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 商品分类服务接口
 * 提供商品类目的管理功能，支持多级分类查询与维护
 */
public interface CategoryService {
    /**
     * 分页查询所有分类
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的分类信息
     */
    PageInfo<ProductCategory> selectAll(Integer pageNum, Integer pageSize);

    /**
     * 获取分类列表（通常用于下拉框选择）
     * @return 分类列表
     */
    List<ProductCategory> categoryList();

    /**
     * 根据父ID查询子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<ProductCategory> selectAllUser(Long parentId);

    /**
     * 更新分类信息
     * @param productCategory 分类实体
     * @return 受影响行数
     */
    int update(ProductCategory productCategory);

    /**
     * 新增分类
     * @param productCategory 分类实体
     * @return 受影响行数
     */
    int insert(ProductCategory productCategory);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 受影响行数
     */
    int delete(Long id);
}
