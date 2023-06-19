package vn.com.lifesup.hackathon.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.com.lifesup.hackathon.dto.response.ApiResponse;
import vn.com.lifesup.hackathon.util.Constants;
import vn.com.lifesup.hackathon.util.MessageUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        unauthorized(response);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        unauthorized(response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException, ServletException {
        ApiResponse<?> apiResponse = new ApiResponse<Object>();
        apiResponse.setCode(Constants.ERROR);
        apiResponse.setMessage(MessageUtil.getMessage("error.authorization.token-valid"));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream os = response.getOutputStream();
        objectMapper.writeValue(os, apiResponse);
        os.flush();
    }

}
