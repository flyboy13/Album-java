package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapterRcv extends RecyclerView.Adapter<ImageAdapterRcv.MyViewHolder> {
    List<Photos> photos;
    private Context mContext;

    public ImageAdapterRcv(Context context, List<Photos> items) {
        this.mContext = context;
        this.photos = items;
    }

    public void filterList(List<Photos> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        photos = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.photo_search_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String image_path = photos.get(position).getPath();
        File file = new File(image_path);
        Uri imageUri = FileProvider.getUriForFile(
                mContext.getApplicationContext(),
                "com.example.myapplication" +".provider", //(use your app signature + ".provider" )
                file);
        holder.imageView.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.avatar1);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (mContext, PhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", -1);
                    bundle.putSerializable("photo", photos.get(getLayoutPosition()));
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
