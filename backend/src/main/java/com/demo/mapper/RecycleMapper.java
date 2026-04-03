package com.demo.mapper;

import com.demo.entity.RecycleOrder;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RecycleMapper {
    List<RecycleOrder> selectAll();

    List<RecycleOrder> selectByUserId(Long userId);

    RecycleOrder selectById(Long id);

    int updateById(RecycleOrder order);

    int insert(RecycleOrder recycleOrder);

    int cancelOrder(Long id);

    // 获取订单数
    Long getCount();

    // 删除订单
    int delete(Long id);
}
