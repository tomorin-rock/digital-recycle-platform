package com.demo.service.impl;

import com.demo.entity.ProductBrand;
import com.demo.mapper.BrandMapper;
import com.demo.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌管理服务实现类
 */
@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional
    public int update(ProductBrand productBrand) {
       return brandMapper.update(productBrand);
    }

    @Override
    @Transactional
    public int delete(Long id) {
       return brandMapper.delete(id);
    }

    @Override
    @Transactional
    public int insert(ProductBrand productBrand) {
       return brandMapper.insert(productBrand);
    }

    @Override
    public PageInfo<ProductBrand> selectAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductBrand> brandList = brandMapper.selectAll();
        return new PageInfo<>(brandList);
    }

    @Override
    public List<ProductBrand> brandList() {
        return brandMapper.brandList();
    }

    @Override
    public ProductBrand selectById(Long id) {
        return brandMapper.selectById(id);
    }
}
