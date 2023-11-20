package com.seguo.controller;

import com.seguo.entity.Post;
import com.seguo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller("PostController")
@RequestMapping("/blogs")
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping
    String index(Model model,
                 @RequestParam("page") Optional<Integer> page,
                 @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Post> pageContent = postService.findAllPosts(currentPage, pageSize);
        model.addAttribute("page", pageContent);
        return "blog/index";
    }

    @GetMapping("{id}")
    String show(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.findById(id);

        if (optionalPost.isEmpty() || !optionalPost.get().isStatus() || !"post".equals(optionalPost.get().getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("post", optionalPost.get());
        return "blog/show";
    }

    @GetMapping("vue/{id}")
    @ResponseBody
    @CrossOrigin
    Post vueShow(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty() || !optionalPost.get().isStatus() || !"post".equals(optionalPost.get().getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else {
            Post post = optionalPost.get();
            System.out.println(post);
            return post;
        }
    }
}
