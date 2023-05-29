package com.example.myapplication;

public class MyImage {
    private String path;
    private String thumb;
    private String dateTaken;
    public String getPath() {
        return path;
    }

    public MyImage() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public MyImage(String path, String thumb) {
        this.path = path;
        this.thumb = thumb;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }
}
