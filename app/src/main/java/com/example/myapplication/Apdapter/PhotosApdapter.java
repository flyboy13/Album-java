package com.example.myapplication.Apdapter;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.Photos;
import com.example.myapplication.R;
import com.example.myapplication.onBindView;

import java.util.ArrayList;


public class PhotosApdapter extends ArrayAdapter<Photos> implements onBindView {

    public final Context context;
    //Test
    public final ArrayList<Photos> items;
    AdapterView.OnItemClickListener onItemClickListener;

    public PhotosApdapter(Context context, int layoutToBeInflated, ArrayList<Photos> items)
    {
        super(context, R.layout.all_photos_layout, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.photo, null);
        ImageView avatar = row.findViewById(R.id.avatar1);
        Log.d("ADebugTag", " ============================================================");
        Log.d("ADebugTag", "Value: " + items.get(position).getPath());
//        avatar.setImageBitmap(BitmapFactory.decodeFile(items.get(position).getPath()));
//        Glide.with(getContext()).load(items.get(position).getPath()).into((ImageView) row);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("ADebugTag", " ============================================================");
            Log.d("ADebugTag", "ACCESS!! ");
//            avatar.setImageBitmap(BitmapFactory.decodeFile(items.get(position).getPath()));
            Glide.with(getContext()).load(items.get(position).getPath()).into(avatar);
//            avatar.setOnClickListener((View.OnClickListener) this);

        }
        else {

            Log.d("ADebugTag", " ============================================================");
            Log.d("ADebugTag", "CANNOT ACCESS ");
        }
        return (row);

    }//Get view





}
