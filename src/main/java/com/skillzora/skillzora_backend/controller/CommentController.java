package com.skillzora.skillzora_backend.controller;



import com.skillzora.skillzora_backend.models.Comment;
import com.skillzora.skillzora_backend.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommentController {

    @Autowired
    private CommentService service;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        try {
            Comment saved = service.addComment(comment);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed to save comment: " + e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getByPostId(@PathVariable String postId) {
        return ResponseEntity.ok(service.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        if (service.deleteComment(id)) {
            return ResponseEntity.ok("✅ Comment deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("❌ Comment not found.");
        }
    }


    @PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable String id, @RequestBody Comment updatedComment) {
    return service.updateComment(id, updatedComment)
            .map(comment -> ResponseEntity.ok("✅ Comment updated successfully."))
            .orElse(ResponseEntity.status(404).body("❌ Comment not found."));
}

}
