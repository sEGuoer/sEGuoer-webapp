package com.seguo;

import com.seguo.entity.User;
import com.seguo.properties.AdminUseProperties;
import com.seguo.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest extends  WithMockUserBaseTest{
    @Autowired
    MockMvc mvc;

    @Autowired
    AdminUseProperties adminUseProperties;

    @Test
    void dashboard() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(MockMvcResultMatchers.view().name("backend/user/index"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<li class=\"breadcrumb-item active\">Users</li>")))
        ;
    }


    @Test
    void testAtModelAttribute() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin/dashboard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("menus"))
                .andExpect(MockMvcResultMatchers.model().attribute("menus", adminUseProperties.getAdminPageContentList()))
        ;
    }
    @Test
    void testLogout() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/logout"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
        ;
    }
    @Test
    void testAddRepeatUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "new-name")
                        .param("email", "admin@example.com")
                        .param("password", "secret"))
                .andExpect(MockMvcResultMatchers.content().string("用户添加失败请返回重试"))
        ;
    }
    @Test
    void testAddUser( @Autowired UserRepository userRepository) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "test-name")
                        .param("email", "fasttest@example.com")
                        .param("password", "secret"))
                .andExpect(MockMvcResultMatchers.content().string("用户添加成功"));
        Optional<User> user = userRepository.findFirstByEmail("fasttest@example.com");
        userRepository.delete(user.get());
    }
}
