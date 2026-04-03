package com.demo.common;

import lombok.Data;
import lombok.NoArgsConstructor; // 新增

/**
 * 统一返回结果封装类
 */
@Data
@NoArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    // ===== 成功 =====

    /** 返回成功，无数据 */
    public static Result success() {
        return success("成功", null);
    }

    /** 返回成功，带消息 */
    public static Result success(String msg) {
        return success(msg, null);
    }

    /** 返回成功，带数据（使用默认消息） */
    public static Result success(Object data) {
        return success("成功", data);
    }

    /** 返回成功，带消息和数据 */
    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // ===== 失败 =====

    public static Result fail(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result notFoundId(Object data) {
        Result result = new Result();
        result.setCode(1003);
        result.setMsg("id不存在！");
        result.setData(data);
        return result;
    }

    public static Result failToAdd() {
        Result result = new Result();
        result.setCode(1004);
        result.setMsg("添加失败");
        return result;
    }
}