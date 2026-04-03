package com.demo.service;

import com.demo.entity.SysUser;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;

public interface UserService {
    /**
     * 用户登录
     * @param loginId 登录账号 (用户名或手机号)
     * @param password 登录密码
     * @return 登录成功的用户对象, 失败则返回 null
     */
    SysUser login(String loginId, String password);

    /**
     * 用户注册
     * @param user 用户信息实体
     * @param confirmPwd 确认密码
     * @return 状态码: -1 用户已存在, 0 密码不一致, 1 注册成功
     */
    int register(SysUser user, String confirmPwd);

    /**
     * 注销用户 (软删除)
     * @param id 用户ID
     * @return 受影响行数
     */
    int delete(Long id);

    /**
     * 分页查询所有用户
     * @param user 过滤条件实体
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageInfo<SysUser> selectAll(SysUser user, Integer pageNum, Integer pageSize);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 新状态
     * @return 受影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 修改用户密码
     * @param id 用户ID
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param confirmPwd 确认新密码
     * @return 状态码: -1 用户不存在, -2 旧密码错误, -3 新旧密码相同, -4 确认密码不匹配, 1 成功
     */
    int updatePassword(Long id, String oldPwd, String newPwd, String confirmPwd);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param nickname 昵称
     * @param amount 账户余额
     */
    void updateInfo(Long id, String nickname, BigDecimal amount);

    /**
     * 更新用户头像
     * @param id 用户ID
     * @param avatar 头像路径
     */
    void updateAvatar(Long id, String avatar);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    SysUser selectById(Long id);

    /**
     * 获取总用户数
     * @return 用户总数
     */
    Long getUserCount();
}
