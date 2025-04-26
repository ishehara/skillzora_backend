package com.skillzora.skillzora_backend.services;



import com.skillzora.skillzora_backend.models.Comment;
import com.skillzora.skillzora_backend.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository repository;

    public Comment addComment(Comment comment) {
        comment.setTimestamp(new Date()); // auto-set timestamp
        return repository.save(comment);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return repository.findByPostId(postId);
    }

    public boolean deleteComment(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Comment> getById(String id) {
        return repository.findById(id);
    }

    public Optional<Comment> updateComment(String id, Comment updatedComment) {
        return repository.findById(id).map(existing -> {
            existing.setCommentText(updatedComment.getCommentText());
            existing.setTimestamp(new Date()); // Update timestamp
            return repository.save(existing);
        });
    }
    
}
