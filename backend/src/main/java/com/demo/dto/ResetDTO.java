package com.demo.dto;

import com.demo.utils.passwordValid.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ResetDTO {
    //修改密码
    @NotBlank
    private String oldPwd;
    @ValidPassword
    private String newPwd;
    @NotBlank
    private String confirmPwd;


    //修改用户信息
    private String nickname;
    private BigDecimal amount;
    private MultipartFile avatar;
}
