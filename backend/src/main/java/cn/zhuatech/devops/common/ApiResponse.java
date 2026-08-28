/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.common;
public record ApiResponse<T>(boolean success, T data, String message) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, data, "success"); }
    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(false, null, message); }
}
