package com.seguo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String title;
    @Nullable
    String slug;
    @Nullable
    String cover;
    @Nullable
    String description;
    String content;
    boolean status = true;
    String type = "post";
    @Nullable
    String attachment;
    boolean is_free = true;
    @Nullable
    Double price;
    int viewCount = 0;
    int replyCount = 0;
    LocalDateTime created_at;
    @Nullable
    LocalDateTime updated_at;
    @Nullable
    LocalDateTime deleted_at;
    @JsonIgnore
    @ManyToOne(
            targetEntity = User.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
}
