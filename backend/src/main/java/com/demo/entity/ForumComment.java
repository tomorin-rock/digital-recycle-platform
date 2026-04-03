package com.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumComment {
    private Long id;

    private Long postId;
    private Long userId;

    private Long parentId;

    private String content;

    private Integer status;

    private Integer likeCount;

    private LocalDateTime createTime;
}
