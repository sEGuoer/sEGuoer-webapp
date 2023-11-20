package com.seguo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private boolean enabled;

    public User(Long id) {
        this.id = id;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName="id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();
}
