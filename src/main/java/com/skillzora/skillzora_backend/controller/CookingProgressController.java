package com.skillzora.skillzora_backend.controller;



import com.skillzora.skillzora_backend.models.CookingProgress;
import com.skillzora.skillzora_backend.services.CookingProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cooking-progress")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CookingProgressController {

    @Autowired
    private CookingProgressService service;

    // ✅ Create plan
    @PostMapping
    public ResponseEntity<String> create(@RequestBody CookingProgress progress) {
        try {
            service.save(progress);
            return ResponseEntity.ok("✅ Progress plan for '" + progress.getRecipeTitle() + "' was added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed to save progress plan: " + e.getMessage());
        }
    }

    // ✅ Get plans by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable String userId) {
        try {
            List<CookingProgress> plans = service.getByUserId(userId);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed to fetch plans: " + e.getMessage());
        }
    }

    // ✅ Update plan
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CookingProgress progress) {
        return service.update(id, progress)
                .map(updated -> ResponseEntity.ok("✅ Plan updated successfully."))
                .orElse(ResponseEntity.status(404).body("❌ Plan not found."));
    }

    // ✅ Delete plan
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        if (deleted) return ResponseEntity.ok("✅ Plan deleted successfully.");
        else return ResponseEntity.status(404).body("❌ Plan not found.");
    }
}

