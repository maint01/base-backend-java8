package vn.com.lifesup.hackathon.exception;

import vn.com.lifesup.hackathon.dto.response.ApiResponse;
import vn.com.lifesup.hackathon.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String ROOT = "gbtd";

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleError(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ApiResponse<Object> res = ApiResponse.error(ex.getMessage());

        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Object> handleError(BusinessException ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ApiResponse<Object> res = ApiResponse.error(ex.getMessage());

        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.OK, request);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthentication(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ApiResponse<Object> res = ApiResponse.error(ex.getMessage());

        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDeniedError(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ApiResponse<Object> res = ApiResponse.error(ex.getMessage());

        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    protected ResponseEntity<Object> handleAuthorizedError(MissingRequestHeaderException ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        return handleExceptionInternal(ex, ApiResponse.error("Access denied"),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

//    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (isApiLog(request)) {
            return new ResponseEntity<>(ApiResponse.error(errors), headers, status);

        }

        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), headers, status);
    }

//    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        if (isApiLog(request)) {
            return new ResponseEntity<>(ApiResponse.error("createDate or userIp malformed"), headers, status);
        }

        return new ResponseEntity<>(ApiResponse.error(ErrorCode.JSON_WRONG_FORMAT), headers, status);
    }

    private boolean isApiLog(WebRequest rq) {
        String url = ((ServletWebRequest) rq).getRequest().getRequestURI();
        if (url.contains(ROOT)) {
            url = url.replace(ROOT, "").replace("//", "/");
        }
        return "/api/logAction".equals(url);
    }
}
