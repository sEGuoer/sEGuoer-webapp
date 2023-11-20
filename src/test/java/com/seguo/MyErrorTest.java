package com.seguo;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", password = "password", roles = {"user"})
public class MyErrorTest {
    @Autowired
    MockMvc mvc;
    @Test
    void errorForOFor() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/asjdaskdhasdas"))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    void errorForOThree() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

}
