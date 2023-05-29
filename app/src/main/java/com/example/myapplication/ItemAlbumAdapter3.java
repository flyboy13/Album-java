package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class ItemAlbumAdapter3 extends RecyclerView.Adapter<ItemAlbumAdapter3.ItemAlbumViewHolder> {
    private ArrayList<String> album;
    private ImageView imgPhoto;
    private static int REQUEST_CODE_PIC = 10;

    public ItemAlbumAdapter3(ArrayList<String> album) {
        this.album = album;
    }

    public void setData(ArrayList<String> album) {
        this.album = album;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ItemAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture_album_linear, parent, false);

        return new ItemAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(album.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(album !=null)
            return album.size();
        return 0;
    }



    public class ItemAlbumViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        public ItemAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
        public void onBind(String img, int pos) {
            // set ảnh cho imgPhoto bằng thư viện Glide
            Glide.with(context).load(img).into(imgPhoto);
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent ( context, PhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", pos);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

//                    ((Activity)context).startActivityForResult(intent,REQUEST_CODE_PIC);
                }
            });

        }
    }
}
