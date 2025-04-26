package com.skillzora.skillzora_backend.services;



import com.skillzora.skillzora_backend.models.Post;
import com.skillzora.skillzora_backend.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository repository;

    public Post save(Post post) {
        return repository.save(post);
    }

    public List<Post> getAll() {
        return repository.findAll();
    }

    public Optional<Post> getById(String id) {
        return repository.findById(id);
    }

    public Optional<Post> update(String id, Post updatedPost) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updatedPost.getTitle());
            existing.setDescription(updatedPost.getDescription());
            existing.setHashtags(updatedPost.getHashtags());
            existing.setImageUrl(updatedPost.getImageUrl());
            return repository.save(existing);
        });
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
