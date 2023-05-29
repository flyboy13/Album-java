package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResizeFragment extends Fragment {
    static ResizeFragment resizeFragment;
    Context context;
    ImageButton btnClear, btnCheck,btnRe;
    private EditText edtWidth;
    private EditText edtHeight;
    private int scale_Width,scale_Height;
    EditImageView editImageView;

    public static ResizeFragment getInstance(Context context, EditImageView editImageView) {
        resizeFragment =  new ResizeFragment(context, editImageView);
        return resizeFragment;
    }

    private ResizeFragment(Context context, EditImageView editImageView) {
        this.context = context;
        this.editImageView = editImageView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ResizeFragment = inflater.inflate(R.layout.resize_fragment, container, false);
        edtWidth = (EditText) ResizeFragment.findViewById(R.id.edtWidth);
        edtHeight = (EditText) ResizeFragment.findViewById(R.id.edtHeight);
        btnCheck = (ImageButton) ResizeFragment.findViewById(R.id.btnCheckResize);
        btnClear= (ImageButton) ResizeFragment.findViewById(R.id.btnClearResize);
        btnRe = (ImageButton) ResizeFragment.findViewById(R.id.btnRe);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale_Width=Integer.parseInt(edtWidth.getText().toString());
                scale_Height=Integer.parseInt(edtHeight.getText().toString());
                if (editImageView.ISRES())
                    editImageView.rescale();
                editImageView.scaleBitmapResource(scale_Width,scale_Height);
                ((EditPhoto) context).onMsgFromFragToEdit("resize", "CHECK", null);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditPhoto) context).onMsgFromFragToEdit("resize", "CLEAR", null);
            }
        });

        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageView.rescale();
            }
        });

        return ResizeFragment;
    }
}
