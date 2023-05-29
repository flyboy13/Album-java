package com.example.myapplication;


import java.util.ArrayList;
import java.util.List;

public class Album {
    private String pathFolder;
    private MyImage img;
    private String name;
    private List<MyImage> listImage;
    public Album(MyImage img, String name) {
        this.name = name;
        this.img = img;
        listImage = new ArrayList<>();
    }

    public void setPathFolder(String pathFolder) {
        this.pathFolder = pathFolder;
    }

    public String getPathFolder() {
        return pathFolder;
    }

    public MyImage getImg() {
        return img;
    }
    public String getName() {
        return name;
    }
    public List<MyImage> getList() {
        return listImage;
    }
    public void addList(List<MyImage> list) {
        listImage = new ArrayList<>(list);
    }
    public void addItem(MyImage img) {
        listImage.add(img);
    }
}
