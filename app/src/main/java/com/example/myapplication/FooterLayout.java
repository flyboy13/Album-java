package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

public class FooterLayout extends Fragment implements FragmentCallbacks {
    MainActivity main;
    Context context;
    Button btnPhotos, btnAlbums, btnSearch;
    int position = 1;

    public static FooterLayout newInstance(){
        FooterLayout fragment = new FooterLayout();
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

        LinearLayout layout_footer = (LinearLayout) inflater.inflate(R.layout.footer_layout, null);

        assignViewByFindId(layout_footer);

        btnPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultState(position);
                main.onMsgFromFragToMain("Footer_Layout", "All_Photo_Layout");
                selectState(btnPhotos);
            }
        });

        btnAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultState(position);
                main.onMsgFromFragToMain("Footer_Layout", "Home_Layout");
                selectState(btnAlbums);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultState(position);
                main.onMsgFromFragToMain("Footer_Layout", "Search_Layout");
                selectState(btnSearch);
            }
        });
        return layout_footer;
    }

    private void assignViewByFindId(LinearLayout layout){
        btnPhotos = (Button)  layout.findViewById(R.id.btnPhotos);
        btnAlbums = (Button) layout.findViewById(R.id.btnAlbum);
        btnSearch = (Button) layout.findViewById(R.id.btnSearch);
    }

    private void defaultState(int position){
        Button btn;
        if (position == 0){
            btn = btnPhotos;
        }
        else if (position == 1){
            btn = btnAlbums;
        }
        else {
            btn = btnSearch;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#666666")));
        }
        btn.setTextColor(Color.parseColor("#666666"));
    }

    private void selectState(Button btn){
        if (btn.equals(btnPhotos)){
            position = 0;
        }
        else if (btn.equals(btnAlbums)){
            position = 1;
        }
        else if (btn.equals(btnSearch)){
            position = 2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#0066ff")));
        }
        btn.setTextColor(Color.parseColor("#0066ff"));
    }

    @Override
    public void onMsgFromMainToFragment(String btn) {

    }
}