package com.example.thienpro.mvp_firebase.model.entity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThienPro on 11/11/2017.
 */

public class Post {
    private String id;
    private String name;
    private String timePost;
    private String post;

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("id", id);
//        result.put("name", name);
//        result.put("timePost", timePost);
//        result.put("post", post);
//        return result;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Post(String id, String name, String timePost, String post) {
        this.id = id;
        this.name = name;
        this.timePost = timePost;
        this.post = post;
    }
}
