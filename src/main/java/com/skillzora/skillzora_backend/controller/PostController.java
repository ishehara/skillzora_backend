package com.skillzora.skillzora_backend.controller;

import com.skillzora.skillzora_backend.models.Post;
import com.skillzora.skillzora_backend.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService service;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("hashtags") String hashtags,
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            // ✅ Absolute path to project root/uploads folder
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            // ✅ Save image file
            String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String filePath = uploadDir + File.separator + filename;
            imageFile.transferTo(new File(filePath));

            // ✅ Create and save Post object
            Post post = new Post();
            post.setTitle(title);
            post.setDescription(description);
            post.setHashtags(Arrays.asList(hashtags.split(",")));
            post.setImageUrl("/uploads/" + filename); // for frontend access

            return ResponseEntity.ok(service.save(post));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("❌ Image upload failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("❌ Post not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Post updated) {
        return service.update(id, updated)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("❌ Post not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        return service.delete(id)
                ? ResponseEntity.ok("✅ Post deleted")
                : ResponseEntity.status(404).body("❌ Post not found");
    }
}
