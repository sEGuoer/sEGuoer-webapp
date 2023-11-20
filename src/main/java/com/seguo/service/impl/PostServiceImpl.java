package com.seguo.service.impl;

import com.seguo.dto.PostDto;
import com.seguo.entity.Post;
import com.seguo.entity.User;
import com.seguo.repository.PostRepository;
import com.seguo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;

    @Override
    public Page<Post> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,Sort.by("id").descending());
        return this.postRepository.findAll(pageable);
    }
    @Override
    public void savePost(PostDto postDto) {
        Post post = new Post();

        if (postDto.getId() != null) {
            post = postRepository.findById(postDto.getId()).get();
            post.setUpdated_at(LocalDateTime.now());
        } else {
            post.setCreated_at(LocalDateTime.now());
        }
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());
        post.setStatus(postDto.isStatus());
        post.setType(postDto.getType());
        post.setCover(postDto.getCover());
        post.setUser(new User(postDto.getUser_id()));
        postRepository.save(post);
    }
    @Override
    public void destroy(Long id) {
        this.postRepository.deleteById(id);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return this.postRepository.findById(id);
    }

    @Override
    public void destroyAllById(List<Long> ids) {
        this.postRepository.deleteAllById(ids);
    }

    @Override
    public Page<Post> findAllPosts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").descending());
        return this.postRepository.findAllByType("post", pageable);
    }

    @Override
    public Page<Post> findAllResources(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").descending());
        return this.postRepository.findAllByType("resource", pageable);
    }
}
