package com.example.myapplication;
public class ImageItem
{
    private String name;
    private int avatar;

    public ImageItem()
    {
        // Do nothing

    }

    public ImageItem(String name, int avatar)
    {
        this.name = name;
        this.avatar = avatar;
    }

    public int getAvatar()
    {
        return avatar;
    }

    public String getName()
    {
        return name;
    }

}
