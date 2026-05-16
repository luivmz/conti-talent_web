package com.conti_talent.springboot.appweb.conti_talent_web.dto.response;

/**
 * Wrapper genérico para respuestas REST. Mantiene el contrato { ok, data, error }
 * que ya usa el frontend en su capa Auth (auth.js -> { ok, error, user }).
 */
public class ApiResponse<T> {

    private boolean ok;
    private T data;
    private String error;

    public ApiResponse() {
    }

    public ApiResponse(boolean ok, T data, String error) {
        this.ok = ok;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
