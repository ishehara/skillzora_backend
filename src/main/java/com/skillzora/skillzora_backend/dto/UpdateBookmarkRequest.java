
package com.skillzora.skillzora_backend.dto;

import jakarta.validation.constraints.Size;

public class UpdateBookmarkRequest {
    @Size(max = 1000, message = "Note must be less than 1000 characters")
    private String note;

    // Getters and Setters
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}