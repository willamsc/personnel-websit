package com.willamsc.personalwebsite.common;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 
 * @param <T> 响应数据类型
 * @author william
 * @since 2024-02-20
 */
@Data
public class Result<T> {
    /**
     * 响应状态码
     * 200: 成功
     * 500: 服务器错误
     * 其他: 特定业务错误码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造函数，防止直接实例化
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "Success", null);
    }

    /**
     * 创建成功响应（带数据）
     * 
     * @param data 响应数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "Success", data);
    }

    /**
     * 创建成功响应（带消息和数据）
     * 
     * @param message 成功消息
     * @param data 响应数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 创建错误响应（带消息）
     * 
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 创建错误响应（带状态码和消息）
     * 
     * @param code 错误状态码
     * @param message 错误消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
