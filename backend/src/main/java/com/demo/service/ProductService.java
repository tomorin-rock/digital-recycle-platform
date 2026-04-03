package com.demo.service;

import com.demo.entity.Product;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 商品（数码产品型号）服务接口
 * 管理系统中可回收或可换新的商品型号信息
 */
public interface ProductService {
    /**
     * 管理端：分页查询商品型号
     * @param modelName 型号名称模糊匹配
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页商品列表
     */
    PageInfo<Product> selectByAdmin(String modelName, Integer pageNum, Integer pageSize);

    /**
     * 用户端：根据型号名称查询可选商品
     * @param model 型号名称关键字
     * @return 商品列表
     */
    List<Product> selectByUser(String model);

    /**
     * 根据分类ID查询商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> selectByCategoryId(Long categoryId);

    /**
     * 根据品牌ID查询商品列表
     * @param brandId 品牌ID
     * @return 商品列表
     */
    List<Product> selectByBrandId(Long brandId);

    /**
     * 更新商品型号信息
     * @param product 商品实体
     * @return 受影响行数
     */
    int update(Product product);

    /**
     * 更新商品状态（上架/下架）
     * @param id 商品ID
     * @param status 状态值
     * @return 受影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 新增商品型号
     * @param product 商品实体
     * @return 受影响行数
     */
    int insert(Product product);

    /**
     * 根据ID获取商品详情
     * @param productId 商品ID
     * @return 商品实体
     */
    Product selectById(Long productId);

    /**
     * 删除指定商品
     * @param productId 商品ID
     * @return 影响行数
     */
    int delete(Long productId);

    /**
     * 获取系统中已录入的商品型号总数
     * @return 商品总数
     */
    Long getProductCount();
}
