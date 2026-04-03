package com.demo.mapper;

import com.demo.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

//用户
@Mapper
public interface UserMapper{
    //添加
    int insert (SysUser user);
    //删除
    int cancel (Long id);
    //更新
    int update (Long id, String nickname, BigDecimal amount);
    //更换头像
    int updateAvatar (Long id, String avatar);
    //启用，禁用用户
    int updateStatus (Long id, Integer status);
    //查询(根据username)
    SysUser selectByLoginId(String loginId);
    //查询全部
    List<SysUser> selectAll(SysUser user);
    //修改密码
    int updatePassword(Long id, String password);
    //查询(根据id)
    SysUser selectById(Long id);
    // 增加余额
    int addBalance(Long id, BigDecimal amount);
    // 减少余额
    int reduceBalance(Long id, BigDecimal amount);
    // 获取用户数
    Long getCount();
}
