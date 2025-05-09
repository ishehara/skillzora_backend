package com.skillzora.skillzora_backend.services;



import com.skillzora.skillzora_backend.models.CookingProgress;
import com.skillzora.skillzora_backend.repositories.CookingProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CookingProgressService {

    @Autowired
    private CookingProgressRepository repository;

    public CookingProgress save(CookingProgress progress) {
        return repository.save(progress);
    }

    public List<CookingProgress> getByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public Optional<CookingProgress> update(String id, CookingProgress updated) {
        return repository.findById(id).map(existing -> {
            existing.setRecipeTitle(updated.getRecipeTitle());
            existing.setSteps(updated.getSteps());
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

    public List<CookingProgress> getByPostId(String postId) {
        return repository.findByPostId(postId);
    }

    public List<CookingProgress> getAllProgress() {
        return repository.findAll();
    }
    
    
}
