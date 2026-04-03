package com.demo.service.impl;

import com.demo.entity.ProductCategory;
import com.demo.mapper.CategoryMapper;
import com.demo.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类服务实现类
 */
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageInfo<ProductCategory> selectAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductCategory> categoryList = categoryMapper.selectAll();
        return new PageInfo<>(categoryList);
    }

    @Override
    public List<ProductCategory> categoryList() {
        return categoryMapper.categoryList();
    }

    @Override
    public List<ProductCategory> selectAllUser(Long parentId) {
        return categoryMapper.selectAllUser(parentId);
    }

    @Override
    @Transactional
    public int update(ProductCategory productCategory) {
        return categoryMapper.update(productCategory);
    }

    @Override
    @Transactional
    public int insert(ProductCategory productCategory) {
        return categoryMapper.insert(productCategory);
    }

    @Override
    public int delete(Long id) {
        return categoryMapper.delete(id);
    }
}
