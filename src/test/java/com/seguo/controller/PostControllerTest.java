package com.seguo.controller;


import com.seguo.WithMockUserForAdminBaseTest;
import com.seguo.entity.Post;
import com.seguo.repository.PostRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

@WithUserDetails(userDetailsServiceBeanName = "jpaUserDetailsService", value = "admin@example.com")
class PostControllerTest extends WithMockUserForAdminBaseTest {

    @Test
    void index() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin/blogs"))
                .andExpect(MockMvcResultMatchers.view().name("backend/blog/index"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Post 管理")))
        ;
    }
    @Test
    void create() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin/blog/create"))
                .andExpect(MockMvcResultMatchers.view().name("backend/blog/create"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Create New Post")))
        ;
    }
    @Test
    @DisplayName("发送的title和content为空时会提示错误消息")
    void storeWithoutTitleAndContent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/admin/blog/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("user_id", "1")
                        .param("title", "")
                        .param("content", "")
                )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("post", "title", "NotEmpty"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("post", "content", "NotEmpty"))
        ;
    }
    @Test
    @DisplayName("填写正确的内容后的测试")
    void store(@Autowired PostRepository postRepository) throws Exception {
        String title = "title-" + UUID.randomUUID();
        mvc.perform(MockMvcRequestBuilders.post("/admin/blog/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("user_id", "1")
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/blogs"))
        ;

        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());

        postRepository.delete(po.get());
    }
}