package com.demo.service.impl;

import com.demo.entity.Product;
import com.demo.mapper.ProductMapper;
import com.demo.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageInfo<Product> selectByAdmin(String modelName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByAdmin(modelName);
        return new PageInfo<>(productList);
    }

    @Override
    public List<Product> selectByUser(String modelName) {
        return productMapper.selectByUser(modelName);
    }

    @Override
    public List<Product> selectByCategoryId(Long categoryId) {
        return productMapper.selectByCategory(categoryId);
    }

    @Override
    public List<Product> selectByBrandId(Long brandId) {
        return productMapper.selectByBrand(brandId);
    }

    @Override
    public Product selectById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public int delete(Long productId) {
        return productMapper.delete(productId);
    }

    @Override
    @Transactional
    public int update(Product product) {
        return productMapper.update(product);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Integer status) {
        return productMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public int insert(Product product) {
        return productMapper.insert(product);
    }

    @Override
    public Long getProductCount() {
        return productMapper.getCount();
    }
}
