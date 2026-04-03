package com.demo.utils.passwordValid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Constraint(validatedBy = PasswordValidator.class) // 指定校验器
@Target({ElementType.FIELD, ElementType.PARAMETER}) // 可用于字段/参数
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    //默认错误提示
    String message() default "密码强度不足：需至少8位，包含大小写字母、数字和特殊字符(!@#$%^&*)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    //密码最小长度
    int minLength() default 8;

    //允许使用的特殊字符
    String specialChars() default "!@#$%^&*";
}
