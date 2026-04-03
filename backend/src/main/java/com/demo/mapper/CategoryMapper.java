package com.demo.mapper;
import com.demo.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface CategoryMapper {
    //查询所有分类(管理员)
    List<ProductCategory> selectAll();
    //查询所有分类(用户)
    List<ProductCategory> selectAllUser(Long parentId);
    //分类列表
    List<ProductCategory> categoryList();
    //修改分类信息
    int update(ProductCategory productCategory);
    //增加分类
    int insert(ProductCategory productCategory);
    //删除分类
    int delete(Long id);
}
