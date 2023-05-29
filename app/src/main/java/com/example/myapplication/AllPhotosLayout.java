package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ContentProvider;
import android.content.Context;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Apdapter.PhotosApdapter;
import java.io.File;
import java.util.ArrayList;

public class AllPhotosLayout extends Fragment implements FragmentCallbacks {
    MainActivity main;
    Context context;
    GridView gridPhoto;
    Button btnSelect;
    private static PhotosApdapter adapter;
    //Array Media
    ArrayList<Photos> arrayList = new ArrayList<>();

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Toast.makeText(context, "Register success", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
    );

    //=============

    public static AllPhotosLayout newInstance() {
        AllPhotosLayout fragment = new AllPhotosLayout();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout All_Photo_layout = (LinearLayout) inflater.inflate(R.layout.all_photos_layout, null);
        assignViewByFindId(All_Photo_layout);
        main.requestPermissions();

        //=========
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), MultiSelectImage.class);

                activityLauncher.launch(intent);


            }
        });

        return All_Photo_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getImages();
    }

    private void assignViewByFindId(LinearLayout layout) {
        gridPhoto = layout.findViewById(R.id.gridPhoto);
        btnSelect = layout.findViewById(R.id.btnSelect);
    }

    @Override
    public void onMsgFromMainToFragment(String btn) {
        if(btn == "SlideShow"){
            SlideShowData.list = arrayList;
            Intent intent = new Intent(main.context, SlideShow.class);
            startActivity(intent);
        }
    }
    //Get Path Image and Video
    private void getImages() {
        arrayList.clear();
        //Use Image uri
        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        // Make the query.
        Log.e("getImages: ", images.toString());

        Cursor cur = getContext().getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );
        Cursor cur1 = getContext().getContentResolver().query(videos,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        //Images
        if (cur.moveToFirst()) {
            Log.i("ListingImages", " query count=" + cur);

            String dataImage;
            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            Log.i("ListingImages", " query count=" + dataColumn);

            do {
                // Get the field values
                dataImage = cur.getString(dataColumn);
                arrayList.add(new Photos(dataImage));
//                Log.i("ListingImages", " Data=" + dataVideo);
            } while (cur.moveToNext());

        }
        Log.e("ADebugTag", "============================================================");
        Log.e("ListImage", "arrayList " + arrayList);
        // oke

        PhotosApdapter adapter = new PhotosApdapter(getContext(), R.layout.photo, arrayList);
        gridPhoto.setAdapter(adapter);
        gridPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> container, View v, int position, long id) {
                Intent intent = new Intent (getContext(), PhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                activityLauncher.launch(intent);
            }
        });
    } //GetImages
}
