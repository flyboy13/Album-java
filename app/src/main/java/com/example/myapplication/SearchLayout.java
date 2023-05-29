package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchLayout extends Fragment implements FragmentCallbacks {
    MainActivity main;
    Context context;
    EditText editText_search;
    RecyclerView recyclerView;
    List<Photos> arrayList = new ArrayList<Photos>();
    List<Photos> filterList = new ArrayList<Photos>();
    ImageAdapterRcv imageAdapterRcv;
    public static SearchLayout newInstance(){
        SearchLayout fragment = new SearchLayout();
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
    public void onResume() {
        super.onResume();
        GetAllPhotos();
        filter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.search_page_layout, null);
        recyclerView = layout.findViewById(R.id.rcv_grid);
        editText_search = layout.findViewById(R.id.editText_search);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(main.getApplication(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageAdapterRcv = new ImageAdapterRcv(main.getApplicationContext(),filterList);
        recyclerView.setAdapter(imageAdapterRcv);

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filter();
            }
        });
        return layout;
    }

    public void filter(){
        filterList.clear();
        if(editText_search.getText().length()>0){
            for(Photos photo : arrayList) {
                String image_path = photo.getPath();
                Uri imageUri = FileProvider.getUriForFile(
                        main.getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", //(use your app signature + ".provider" )
                        new File(photo.getPath()));

                File file = new File(image_path);
                if (file.getName().toLowerCase().contains(editText_search.getText().toString().toLowerCase(Locale.ROOT))) {
                    filterList.add(photo);
                    continue;
                }
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(imageUri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                ExifInterface exifInterface = null;
                try {
                    exifInterface = new ExifInterface(fileDescriptor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String str_latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String str_longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                if (str_latitude == null || str_longitude == null) {
                    continue;
                } else {
                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(main.getApplication(), Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(
                                ConvertStringToLat(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)),
                                ConvertStringToLat(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)),
                                1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    if (state.toLowerCase(Locale.ROOT).contains(editText_search.getText().toString().toLowerCase(Locale.ROOT))
                            || country.toLowerCase(Locale.ROOT).contains(editText_search.getText().toString().toLowerCase(Locale.ROOT))) {
                        filterList.add(photo);
                        continue;
                    }
                }
            }
        }
        imageAdapterRcv.filterList(filterList);
    }

    public double ConvertStringToLat(String str){ //ex: "12/1,10/1,20012/2012" => 12.16943
        if(str == null) return 0;
        String[] split1 = str.split(",");
        double result = 0;
        int i = 1;
        for (String s : split1) {
            String[] split2 = s.split("/");
            result += Double.parseDouble(split2[0])/Double.parseDouble(split2[1])/i;
            i *= 60;
        }
        return result;
    }


    public void GetAllPhotos(){
        arrayList.clear();
        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.e("getImages: ", images.toString());

        Cursor cur = main.getApplicationContext().getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );
        if (cur.moveToFirst()) {
            String dataImage;
            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            do {
                dataImage = cur.getString(dataColumn);
                arrayList.add(new Photos(dataImage));
            } while (cur.moveToNext());
        }
    }
    @Override
    public void onMsgFromMainToFragment(String btn) {

    }
}
