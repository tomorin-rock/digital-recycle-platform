package com.demo.mapper;

import com.demo.entity.OperationLog;
import com.demo.enums.OperatorRole;
import com.demo.enums.OrderType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderOperationMapper {
    int insert(OperationLog log);

    // 查询所有操作日志
    List<OperationLog> selectAll(OrderType orderType, OperatorRole operateRole);
}
