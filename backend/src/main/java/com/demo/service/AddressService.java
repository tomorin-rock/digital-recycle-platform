package com.demo.service;

import com.demo.entity.UserAddress;

import java.util.List;

/**
 * 用户地址服务接口
 * 提供用户收货地址的管理功能，包括增删改查及设置默认地址
 */
public interface AddressService {
    /**
     * 获取指定用户的地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> addressList(Long userId);

    /**
     * 新增收货地址
     * @param userAddress 地址信息实体
     * @return 受影响行数
     */
    int addAddress(UserAddress userAddress);

    /**
     * 更新收货地址
     * @param userAddress 待更新的地址信息
     * @return 受影响行数
     */
    int updateAddress(UserAddress userAddress);

    /**
     * 删除收货地址
     * @param Id 地址ID
     * @return 受影响行数
     */
    int deleteAddress(Long Id);

    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    int setDefault(Long id, Long userId);

    /**
     * 获取用户的默认地址
     * @param userId 用户ID
     * @return 默认地址对象，若无则返回 null
     */
    UserAddress getDefaultAddress(Long userId);
}
