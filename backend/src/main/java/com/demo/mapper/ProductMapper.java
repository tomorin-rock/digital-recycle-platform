package com.demo.mapper;

import com.demo.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//商品
@Mapper
public interface ProductMapper {
    //查询所有商品(管理员)
    List<Product> selectByAdmin(String modelName);

    //修改商品信息
    int update(Product product);

    //(下架/上架)商品
    int updateStatus(Long id, Integer status);

    //添加商品
    int insert(Product product);

    //查询所有商品(用户)
    List<Product> selectByUser(String modelName);

    //查询商品(分类)
    List<Product> selectByCategory(Long categoryId);

    //查询商品(品牌)
    List<Product> selectByBrand(Long brandId);

    //查看商品详情
    Product selectById(Long id);

    //商品库存+1
    void incrementStock(Long id);

    //商品库存-1
    void decrementStock(Long id);

    // 获取商品数
    Long getCount();

    // 删除商品
    int delete(Long id);
}
