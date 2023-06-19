package vn.com.lifesup.hackathon.security;

import vn.com.lifesup.hackathon.security.jwt.JwtPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class UserDetails extends User {
    private JwtPayload user;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public void setUser(JwtPayload user) {
        this.user = user;

    }
}
