package vn.com.lifesup.hackathon.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthentication implements Authentication {
    private final UserDetails user;

    public UserAuthentication(UserDetails user) {
        this.user = user;
    }

    public String getName() {
        return user.getUsername();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getAuthorities();
    }

    public Object getCredentials() {
        return null;
    }

    public Object getDetails() {
        return this.user;
    }

    public Object getPrincipal() {
        return this.user.getUsername();
    }

    public boolean isAuthenticated() {
        return this.user != null && this.user.getUser().getUserId() != null;
    }

    public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
    }
}
