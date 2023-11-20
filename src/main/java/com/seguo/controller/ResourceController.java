package com.seguo.controller;

import com.seguo.entity.Post;
import com.seguo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    PostService postService;

    @GetMapping
    String index(Model model,
                 @RequestParam("page") Optional<Integer> page,
                 @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Post> pageContent = postService.findAllResources(currentPage, pageSize);
        model.addAttribute("page", pageContent);
        return "resource/index";
    }

    @GetMapping("{id}")
    String show(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.findById(id);

        if (optionalPost.isEmpty() || !optionalPost.get().isStatus() || !optionalPost.get().getType().equals("resource")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("post", optionalPost.get());
        return "resource/show";
    }
}
