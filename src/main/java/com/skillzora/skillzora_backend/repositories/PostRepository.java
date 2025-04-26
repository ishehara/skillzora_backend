package com.skillzora.skillzora_backend.repositories;



import com.skillzora.skillzora_backend.models.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
