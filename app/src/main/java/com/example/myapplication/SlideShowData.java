package com.example.myapplication;

import java.util.ArrayList;

public class SlideShowData {
    public static ArrayList<Photos> list;

    public static void setList(ArrayList<Photos> data) {
        list = new ArrayList<>(data);
    }
    public static void clearList(){
        list.clear();
    }
}