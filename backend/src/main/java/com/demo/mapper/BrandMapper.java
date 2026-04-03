package com.demo.mapper;

import com.demo.entity.ProductBrand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//品牌
@Mapper
public interface BrandMapper {
    //查询所有品牌
    List<ProductBrand> selectAll();
    //品牌列表
    List<ProductBrand> brandList();
    //修改品牌信息
    int update(ProductBrand productBrand);
    //删除品牌信息
    int delete(Long id);
    //增加品牌
    int insert(ProductBrand productBrand);
    //查询品牌信息
    ProductBrand selectById(Long id);
}
