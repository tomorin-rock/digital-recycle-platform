package com.demo.service;
import com.demo.entity.RecycleOrder;
import com.demo.enums.RecycleOrderStatus;
import com.demo.enums.RecyclePayType;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 回收订单服务接口
 * 处理数码产品回收的核心流程，包括订单创建、质检、价格确认及打款
 */
public interface RecycleService {
    /**
     * 分页查询所有回收订单
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的回收订单列表
     */
    PageInfo<RecycleOrder> selectAll(int pageNum, int pageSize);

    /**
     * 查询指定用户的所有回收订单
     * @param userId 用户ID
     * @return 回收订单列表
     */
    List<RecycleOrder> selectByUserId(Long userId);

    /**
     * 根据主键ID查询回收订单详情
     * @param id 订单ID
     * @return 订单实体
     */
    RecycleOrder selectById(Long id);

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新的目标状态
     * @return 受影响行数
     */
    int updateStatus(Long id, RecycleOrderStatus status);

    /**
     * 提交新的回收订单
     * @param recycleOrder 订单信息实体
     * @return 受影响行数
     */
    int insert(RecycleOrder recycleOrder);

    /**
     * 用户确认最终质检报价
     * @param id 订单ID
     * @param userId 用户ID
     * @param payType 支付方式（余额/优惠券）
     */
    void confirmFinalPrice(Long id, Long userId, RecyclePayType payType);

    /**
     * 用户取消回收订单
     * @param id 订单ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    int cancelOrder(Long id, Long userId);

    /**
     * 管理员开始质检
     * @param id 订单ID
     * @param userId 管理员ID
     */
    void startInspection(Long id, Long userId);

    /**
     * 管理员完成质检并录入最终价格
     * @param id 订单ID
     * @param userId 管理员ID
     * @param finalPrice 最终评估价格
     * @param remark 质检备注/说明
     */
    void finishInspection(Long id, Long userId, BigDecimal finalPrice, String remark);

    /**
     * 删除指定订单
     * @param id 订单id
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 获取系统中已提交的回收订单总数
     * @return 订单总数
     */
    Long getRecycleCount();


}
