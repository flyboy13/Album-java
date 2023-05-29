package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;


public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Photos> arrayList;

    ImageAdapter(Context context, ArrayList<Photos> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
        Log.e("onCreate: ", "!!!" + arrayList.size());
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(mContext);
        String image_path = arrayList.get(position).getPath();
        File file = new File(image_path);
        Uri imageUri = FileProvider.getUriForFile(
                        mContext.getApplicationContext(),
                        "com.example.myapplication" +".provider", //(use your app signature + ".provider" )
                        file);
        imageView.setImageURI(imageUri);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
