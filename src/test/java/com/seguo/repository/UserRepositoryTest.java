package com.seguo.repository;

import com.seguo.entity.PasswordResetToken;
import com.seguo.entity.User;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MockMvc mvc;

    @Test
    void findUserByName() {
        User user = new User();
        String userName = "test";
        user.setName(userName);
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail(userName + "@example.com");
        user.setPhone("18888888888");
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);
        userRepository.save(user);

        Optional<User> optionalUser = userRepository.findFirstByName(userName);
        Assertions.assertTrue(optionalUser.isPresent());
        userRepository.delete(user);
        Assertions.assertFalse(userRepository.findAll().contains(user));
    }

    @Test
    @DisplayName("找回页面是否正常显示")
    void getPasswordRestPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/password-reset"))
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("发送重置密码链接")))
        ;

    }

    @Test
    @DisplayName("输入一个数据库中没有的邮箱看结果是否符合预期")
    void postPasswordRestWithNonExistentEmail() throws Exception {
        String randomStr = UUID.randomUUID().toString().substring(0, 6);
        mvc.perform(MockMvcRequestBuilders.post("/user/password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", randomStr + "@example.com")
                )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("passwordResetEmail", "email", "non-existent"))
        ;
    }

    @Test
    @Disabled
    @DisplayName("输入一个数据库中有的邮箱看结果是否可以发送邮件")
    void postPasswordRestWithExistEmail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "admin@example.com")
                )
                .andExpect(MockMvcResultMatchers.flash().attribute("success", "密码重置邮件已发送，请注意查收"))
        ;
    }


    @Test
    @DisplayName("无token时访问页面会有error提示token不存在")
    void showDoPasswordRestFormWithNonExistentToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(MockMvcResultMatchers.model().attribute("error", "token 不存在"))
        ;
    }

    @Test
    @DisplayName("检验过期功能,模拟token在数据库中保存的时间在现在的时间之前的情况下，是否会显示token过期")
    void showDoPasswordRestFormWithExpiredToken(@Autowired UserRepository userRepository,
                                                @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        User user = new User();
        String username = UUID.randomUUID().toString().substring(0, 6);
        user.setName(username);
        user.setEmail(username + "@example.com");
        userRepository.save(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        String expiredToken = UUID.randomUUID().toString();
        token.setToken(expiredToken);
        token.setExpirationDate(LocalDateTime.now().minusHours(1));
        passwordResetTokenRepository.save(token);

        mvc.perform(MockMvcRequestBuilders.get("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", expiredToken)
                )
                .andExpect(MockMvcResultMatchers.model().attribute("error", "token 已过期"))
        ;

        passwordResetTokenRepository.delete(token);
        userRepository.delete(user);
    }

    @Test
    @DisplayName("token正常且没过期")
    void showDoPasswordRestFormWithCorrectToken(@Autowired UserRepository userRepository,
                                                @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        User user = new User();
        String username = UUID.randomUUID().toString().substring(0, 6);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        String correctToken = UUID.randomUUID().toString();
        token.setToken(correctToken);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(token);

        mvc.perform(MockMvcRequestBuilders.get("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", correctToken)
                )
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("新密码")))
        ;

        passwordResetTokenRepository.delete(token);
        userRepository.delete(user);
    }

    @Test
    @DisplayName("模拟用户输入两次密码不同的情况")
    void postResetPasswordWithMismatchingPassword(@Autowired UserRepository userRepository,
                                                  @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        User user = new User();
        String username = UUID.randomUUID().toString().substring(0, 6);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        String correctToken = UUID.randomUUID().toString();
        token.setToken(correctToken);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(token);

        mvc.perform(MockMvcRequestBuilders.post("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", correctToken)
                        .param("password", "new-password")
                        .param("confirmPassword", "mismatching-password")
                )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("passwordResetDto", "confirmPassword", "PasswordMatch"))
        ;

        passwordResetTokenRepository.delete(token);
        userRepository.delete(user);
    }

    @Test
    @DisplayName("模拟用户输入正确的更新信息")
    void postResetPassword(@Autowired UserRepository userRepository,
                           @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        User user = new User();
        String username = UUID.randomUUID().toString().substring(0, 6);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        String correctToken = UUID.randomUUID().toString();
        token.setToken(correctToken);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(token);

        mvc.perform(MockMvcRequestBuilders.post("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", correctToken)
                        .param("password", "new-password")
                        .param("confirmPassword", "new-password")
                )
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
        ;

        passwordResetTokenRepository.delete(token);
        userRepository.delete(user);
    }
    @Test
    void expireThisTokenAfterPasswordReset(@Autowired UserRepository userRepository,
                                           @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        User user = new User();
        String username = UUID.randomUUID().toString().substring(0, 6);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        String correctToken = UUID.randomUUID().toString();
        token.setToken(correctToken);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(token);

        mvc.perform(MockMvcRequestBuilders.post("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", correctToken)
                        .param("password", "new-password")
                        .param("confirmPassword", "new-password")
                )
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
        ;

        mvc.perform(MockMvcRequestBuilders.get("/user/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", correctToken)
                )
                .andExpect(MockMvcResultMatchers.model().attribute("error", "token 已过期"))
        ;

        passwordResetTokenRepository.delete(token);
        userRepository.delete(user);
    }
}
