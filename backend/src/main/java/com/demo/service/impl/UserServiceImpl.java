package com.demo.service.impl;

import com.demo.entity.SysUser;
import com.demo.mapper.UserMapper;
import com.demo.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser login(String loginId, String password) {
        SysUser user = userMapper.selectByLoginId(loginId);
        if (user == null) {
            return null;
        }
        // 校验密码（加密匹配）
        return passwordEncoder.matches(password, user.getPassword()) ? user : null;
    }

    @Override
    @Transactional
    public int register(SysUser user, String confirmPwd) {
        // 1. 校验用户是否存在
        SysUser existingUser = userMapper.selectByLoginId(user.getUsername());
        if (existingUser != null) {
            return -1; // 用户已存在
        }
        // 2. 校验密码是否一致
        String rawPassword = user.getPassword();
        if (!rawPassword.equals(confirmPwd)) {
            return 0; // 密码不一致
        }
        // 3. 密码加密存储
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userMapper.insert(user);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        // 软删除/注销用户
        return userMapper.cancel(id);
    }

    @Override
    public PageInfo<SysUser> selectAll(SysUser user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> userList = userMapper.selectAll(user);
        return new PageInfo<>(userList);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Integer status) {
        return userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public int updatePassword(Long id, String oldPwd, String newPwd, String confirmPwd) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return -1; // 用户不存在
        }
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return -2; // 旧密码不正确
        }
        if (newPwd.equals(oldPwd)) {
            return -3; // 新旧密码相同
        }
        if (!confirmPwd.equals(newPwd)) {
            return -4; // 确认密码不匹配
        }
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPwd);
        return userMapper.updatePassword(id, encodedPassword);
    }

    @Override
    @Transactional
    public void updateInfo(Long id, String nickname, BigDecimal amount) {
        userMapper.update(id, nickname, amount);
    }

    @Override
    @Transactional
    public void updateAvatar(Long id, String avatar) {
        userMapper.updateAvatar(id, avatar);
    }


    @Override
    public SysUser selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Long getUserCount() {
        return userMapper.getCount();
    }
}
