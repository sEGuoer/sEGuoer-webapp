package com.seguo.controller.admin;

import com.seguo.entity.User;
import com.seguo.po.AdminPageContent;
import com.seguo.properties.AdminUseProperties;
import com.seguo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {
    @GetMapping("dashboard")
    String index() {
        return "backend/dashboard";
    }
    @Autowired
    UserService userService;
    @GetMapping("users")
    String users(Model model,
                 @RequestParam("page") Optional<Integer> page,
                 @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);
        Page<User> pageContent = userService.findAll(currentPage, pageSize);
        model.addAttribute("page", pageContent);
        return "backend/user/index";
    }


}
