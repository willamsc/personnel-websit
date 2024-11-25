package com.willamsc.personalwebsite.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author william
 * @since 2024-11-25
 */
@Getter
public enum ErrorCode {

    // 系统错误
    SYSTEM_ERROR("500", "系统错误"),
    PARAM_ERROR("400", "参数错误"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "无权限"),
    NOT_FOUND("404", "资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法不允许"),
    REQUEST_TIMEOUT("408", "请求超时"),
    CONFLICT("409", "资源冲突"),
    TOO_MANY_REQUESTS("429", "请求过于频繁"),

    // 用户相关错误
    USER_NOT_FOUND("1001", "用户不存在"),
    USER_ALREADY_EXISTS("1002", "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR("1003", "用户名或密码错误"),
    PASSWORD_ERROR("1004", "密码错误"),
    OLD_PASSWORD_ERROR("1005", "原密码错误"),
    USER_LOCKED("1006", "用户已被锁定"),
    USER_DISABLED("1007", "用户已被禁用"),
    TOKEN_EXPIRED("1008", "令牌已过期"),
    TOKEN_INVALID("1009", "令牌无效"),
    LOGOUT_ERROR("1010", "退出登录失败"),

    // 文章相关错误
    ARTICLE_NOT_FOUND("2001", "文章不存在"),
    ARTICLE_ALREADY_EXISTS("2002", "文章已存在"),
    ARTICLE_TITLE_EXISTS("2003", "文章标题已存在"),
    ARTICLE_CONTENT_EMPTY("2004", "文章内容不能为空"),
    ARTICLE_STATUS_ERROR("2005", "文章状态错误"),
    ARTICLE_CATEGORY_NOT_FOUND("2006", "文章分类不存在"),
    ARTICLE_TAG_NOT_FOUND("2007", "文章标签不存在"),

    // 评论相关错误
    COMMENT_NOT_FOUND("3001", "评论不存在"),
    COMMENT_CONTENT_EMPTY("3002", "评论内容不能为空"),
    COMMENT_PARENT_NOT_FOUND("3003", "父评论不存在"),
    COMMENT_REPLY_USER_NOT_FOUND("3004", "回复用户不存在"),
    COMMENT_NOT_ALLOWED("3005", "不允许评论"),

    // 分类相关错误
    CATEGORY_NOT_FOUND("4001", "分类不存在"),
    CATEGORY_ALREADY_EXISTS("4002", "分类已存在"),
    CATEGORY_HAS_CHILDREN("4003", "分类下有子分类"),
    CATEGORY_HAS_ARTICLES("4004", "分类下有文章"),
    CATEGORY_PARENT_NOT_FOUND("4005", "父分类不存在"),
    CATEGORY_LEVEL_ERROR("4006", "分类层级错误"),

    // 标签相关错误
    TAG_NOT_FOUND("5001", "标签不存在"),
    TAG_ALREADY_EXISTS("5002", "标签已存在"),
    TAG_HAS_ARTICLES("5003", "标签下有文章"),

    // 文件相关错误
    FILE_UPLOAD_ERROR("6001", "文件上传失败"),
    FILE_DELETE_ERROR("6002", "文件删除失败"),
    FILE_NOT_FOUND("6003", "文件不存在"),
    FILE_TYPE_NOT_ALLOWED("6004", "文件类型不允许"),
    FILE_SIZE_EXCEEDED("6005", "文件大小超出限制"),

    // 搜索相关错误
    SEARCH_ERROR("7001", "搜索失败"),
    INDEX_ERROR("7002", "索引失败"),

    // 验证码相关错误
    CAPTCHA_ERROR("8001", "验证码错误"),
    CAPTCHA_EXPIRED("8002", "验证码已过期"),

    // 邮件相关错误
    EMAIL_SEND_ERROR("9001", "邮件发送失败"),
    EMAIL_ALREADY_EXISTS("9002", "邮箱已存在"),
    EMAIL_NOT_FOUND("9003", "邮箱不存在"),
    EMAIL_FORMAT_ERROR("9004", "邮箱格式错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
