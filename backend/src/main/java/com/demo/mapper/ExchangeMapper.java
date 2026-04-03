package com.demo.mapper;

import com.demo.entity.ExchangeOrder;
import com.demo.enums.ExchangeOrderStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExchangeMapper {
    // 创建订单
    int insert(ExchangeOrder exchangeOrder);

    // 根据订单号查询订单
    ExchangeOrder selectByOrderNo(String orderNo);

    // 根据id查询订单
    ExchangeOrder selectById(Long id);

    // 查询所有订单
    List<ExchangeOrder> selectAll();

    // 根据用户id查询订单
    List<ExchangeOrder> selectByUserId(Long userId);

    // 修改订单状态
    int updateStatus(String orderNo, ExchangeOrderStatus status);

    // 获取订单数
    Long getCount();

    // 删除订单
    int delete(Long id);

}
