package com.example.oauthdemo.model;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@ToString
public class CustomOAuthUser extends User implements OAuth2User, Serializable {
    @Serial
    private static final long serialVersionUID = -8362892628832016809L;
    private final OAuth2User oAuth2User;

    public CustomOAuthUser(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getUsername() {
        return oAuth2User.getAttribute("name");
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    @Override
    public String getEmail() {
        String email = oAuth2User.getAttribute("email");
        if(StringUtils.hasLength(email)) {
            email = "Null@abc.com";
        }
        return email;
    }

}
