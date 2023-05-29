package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddImageToAlbumActivity extends AppCompatActivity implements ListTransInterface {
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private RecyclerView rycAddAlbum;
    private List<MyImage> listImage;
    private ArrayList<MyImage> listImageSelected;
    private Intent intent;
    private String path_folder;
    private ArrayList<String> myAlbum;
    private String album_name;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_album);

        intent = getIntent();
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
        path_folder = intent.getStringExtra("path_folder");
        album_name = intent.getStringExtra("name_folder");
        myAlbum = intent.getStringArrayListExtra("list_image");
    }
    private void event() {
        img_back_create_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setViewRyc();

        btnTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAlbumAsyncTask createAlbumAsyncTask = new AddAlbumAsyncTask();
                createAlbumAsyncTask.execute();
            }
        });
    }


    private void setViewRyc() {
        listImage = GetAllPhotoFromGallery.getAllImageFromGallery(context);
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this);
        imageAdapter.setListTransInterface(this);
        imageAdapter.setData(listImage);
        rycAddAlbum.setLayoutManager(new GridLayoutManager(this, 4));
        rycAddAlbum.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        rycAddAlbum = findViewById(R.id.rycAddAlbum);
    }

    @Override
    public void addList(MyImage img) {
        listImageSelected.add(img);
    }
    public void removeList(MyImage img) {
        listImageSelected.remove(img);
    }
    public class AddAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        ArrayList<String> list;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String[] paths = new String[listImageSelected.size()];
            int i =0;
            Set<String> imageListFavor = DataLocalManager.getListSet();
            for (MyImage img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(path_folder,album_name+"_"+imgFile.getName());
                list.add(desImgFile.getPath());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
                for (String imgFavor: imageListFavor){
                    if(imgFavor.equals(imgFile.getPath())){
                        imageListFavor.remove(imgFile.getPath());
                        imageListFavor.add(desImgFile.getPath());
                        break;
                    }
                }

            }
            DataLocalManager.setListImg(imageListFavor);
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("list_result", list);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
