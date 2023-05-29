package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FlipFragment extends Fragment {
    static FlipFragment flipFragment;
    Context context;
    ImageButton fliphorBtn,flipverBtn,btnClear, btnCheck;
    EditImageView editImageView;

    public static FlipFragment getInstance(Context context, EditImageView editImageView) {
        flipFragment =  new FlipFragment(context, editImageView);
        return flipFragment;
    }

    private FlipFragment(Context context, EditImageView editImageView) {
        this.context = context;
        this.editImageView = editImageView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View flipFragment = inflater.inflate(R.layout.flip_fragment, container, false);
        flipverBtn = (ImageButton) flipFragment.findViewById(R.id.flip_vertical_Btn);
        fliphorBtn = (ImageButton) flipFragment.findViewById(R.id.flip_hor_Btn);
        btnCheck = (ImageButton) flipFragment.findViewById(R.id.btnCheck);

        fliphorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageView.flip_hor();
            }
        });

        flipverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageView.flip_ver();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditPhoto) context).onMsgFromFragToEdit("FLIP", "CHECK", null);
            }
        });
        return flipFragment;
    }
}
