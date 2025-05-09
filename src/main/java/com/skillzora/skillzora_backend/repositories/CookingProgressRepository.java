package com.skillzora.skillzora_backend.repositories;



import com.skillzora.skillzora_backend.models.CookingProgress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CookingProgressRepository extends MongoRepository<CookingProgress, String> {
    List<CookingProgress> findByUserId(String userId);
    List<CookingProgress> findByPostId(String postId);

}

