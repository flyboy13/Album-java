package com.example.myapplication;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateAlbumActivity extends AppCompatActivity implements ListTransInterface {
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private EditText edtTitleAlbum;
    private RecyclerView rycAddAlbum;
    private List<MyImage> listImage;
    private ArrayList<MyImage> listImageSelected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
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
                if(!TextUtils.isEmpty(edtTitleAlbum.getText())) {
                    if(edtTitleAlbum.getText().toString().contains("#")) {
                        Toast.makeText(getApplicationContext(), "Không được chứa kí tự #", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        CreateAlbumAsyncTask createAlbumAsyncTask = new CreateAlbumAsyncTask();
                        createAlbumAsyncTask.execute();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void action() {
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/

    private void setViewRyc() {
        listImage = GetAllPhotoFromGallery.getAllImageFromGallery(this);
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this);
        imageAdapter.setListTransInterface(this);
        imageAdapter.setData(listImage);
        rycAddAlbum.setLayoutManager(new GridLayoutManager(this, 4));
        rycAddAlbum.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        edtTitleAlbum = findViewById(R.id.edtTitleAlbum);
        rycAddAlbum = findViewById(R.id.rycAddAlbum);
    }

    @Override
    public void addList(MyImage img) {
        listImageSelected.add(img);
    }
    public void removeList(MyImage img) {
        listImageSelected.remove(img);
    }
    public class CreateAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String albumName = edtTitleAlbum.getText().toString();
            String albumPath = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + albumName;
            File directtory = new File(albumPath);
            Log.e("Path add Album:", albumPath);
            if (!directtory.exists()) {
                directtory.mkdirs();
                Log.e("File-no-exist", directtory.getPath());
            }
            String[] paths = new String[listImageSelected.size()];
            int i = 0;
//            Set<String> imageListFavor = DataLocalManager.getListSet();
            for (MyImage img : listImageSelected) {
                File imgFile = new File(img.getPath());
                File desImgFile = new File(albumPath, albumName + "_" + imgFile.getName());
                try {
                    copyFile(imgFile, desImgFile);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
//                paths[i] = desImgFile.getPath();
//                i++;
//                for (String imgFavor: imageListFavor){
//                    if(imgFavor.equals(imgFile.getPath())){
//                        imageListFavor.remove(imgFile.getPath());
//                        imageListFavor.add(desImgFile.getPath());
//                        break;
//                    }
//                }
//            }
//            DataLocalManager.setListImg(imageListFavor);
//            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            }
            return null;

        }
        private void copyFile(File sourceFile, File destFile) throws IOException {
            if (!sourceFile.exists()) {
                return;
            }

            FileChannel source = null;
            FileChannel destination = null;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }


        }



        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
