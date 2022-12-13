package com.example.oauthdemo.config;

import com.example.oauthdemo.model.AuthProvider;
import com.example.oauthdemo.service.MyUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
public class CustomOauthSuccessHandler implements AuthenticationSuccessHandler {
    private static final String FRONTEND_URL = "http://localhost:8080/hello.html";
    final MyUserDetailsService myUserDetailsService;

    @Autowired
    public CustomOauthSuccessHandler(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oidcUser = (OAuth2User) authentication.getPrincipal();
        String type = request.getRequestURI().replaceFirst(".*/(\\w+).*", "$1");
        AuthProvider authProvider = AuthProvider.valueOf(type.toUpperCase(Locale.ROOT));
        myUserDetailsService.processOAuthPostLogin(oidcUser, request.getSession(), authProvider);
        response.sendRedirect(FRONTEND_URL);
    }

}
