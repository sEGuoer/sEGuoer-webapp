package com.seguo.service;


import com.seguo.entity.Post;
import org.springframework.data.domain.Page;

public interface PostService {
    Page<Post> findAll(int pageNumber, int pageSize);
}