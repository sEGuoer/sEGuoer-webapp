package com.seguo.repository;


import com.seguo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findFirstByTitle(String title);

    Page<Post> findAllByType(String type, Pageable pageable);
}
