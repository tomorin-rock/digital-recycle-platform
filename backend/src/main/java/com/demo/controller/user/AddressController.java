package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.entity.UserAddress;
import com.demo.service.AddressService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端-个人收货地址管理控制器
 */
@RestController
@RequestMapping("api/user/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 获取当前登录用户的所有收货地址
     * @param userId 当前用户ID
     * @return 地址列表
     */
    @GetMapping
    public Result getAddressList(@CurrentUser Long userId) {
        return Result.success(addressService.addressList(userId));
    }

    /**
     * 新增收货地址
     * @param address 地址实体
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PostMapping
    public Result addAddress(@RequestBody UserAddress address, @CurrentUser Long userId) {
        address.setUserId(userId);
        int res = addressService.addAddress(address);
        if (res == 1) {
            return Result.success("地址添加成功");
        }
        return Result.failToAdd();
    }

    /**
     * 修改现有收货地址
     * @param id 地址ID
     * @param address 修改后的地址实体
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        if (!id.equals(address.getId())) {
            return Result.fail(2000, "请求路径 ID 与请求体 ID 不一致");
        }
        int res = addressService.updateAddress(address);
        if (res == 1) {
            return Result.success("地址修改成功");
        }
        return Result.notFoundId(res);
    }

    /**
     * 删除收货地址
     * @param id 地址ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delAddress(@PathVariable Long id) {
        int res = addressService.deleteAddress(id);
        if (res == 1) {
            return Result.success("地址删除成功");
        }
        return Result.notFoundId(res);
    }

    /**
     * 设置默认收货地址
     * @param id 地址ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/default/{id}")
    public Result setDefault(@PathVariable Long id, @CurrentUser Long userId) {
        int result = addressService.setDefault(id, userId);
        if (result > 0) {
            return Result.success("默认地址设置成功");
        } else {
            return Result.fail(2005, "设置默认地址失败，地址不存在或不属于当前用户");
        }
    }

    /**
     * 获取当前用户的默认收货地址
     * @param userId 当前用户ID
     * @return 默认地址实体或空消息
     */
    @GetMapping("/default")
    public Result getDefaultAddress(@CurrentUser Long userId) {
        UserAddress defaultAddress = addressService.getDefaultAddress(userId);
        if (defaultAddress != null) {
            return Result.success(defaultAddress);
        } else {
            return Result.success("暂无默认地址");
        }
    }
}
