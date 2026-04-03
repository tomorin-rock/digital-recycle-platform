package com.demo.dto;
import com.demo.utils.passwordValid.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ValidPassword
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
}