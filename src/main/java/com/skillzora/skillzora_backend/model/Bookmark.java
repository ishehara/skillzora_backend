
package com.skillzora.skillzora_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bookmarks")
public class Bookmark {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String note;
    private Date createdAt;
    private Date updatedAt;

    public Bookmark() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Bookmark(String userId, String postId, String note) {
        this();
        this.userId = userId;
        this.postId = postId;
        this.note = note;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}