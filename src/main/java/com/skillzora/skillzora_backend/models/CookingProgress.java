package com.skillzora.skillzora_backend.models;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "cooking_progress")
public class CookingProgress {

    @Id
    private String id;
    private String userId;
    private String recipeTitle;
    private List<CookingStep> steps;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public List<CookingStep> getSteps() {
        return steps;
    }

    public void setSteps(List<CookingStep> steps) {
        this.steps = steps;
    }
}
