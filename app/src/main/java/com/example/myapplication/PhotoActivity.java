package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotoActivity extends Activity {
    private Context context;

    ImageButton btnShare, btnFavorite, btnInfo, btnDelete;
    Button btnEdit, button_clickback;
    Photos photoCur;
    TextView photoDateCreate, photoLocate;
    ViewPager viewPager;
    Integer position = 0;
    ArrayList<Photos> arrayList = new ArrayList<>();
    BasicFileAttributes attr = null;
    String ext = "";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_item_page);
        assignViewByFindId();
        position = (Integer) getIntent().getExtras().get("position");

        if(position == -1){
            photoCur = (Photos) getIntent().getExtras().get("photo");
            arrayList.add(photoCur);
            position = 0;
        }
        else{
            GetAllPhotos();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ROOT);
        photoCur =  arrayList.get(position);
        try {
            setLocate_Time();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = photoCur.getPath();
        ext = path.substring(path.lastIndexOf('.'));

        ImageAdapter adapter = new ImageAdapter(this, arrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                photoCur = arrayList.get(position);
                try {
                    setLocate_Time();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        button_clickback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PhotoActivity.this,EditPhoto.class);
                intent.putExtra("ImageUri", photoCur.getPath());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String image_path = photoCur.getPath();
                File file = new File(image_path);
                Uri imageUri = FileProvider.getUriForFile(
                        getApplicationContext(),
                        BuildConfig.APPLICATION_ID +".provider", //(use your app signature + ".provider" )
                        file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION );
                startActivity(intent);
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do something
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    opendialog_Info(photoCur, sdf, sdf.format(attr.creationTime().toMillis()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opendialog_confirm_delete();
            }
        });

    }

    private void assignViewByFindId(){
        viewPager = findViewById(R.id.viewPager);
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        button_clickback = (Button) findViewById(R.id.button_clickback);
        photoDateCreate = (TextView) findViewById(R.id.photoDateCreate);
        photoLocate = (TextView) findViewById(R.id.locate);
    }


    private void opendialog_Info(Photos photo, SimpleDateFormat sdf, String last_Access) throws IOException {
        File current_file = new File(photo.getPath());
        Log.e("onClick: ", current_file.getName());

        Uri imageUri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID +".provider", //(use your app signature + ".provider" )
                current_file);
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(imageUri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        ExifInterface exifInterface = new ExifInterface(fileDescriptor);

        long fileSizeNumber = Math.round(current_file.length() * 1.0 / 1000);
        String fileSizeResult;
        if (fileSizeNumber > 2000)
            fileSizeResult = String.format(Locale.ROOT, "%.2f MB", fileSizeNumber * 1.0 / 1000);
        else
            fileSizeResult = String.format(Locale.ROOT, "%d KB", fileSizeNumber);
        String general_info = (" FILE_PATH: " + current_file.getAbsolutePath());
        general_info += "\n LAST_MODIFY: " + sdf.format(current_file.lastModified());
        general_info += "\n SIZE: " + fileSizeResult;
        String exif = " IMAGE_LENGTH: " +
                exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        exif += "\n IMAGE_WIDTH: " +
                exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        exif += "\n DATETIME: " +
                exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        exif += "\n TAG_MAKE: " +
                exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        exif += "\n TAG_MODEL: " +
                exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        exif += "\n TAG_ORIENTATION: " +
                exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        exif += "\n TAG_WHITE_BALANCE: " +
                exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        exif += "\n TAG_FOCAL_LENGTH: " +
                exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        exif += "\n TAG_FLASH: " +
                exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        exif += "\n GPS related:";
        exif += "\n TAG_GPS_DATESTAMP: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
        exif += "\n TAG_GPS_TIMESTAMP: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
        exif += "\n TAG_GPS_LATITUDE: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        exif += "\n TAG_GPS_LATITUDE_REF: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        exif += "\n TAG_GPS_LONGITUDE: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        exif += "\n TAG_GPS_LONGITUDE_REF: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        exif += "\n TAG_GPS_PROCESSING_METHOD: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

        exif += "\n TAG_GPS_LATITUDE: " +
                exifInterface.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION);

        parcelFileDescriptor.close();

        View photo_InfoView = LayoutInflater.from(this).inflate(R.layout.photo_info, null);
        EditText filename = photo_InfoView.findViewById(R.id.info_filename);
        TextView text_info = photo_InfoView.findViewById(R.id.info_img);
        TextView info_general = photo_InfoView.findViewById(R.id.info_general);
        Button btnEditInfo = photo_InfoView.findViewById(R.id.btnEditInfo);

        filename.setText(current_file.getName());
        info_general.setText(general_info);
        text_info.setText(exif);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(photo_InfoView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName = current_file.getName();

                if(btnEditInfo.getText() == "Save"){
                    String image_path = photoCur.getPath();
                    String pathDirectory = image_path.substring(0, image_path.lastIndexOf('/')+1);
                    File directory = new File(pathDirectory);
                    File file = new File(directory, oldName);
                    File fileNew = new File(directory, filename.getText() + ext);

                    boolean renamed = file.renameTo(fileNew);
                    if (renamed) {
                        photoCur = new Photos(fileNew.getPath());
                        arrayList.set(position, photoCur);
                        Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getBaseContext(), "Fail!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    return;
                }
                filename.setText(oldName.substring(0, oldName.lastIndexOf('.')));
                filename.setBackgroundResource(R.drawable.background_edittext);
                filename.setEnabled(true);
                filename.requestFocus();
                filename.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!(filename.getText()+ext).equals(oldName)){
                            btnEditInfo.setText("Save");
                        }
                        else {
                            btnEditInfo.setText("Edit");
                        }
                    }
                });
            }
        });
        String image_path = photoCur.getPath();
        File file = new File(image_path);
        dialog.show();

    }

    public void opendialog_confirm_delete(){
        View delete_comfirmView = LayoutInflater.from(this).inflate(R.layout.comfirm_delete, null);
        Button btn_no_dialog = delete_comfirmView.findViewById(R.id.btn_no_dialog);
        Button btn_yes_dialog = delete_comfirmView.findViewById(R.id.btn_yes_dialog);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(delete_comfirmView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_no_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_yes_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image_path = photoCur.getPath();
                File file = new File(image_path);

                Uri imageUri = FileProvider.getUriForFile(
                        getApplicationContext(),
                        BuildConfig.APPLICATION_ID +".provider", //(use your app signature + ".provider" )
                        file);
                if(getContentResolver().delete(imageUri, null, null) == 1){
                    Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), "Fail!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    public void GetAllPhotos(){
        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.e("getImages: ", images.toString());

        Cursor cur = getApplicationContext().getContentResolver().query(images,
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

    public void setLocate_Time() throws IOException {
        Uri imageUri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID +".provider", //(use your app signature + ".provider" )
                new File(photoCur.getPath()));

        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(imageUri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        ExifInterface exifInterface = new ExifInterface(fileDescriptor);
        String str_latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String str_longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if(str_latitude == null || str_longitude == null){
            photoLocate.setText("Location?");
        }
        else {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(
                    ConvertStringToLat(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)),
                    ConvertStringToLat(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)),
                    1);
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            photoLocate.setText(state+", "+country);
        }
        parcelFileDescriptor.close();
        try {
            attr = Files.readAttributes(Paths.get(photoCur.getPath()), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
        photoDateCreate.setText(sdf_date.format(attr.creationTime().toMillis()));
    }
    public double ConvertStringToLat(String str){ //ex: "12/1,10/1,20012/2012" => 12.16943
        Log.e("ConvertStringToLat: ", str+ " 123");
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
}
