package com.example.myapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Apdapter.AlbumAdapter;
import com.example.myapplication.Apdapter.GridApdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.myapplication.Apdapter.AlbumsAdapter;

public class AllAlbumLayout extends Fragment implements FragmentCallbacks {
    MainActivity main;
    Context context;
    Button btnSelect;
    ListView  gridAlbum;
    //=============
    ListView ItemLV;

    private FloatingActionButton btnAdd;
    private RecyclerView albumRecView;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<String> albums;
    private List<MyImage> listImage;
    private AlbumAdapter albumAdapter;



    //===============

    public static AllAlbumLayout newInstance(){
        AllAlbumLayout fragment = new AllAlbumLayout();
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
    public ArrayList<String> getAlbums() {
        return AlbumUtility.getInstance(context).getAllAlbums();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        LinearLayout All_Album_layout = (LinearLayout) inflater.inflate(R.layout.all_album_layout, null);
        assignViewByFindId(All_Album_layout);
        //Tài

//        Iterator<String> iter = albums.iterator();
//        while (iter.hasNext()) {
//            String album = iter.next();
//            if (album.equals("Favorite")||album.equals("Trashed")||album.equals("Hide")) {
//                iter.remove();
//            }
//        }
        albums = AlbumUtility.getInstance(context).getAllAlbums();

        albumRecView = All_Album_layout.findViewById(R.id.albumsRecView);
        albumsAdapter = new AlbumsAdapter(context, albums);
        //Test album
        listImage = GetAllPhotoFromGallery.getAllImageFromGallery(this.getContext());
//        albumAdapter = new AlbumsAdapter(listImage);

        albumRecView.setAdapter(albumsAdapter);
        albumRecView.setLayoutManager(new LinearLayoutManager(All_Album_layout.getContext()));
        btnAdd = (FloatingActionButton) All_Album_layout.findViewById(R.id.btnAdd_AlbumsFragment);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAlbum();
            }
        });


//        btnSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Do something
//            }
//        });


        return All_Album_layout;
    }

    private void assignViewByFindId(LinearLayout layout){
//        gridAlbum = (GridView) layout.findViewById(R.id.gridAlbum);


    }
    @NonNull
    private List<Album> getListAlbum(List<MyImage> listImage) {
        List<String> ref = new ArrayList<>();
        List<Album> listAlbum = new ArrayList<>();

        for (int i = 0; i < listImage.size(); i++) {
            String[] _array = listImage.get(i).getThumb().split("/");
            String _pathFolder = listImage.get(i).getThumb().substring(0, listImage.get(i).getThumb().lastIndexOf("/"));
            String _name = _array[_array.length - 2];
            if (!ref.contains(_pathFolder)) {
                ref.add(_pathFolder);
                Album token = new Album(listImage.get(i), _name);
                token.setPathFolder(_pathFolder);
                token.addItem(listImage.get(i));
                listAlbum.add(token);
            } else {
                listAlbum.get(ref.indexOf(_pathFolder)).addItem(listImage.get(i));
            }
        }
        return listAlbum;
    }//Get List Album
    private void addNewAlbum() {
        View addNewAlbumForm = LayoutInflater.from(context).inflate(R.layout.add_album_form, null);
        EditText edtAlbumName = addNewAlbumForm.findViewById(R.id.edtAlbumName);

        AlertDialog.Builder addDialog = new AlertDialog.Builder(context, R.style.AlertDialog);
        addDialog.setView(addNewAlbumForm);
        addDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAlbumName = edtAlbumName.getText().toString();
                if (newAlbumName.length() != 0) {
                    if (AlbumUtility.getInstance(context).addNewAlbum(newAlbumName)) {
                        albumsAdapter.addAlbum(newAlbumName);
                        Toast.makeText(context, "New album created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Error: Failed to create new album!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Empty Album Name!", Toast.LENGTH_LONG).show();
                }
            }
        });
        addDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        addDialog.create();
        addDialog.show();
    }

    @Override
    public void onMsgFromMainToFragment(String btn) {

    }
}