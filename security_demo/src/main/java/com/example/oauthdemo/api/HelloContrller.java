package com.example.oauthdemo.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloContrller {
    @GetMapping("/hello.html")
    public String main() {
        return "main.html";
    }
}
