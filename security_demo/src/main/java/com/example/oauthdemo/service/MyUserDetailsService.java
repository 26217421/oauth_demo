package com.example.oauthdemo.service;

import com.example.oauthdemo.dao.UserRepository;
import com.example.oauthdemo.model.AuthProvider;
import com.example.oauthdemo.model.CustomOAuthUser;
import com.example.oauthdemo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    public static final String USERID = "USER_ID";
    private static final String SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    }

    /**
     * Checks if Google-User exists in dB. If not, register as new user.
     * Else just login with new Session.
     *
     * @param oAuth2User authenticated User
     * @param session  logged-in session
     */
    public void processOAuthPostLogin(OAuth2User oAuth2User, HttpSession session,
                                      AuthProvider authProvider) {
        CustomOAuthUser m = new CustomOAuthUser(oAuth2User);
        Optional<User> existUser = userRepository.findByEmail(m.getEmail());

        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setFullname((String) m.getAttributes().get("name"));
            newUser.setEmail(m.getEmail());
            newUser.setConfirmPass("WHATEVER!");
            newUser.setAuthProvider(authProvider);

            userRepository.save(newUser);
            session.setAttribute(USERID, newUser.getId());
            return;
        }
        Integer userId = existUser.get().getId();
        session.setAttribute(USERID, userId);
    }

}
