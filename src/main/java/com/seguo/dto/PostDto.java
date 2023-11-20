package com.seguo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private Long user_id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String description;

    @Pattern(regexp = "post|resource")
    private String type = "post";

    private boolean status;

    private String cover;
}
