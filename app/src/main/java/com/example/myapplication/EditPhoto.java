package com.example.myapplication;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class EditPhoto extends FragmentActivity implements EditCallBacks
{
    Button btnCancle,btnSave;
    EditImageView editImageView;
    RecyclerView toolsRecView;
    Fragment currentFragment;
    List<Tool> toolItemList;
    ToolAdapter adapter;
    String pathPictureFile;
    int widthScreen, heightScreen;
    Bitmap resourcei;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photo);
        assignViewByFindId();


        Intent intent = getIntent();
        pathPictureFile = intent.getStringExtra("ImageUri");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heightScreen = displayMetrics.heightPixels;

        editImageView = (EditImageView) findViewById(R.id.imgEdit);
        Glide.with(this).asBitmap().load(pathPictureFile).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                resourcei=resource;
                editImageView.setBitmapResource(resource, widthScreen, heightScreen*6/10);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        toolsRecView = (RecyclerView) findViewById(R.id.toolsRecView);
        initTool();
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //editImageView.scaleBitmapResource();
                saveImage(editImageView.getEditBitmap());
                finish();
            }
        });


    }

    private void initTool() {
        toolItemList = new ArrayList<Tool>();

        toolItemList.add(new Tool(R.drawable.ic_baseline_photo_filter_24, "Filter"));
        toolItemList.add(new Tool(R.drawable.ic_baseline_filter_none_24, "Blur"));
        toolItemList.add(new Tool(R.drawable.ic_baseline_flip_24, "Flip"));
        toolItemList.add(new Tool(R.drawable.ic_baseline_transform_24, "Resize"));
        toolItemList.add(new Tool(R.drawable.ic_baseline_brush_24, "Brush"));
        adapter = new ToolAdapter(toolItemList, EditPhoto.this, editImageView);
        toolsRecView.setAdapter(adapter);
        toolsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void assignViewByFindId()
    {
        btnSave = findViewById(R.id.button_Save);
        btnCancle = findViewById(R.id.button_Cancle);
    }

    private void saveImage(Bitmap bitmap) {

        String pathFile = pathPictureFile.substring(0, pathPictureFile.lastIndexOf("/"));
        File pictureFile = new File(pathFile, bitmap.toString() + ".jpg");
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            Toast.makeText(this, "Saved to " + pathFile, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Log.e("Error to save image in edit ", e.getMessage());
        }
    }

    public void inflateFragment(Fragment fragment) {
        toolsRecView.setVisibility(View.GONE);
        //.hide();
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.editFragment, currentFragment).commit();
    }

    @Override
    public void onMsgFromFragToEdit(String sender, String request, Bitmap bitmap) {
        if (request.equals("CLEAR")) {
            if (sender.equals("FILTER")) {
                editImageView.clearFilter();
            }
        }
        if (request.equals("CHECK")) {
            editImageView.saveImage();
        }

        getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        //getSupportActionBar().show();
        toolsRecView.setVisibility(View.VISIBLE);
    }

}

