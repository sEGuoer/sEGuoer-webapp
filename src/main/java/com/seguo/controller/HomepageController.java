package com.seguo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {
    @GetMapping("index")
    String index() {
        return "index";
    }
    @GetMapping("login")
    String login() {
        return "login";
    }
    @GetMapping("register")
    String register() {
        return "register";
    }

}
