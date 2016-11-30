package com.recipe.teejay.pakrecipe.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Asim on 6/11/2016.
 */
public class Recipe {

    String RecipeID;
    String RecipeName;
    String Filename;
    String CategoryID;
    String Bookmark;
    Bitmap image;

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    String recipe;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Recipe(String Recid, String name, String filename, String catid, String bookmark) {
        this.RecipeID=Recid;
        this.RecipeName=name;
        this.Filename=filename;
        this.CategoryID=catid;
        this.Bookmark=bookmark;
    }


    public String getRecipeName() {
        return RecipeName;
    }

    public void setRecipeName(String recipeName) {
        RecipeName = recipeName;
    }

    public String getRecipeID() {
        return RecipeID;
    }

    public void setRecipeID(String recipeID) {
        RecipeID = recipeID;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public String getBookmark() {
        return Bookmark;
    }

    public void setBookmark(String bookmark) {
        Bookmark = bookmark;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }
}
