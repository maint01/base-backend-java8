package vn.com.lifesup.hackathon.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.com.lifesup.hackathon.util.ErrorCode;
import vn.com.lifesup.hackathon.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    private Integer totalRecords;

    private Object extraData;

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = MessageUtil.getMessage(message);
        this.data = data;
    }

    public ApiResponse(String code, String message, T data, Integer totalRecords) {
        this.code = code;
        this.message = MessageUtil.getMessage(message);
        this.data = data;
        this.totalRecords = totalRecords;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(ErrorCode.SUCCESS.getCode(),
                ErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<List<T>> success(List<T> data, Integer totalRecords) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(),
                ErrorCode.SUCCESS.getMessage(), data, totalRecords);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(ErrorCode.SUCCESS.getCode(),
                message, data);
    }

    public static <T> ApiResponse<T> error(@NonNull String message) {
        return new ApiResponse<>(ErrorCode.FAILED.getCode(), message, null);
    }

    public static <T> ApiResponse<T> serverError() {
        return error(ErrorCode.SERVER_ERROR);
    }

    public static <T> ApiResponse<T> invalid(@NonNull String message) {
        return new ApiResponse<>(ErrorCode.INVALIDATE.getCode(), message, null);
    }


    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), MessageUtil.getMessage(errorCode), null);
    }

    public static <T> ApiResponse<T> error(T data, ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), MessageUtil.getMessage(errorCode), data);
    }
}
