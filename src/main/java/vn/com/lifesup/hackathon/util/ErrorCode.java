package vn.com.lifesup.hackathon.util;

public enum ErrorCode {
    SERVER_ERROR("E00", "error.server_error"),
    CLIENT_ERROR("E01", "error.client_error"),
    DISABLED("E02", "error.disabled"),
    ALREADY_EXIST("E04", "error.already_exist"),
    FAILED("E05", "error.failed"),
    MISSING_PARAMS("E06", "error.missing_params"),
    NOT_FOUND("E07", "error.not_found"),
    AUTHEN_ERROR("E10", "error.auth_error"),
    ACCESS_DENIED("E11", "error.access_denied"),
    JSON_WRONG_FORMAT("E14", "error.json_wrong_format"),
    NOT_VALID("E15", "error.not_valid"),

    SUCCESS("00", "data.success"),
    INVALIDATE("001", "Validate error"),
//    BaseRes

    NOT_NULL("E16","error.not_null" ), DATE_FORMAT("E17","error.date_fomat" );

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
