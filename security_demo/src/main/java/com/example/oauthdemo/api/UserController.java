package com.example.oauthdemo.api;

import com.example.oauthdemo.dao.UserRepository;
import com.example.oauthdemo.model.User;
import com.example.oauthdemo.service.MyUserDetailsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody @Valid User user) {
        if (!user.getPassword().equals(user.getConfirmPass()))
            throw new ResponseStatusException(HttpStatus.valueOf(422), "Passwords dont match");

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registered! Welcome");
        } catch (Exception ex) {
            if (ex instanceof DataIntegrityViolationException) {
                throw new ResponseStatusException(HttpStatus.valueOf(409), "Account already exists");
            }
            throw new ResponseStatusException(HttpStatus.valueOf(400), ex.getMessage());
        }
    }

    @GetMapping(path = "/statuslogin")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(Authentication auth,
                                                                @AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> response = new HashMap<>();

        if (oAuth2User != null) {
            response.put("user", oAuth2User.getAttribute("name"));
            response.put("loggedIn", true);

        } else if (auth != null) {
            response.put("user", auth.getPrincipal());
            response.put("loggedIn", auth.isAuthenticated());
        } else {
            response.put("user", "");
            response.put("loggedIn", false);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Object> realBasicAuthEntry(HttpSession session, Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            User loggedInUser = userRepository.findByEmail(auth.getName()).orElseThrow();
            Integer userId = loggedInUser.getId();
            session.setAttribute(MyUserDetailsService.USERID, userId);
            //return response
            response.put("success", auth.isAuthenticated());
            response.put("message", "Logged in!");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


}
