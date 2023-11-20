package com.seguo.controller;

import com.seguo.dto.PasswordResetDto;
import com.seguo.dto.PasswordResetEmailDto;
import com.seguo.entity.PasswordResetToken;
import com.seguo.entity.User;
import com.seguo.service.PasswordResetTokenService;
import com.seguo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    @ResponseBody
    String addUser(String name, String email, String password) {
        int result = userService.addNewUser(name, email, password);
        if (result == 1) {
            return "用户添加成功";
        } else {
            return "用户添加失败请返回重试";
        }
    }

    @GetMapping("dashboard")
    String dashboard() {
        return "user/dashboard";
    }

    @GetMapping("password-reset")
    String showPasswordRestForm(Model model) {
        PasswordResetEmailDto passwordResetEmailDto = new PasswordResetEmailDto();
        model.addAttribute("passwordResetEmail", passwordResetEmailDto);
        return "user/forget";
    }

    @Autowired
    private JavaMailSender sender;

    @PostMapping("password-reset")
    String passwordReset(@Valid @ModelAttribute("passwordResetEmail") PasswordResetEmailDto passwordResetEmailDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes attributes,
                         HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        User existingUser = userService.findUserByEmail(passwordResetEmailDto.getEmail());
        if (existingUser == null) {
            result.rejectValue("email", "non-existent", "找不到该邮箱对应的用户信息");
        }

        if (result.hasErrors()) {
            model.addAttribute("passwordResetEmail", passwordResetEmailDto);
            return "user/forget";
        }

        // send email
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(existingUser);
        token.setExpirationDate(LocalDateTime.now().plusHours(1));
        try {
            passwordResetTokenService.save(token);
        } catch (Exception e) {
            result.rejectValue("email", null, "未知错误请联系管理员");
            model.addAttribute("passwordResetEmail",passwordResetEmailDto);
            return "/user/forget";
        }

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(new InternetAddress("admin@example.com", "Admin"));
        helper.setSubject("重置密码");
        helper.setTo(existingUser.getEmail());
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        helper.setText("<html><body><p>点击以下链接进行密码重置</p><a href='" + baseUrl + "/user/do-password-reset?token=" + token.getToken() + "'>重置密码</a><p>链接将在 30 分钟后失效，请尽快操作。</p></body></html>", true);

        sender.send(message);

        attributes.addFlashAttribute("success", "密码重置邮件已发送，请注意查收");
        return "redirect:/user/password-reset";
    }

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @GetMapping("do-password-reset")
    String showDoPasswordRestForm(@RequestParam(name = "token", required = false) String token, Model model) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.findFirstByTokenOrderByIdDesc(token);
        if (passwordResetToken == null) {
            model.addAttribute("error", "token 不存在");
        } else if (passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "token 已过期");
        }

        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setToken(token);
        model.addAttribute("passwordResetDto", passwordResetDto);

        return "user/password-reset";
    }

    @PostMapping("do-password-reset")
    public String resetPassword(@Valid @ModelAttribute("passwordResetDto") PasswordResetDto passwordResetDto,
                                BindingResult result,
                                RedirectAttributes attributes) {
        if(result.hasErrors()){
            attributes.addFlashAttribute("passwordResetDto", passwordResetDto);
            return "/user/password-reset";
        }

        PasswordResetToken token = passwordResetTokenService.findByToken(passwordResetDto.getToken());
        User user = token.getUser();
        user.setPassword(passwordResetDto.getPassword());
        userService.updatePassword(user);

        int expireThisTokenResult = passwordResetTokenService.expireThisToken(passwordResetDto.getToken());
        log.info("expireThisToken={}, return={}", passwordResetDto.getToken(), expireThisTokenResult);

        return "redirect:/login";
    }
}
