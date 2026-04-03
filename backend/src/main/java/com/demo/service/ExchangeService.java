package com.demo.service;

import com.demo.entity.ExchangeOrder;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 以旧换新订单服务接口
 * 处理用户使用回收所得余额或优惠券购买新商品的业务流程
 */
public interface ExchangeService {
    /**
     * 创建换新订单
     * @param exchangeOrder 订单信息实体
     * @return 受影响行数
     */
    int insert(ExchangeOrder exchangeOrder);

    /**
     * 支付换新订单
     * @param orderNo 订单编号
     * @param userId 用户ID
     * @return 状态码或受影响行数
     */
    int payOrder(String orderNo, Long userId);

    /**
     * 分页查询所有换新订单
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页订单列表
     */
    PageInfo<ExchangeOrder> selectAll(int pageNum, int pageSize);

    /**
     * 查询指定用户的所有换新订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<ExchangeOrder> selectByUserId(Long userId);

    /**
     * 根据主键ID查询订单
     * @param id 订单ID
     * @return 换新订单实体
     */
    ExchangeOrder selectById(Long id);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @param userId  操作用户ID
     */
    void cancelOrder(String orderNo, Long userId);

    /**
     * 管理员发货
     * @param orderNo 订单编号
     * @param userId 管理员用户ID
     */
    void shipping(String orderNo, Long userId);

    /**
     * 用户确认收货
     * @param orderNo 订单编号
     * @param userId 用户ID
     */
    void confirmReceipt(String orderNo, Long userId);

    /**
     * 获取总换新订单数
     * @return 订单总数
     */
    Long getExchangeCount();

    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响行数
     */
    int delete(Long id);
}
