package vn.com.lifesup.hackathon.security.jwt;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtPayload {
    private String clientId;
    private String firstName;
    private String lastName;
    private String email;
    private String emailVerified;
    private String address;
    private String identification;
    private String sessionId;
    private Long userId;
    private String username;
    private String scope;
    private List<String> permissions;
}
