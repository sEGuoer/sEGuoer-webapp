package com.seguo;

import com.seguo.entity.Post;
import com.seguo.entity.User;
import com.seguo.repository.PostRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceTest {
    @Autowired
    MockMvc mvc;
    @Test
    void resources() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/resources"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("查看详情")))
        ;
    }
    @Autowired
    PostRepository postRepository;
    @Test
    void show() throws Exception {
        Post post = new Post();
        post.setCreated_at(LocalDateTime.now());
        String title = UUID.randomUUID().toString();
        post.setTitle(title);
        post.setContent(UUID.randomUUID().toString());
        post.setUser(new User(1L));
        post.setType("resource");
        postRepository.save(post);
        System.out.println(post.getId());
        mvc.perform(MockMvcRequestBuilders
                        .get("/resources/" + post.getId()))
                .andExpect(MockMvcResultMatchers.view().name("resource/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("post"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(title)))
        ;

        postRepository.deleteById(post.getId());
    }
}
