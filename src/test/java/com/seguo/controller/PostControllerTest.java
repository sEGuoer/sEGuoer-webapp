package com.seguo.controller;


import com.seguo.WithMockUserForAdminBaseTest;
import com.seguo.entity.Post;
import com.seguo.repository.PostRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void storeWithCoverImage(@Autowired PostRepository postRepository, @Autowired Environment env) throws Exception {
        String title = "title-" + UUID.randomUUID();
        MockMultipartFile coverFile = new MockMultipartFile("coverFile", "cover.png", MediaType.IMAGE_PNG_VALUE, new byte[] { 1, 2, 3 });
        mvc.perform(MockMvcRequestBuilders
                        .multipart("/admin/blog/create")
                        //.contentType(MediaType.MULTIPART_FORM_DATA)
                        .file(coverFile)
                        .param("id", "")
                        .param("user_id", "1")
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/blogs"))
        ;

        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());

        String cover = po.get().getCover();
        File coverOnDisk = new File(env.getProperty("custom.upload.base-path") +  cover);
        Assertions.assertTrue(Files.exists(coverOnDisk.toPath()));
        Assertions.assertTrue(coverOnDisk.delete());

        postRepository.delete(po.get());
    }

    @Test
    void update(@Autowired PostRepository postRepository) throws Exception {
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
        Post post = po.get();

        String descriptionUpdated = "description--updated";
        String contendUpdated = post.getContent() + "--updated";
        mvc.perform(MockMvcRequestBuilders.put("/admin/blog/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", post.getId().toString())
                        .param("title", post.getTitle())
                        .param("user_id", "1")
                        .param("description", descriptionUpdated)
                        .param("content", contendUpdated)
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/blogs"))
        ;

        Post postUpdated = postRepository.findFirstByTitle(title).get();
        Assertions.assertEquals(descriptionUpdated, postUpdated.getDescription());
        Assertions.assertEquals(contendUpdated, postUpdated.getContent());

        postRepository.delete(po.get());
    }
    @Test
    void updatePostThatNotMyOwn(@Autowired PostRepository postRepository) throws Exception {
        String title = "title-" + UUID.randomUUID();
        String authorId = "3";
        mvc.perform(MockMvcRequestBuilders.post("/admin/blog/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("user_id", authorId)
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/blogs"))
        ;
        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());
        Post post = po.get();
        String descriptionUpdated = "description--updated";
        String contendUpdated = post.getContent() + "--updated";
        mvc.perform(MockMvcRequestBuilders.put("/admin/blog/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", post.getId().toString())
                        .param("title", post.getTitle())
                        .param("user_id", authorId)
                        .param("description", descriptionUpdated)
                        .param("content", contendUpdated)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
        ;

        postRepository.delete(po.get());
    }

    @Test
    void deleteById(@Autowired PostRepository postRepository) throws Exception {
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
        Post post = po.get();

        mvc.perform(MockMvcRequestBuilders.delete("/admin/blog/destroy/" + post.getId()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/blogs"))
        ;

        Optional<Post> op = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(op.isEmpty());
    }

    @Test
    void batchDelete(@Autowired PostRepository postRepository) throws Exception {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
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
            Post post = po.get();
            ids.add(post.getId());
        }


        mvc.perform(MockMvcRequestBuilders.delete("/admin/blog/destroy")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("ids[]", ids.get(0).toString())
                        .param("ids[]", ids.get(1).toString())
                )
                .andExpect(MockMvcResultMatchers.content().string("DONE"))
        ;

        for (int i = 0; i < 2; i++) {
            Optional<Post> op = postRepository.findById(ids.get(i));
            Assertions.assertTrue(op.isEmpty());
        }
    }
}