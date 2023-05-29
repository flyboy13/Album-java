package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class ss_ImageAdapter extends PagerAdapter {
    private Context context;
    private List<Photos> photoList;

    public ss_ImageAdapter(Context context, List<Photos> images)
    {
        this.context = context;
        this.photoList = images;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.ss_item_slide_show, container, false);

        ImageView imageView = view.findViewById(R.id.slide_show_item);
        Photos image = photoList.get(position);
        if (image != null)
        {
            Glide.with(context).load(image.path).into(imageView);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (photoList != null)
            return photoList.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
