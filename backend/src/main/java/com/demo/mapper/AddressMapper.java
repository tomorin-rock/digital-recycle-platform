package com.demo.mapper;

import com.demo.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressMapper {
    List<UserAddress> getAddress(Long userId);

    int addAddress(UserAddress userAddress);

    int updateAddress(UserAddress userAddress);

    int deleteAddress(Long Id);

    int setDefault(Long id, Long userId);

    UserAddress getDefaultAddress(Long userId);
}
