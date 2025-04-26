package com.skillzora.skillzora_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookmarkRequest {
    @NotBlank(message = "Post ID is required")
    private String postId;

    @Size(max = 1000, message = "Note must be less than 1000 characters")
    private String note;

    // Getters and Setters
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
}