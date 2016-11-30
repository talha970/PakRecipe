package com.recipe.teejay.pakrecipe.Models;

import android.graphics.Bitmap;

/**
 * Created by Asim on 6/11/2016.
 */
public class Category {
    int rowid;
    String CategoryID;
    String CategoryName;
    String CategoryImage;
    String CategoryOrder;
    Bitmap CategoryBitmap;

    public Bitmap getCategoryBitmap() {
        return CategoryBitmap;
    }

    public void setCategoryBitmap(Bitmap categoryBitmap) {
        CategoryBitmap = categoryBitmap;
    }

    public Category(String id , String name, String image, String order) {
        this.CategoryID=id;
        this.CategoryName=name;
        this.CategoryImage=image;
        this.CategoryOrder=order;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        CategoryImage = categoryImage;
    }

    public String getCategoryOrder() {
        return CategoryOrder;
    }

    public void setCategoryOrder(String categoryOrder) {
        CategoryOrder = categoryOrder;
    }
}
