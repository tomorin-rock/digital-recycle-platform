package com.demo.service.impl;

import com.demo.entity.UserAddress;
import com.demo.mapper.AddressMapper;
import com.demo.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户地址服务实现类
 */
@Service
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<UserAddress> addressList(Long userId) {
        return addressMapper.getAddress(userId);
    }

    @Override
    @Transactional
    public int addAddress(UserAddress userAddress) {
       // 如果这是用户的第一条地址，自动设为默认
       List<UserAddress> existingAddresses = addressList(userAddress.getUserId());
       if (existingAddresses.isEmpty()) {
           userAddress.setIsDefault(1); // 设为默认地址
       } else {
           userAddress.setIsDefault(0); // 非默认地址
       }
       return addressMapper.addAddress(userAddress);
    }

    @Override
    @Transactional
    public int updateAddress(UserAddress userAddress) {
       return addressMapper.updateAddress(userAddress);
    }

    @Override
    @Transactional
    public int deleteAddress(Long id) {
       return addressMapper.deleteAddress(id);
    }

    @Override
    @Transactional
    public int setDefault(Long id, Long userId) {
        // 先检查地址是否存在且属于该用户
        List<UserAddress> addresses = addressList(userId);
        boolean addressExists = addresses.stream().anyMatch(addr -> addr.getId().equals(id));

        if (!addressExists) {
            return 0; // 地址不存在或不属于该用户
        }

        // 执行设置默认地址操作
        return addressMapper.setDefault(id, userId);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return addressMapper.getDefaultAddress(userId);
    }
}
