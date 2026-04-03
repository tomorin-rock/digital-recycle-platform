package com.demo.utils.passwordValid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;


public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private Pattern pattern;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        int minLength = constraintAnnotation.minLength();
        String specialChars = constraintAnnotation.specialChars();
        String regex = String.format(
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[%s]).{%d,}$",
                Pattern.quote(specialChars), minLength
        );
        this.pattern = Pattern.compile(regex);
    }

    /**
     * 核心校验逻辑
     * @param password 待校验的密码
     * @param context 校验上下文
     * @return true=符合规则，false=不符合
     */

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // 空值处理：若密码为空，交由 @NotBlank 注解校验，此处返回true（避免重复提示）
        if (password == null || password.isEmpty()) {
            return true;
        }
        // 匹配正则规则
        return pattern.matcher(password).matches();
    }
}
