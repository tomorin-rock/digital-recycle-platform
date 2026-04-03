package com.demo.entity;

import com.demo.enums.UserRole;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class SysUser {
    private Long id;  //主键
    private String username;  //用户名
    private String password;  //密码
    private String nickname;  //昵称
    private String avatar;  //头像
    private UserRole role;  //用户角色
    private Integer  status; //1:正常 0:禁用
    private BigDecimal balance = BigDecimal.ZERO; //余额
    private LocalDateTime createTime;  //创建时间
    private LocalDateTime updateTime;  //更新时间
}
