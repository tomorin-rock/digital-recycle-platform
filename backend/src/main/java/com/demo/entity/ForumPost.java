package com.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumPost {
    private Long id;

    private Long userId;

    private String title;
    private String content;

    private Integer viewCount;
    private Integer replyCount;
    private Integer likeCount;

    private Integer status; // 1正常 0屏蔽

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
