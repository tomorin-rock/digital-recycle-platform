package com.demo.utils;

public class RedisKeyUtil {
    public static String getLikedPostsKey(Long userId) {
        return "user:" + userId + ":liked_posts";
    }
    public static String getViewedPostsKey(Long userId) {
        return "user:" + userId + ":viewed_posts";
    }
    public static String getLikedCommentsKey(Long userId) {
        return "user:" + userId + ":liked_comments";
    }
}
