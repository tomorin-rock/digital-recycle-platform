package com.demo.utils;

import java.math.BigDecimal;

/**
 * 价格计算工具类
 * 提供根据产品成色等级计算预估价格的功能
 */
public class CalculateUtils {

    /**
     * 根据基准价格和成色等级计算预估价格
     *
     * @param basePrice 基准价格，不能为null
     * @param grade 成色等级（A/B/C/D），不能为null
     * @return 计算后的预估价格，如果输入参数为null则返回0
     */
    public static BigDecimal calculateEstimatePrice(BigDecimal basePrice, String grade) {
        // 参数校验，如果任一参数为null则返回零值
        if (basePrice == null || grade == null) {
            return BigDecimal.ZERO;
        }

        // 根据成色等级调整价格
        return switch (grade.toUpperCase()) {
            case "A" -> basePrice.multiply(new BigDecimal("0.9")); // 九成新
            case "B" -> basePrice.multiply(new BigDecimal("0.8")); // 八成新
            case "C" -> basePrice.multiply(new BigDecimal("0.6")); // 六成新
            case "D" -> basePrice.multiply(new BigDecimal("0.4")); // 四成新
            default -> basePrice.multiply(new BigDecimal("0.7")); // 默认七成新
        };
    }
}