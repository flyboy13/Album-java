package com.example.myapplication;

import java.util.List;

public class Category {
    private String nameCategory;
    private List<MyImage> listImage;

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<MyImage> getListGirl() {
        return listImage;
    }

    public void setListGirl(List<MyImage> listImage) {
        this.listImage = listImage;
    }

    public void addListGirl(MyImage img){this.listImage.add(img);}

    public Category(String nameCategory, List<MyImage> listImage) {
        this.nameCategory = nameCategory;
        this.listImage = listImage;
    }

    public Category(List<MyImage> listImage) {
        this.listImage = listImage;
    }
}
