package com.example.myapplication;

import android.graphics.Bitmap;

public interface EditCallBacks {
    public void onMsgFromFragToEdit(String sender, String request, Bitmap bitmap);
}
