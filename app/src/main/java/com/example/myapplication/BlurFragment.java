package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

public class BlurFragment extends Fragment {
    Context context;
    EditImageView editImageView;
    static BlurFragment blurFragment;
    RecyclerView BlurColorRecView;
    TextView txtBlurSize;
    Slider sliderBlurSize;
    ImageButton btnCheckBlur,btnClearBlur;
    ColorAdapter adapter;

    String[] colors = {"#000000", "#FFFFFF", "#FF0000", "#8B0000", "#FF6347", "#FFD700", "#B8860B", "#FFFF00", "#006400", "#00FF00", "#98FB98", "#008B8B",
            "#00FFFF", "#1E90FF", "#00008B", "#0000FF", "#8A2BE2", "#7B68EE", "#9400D3", "#FF00FF", "#FF1493", "#FFC0CB", "#FFE4C4", "#8B4513", "#808080"};

    public static BlurFragment getInstance(Context context, EditImageView editImageView) {
        blurFragment = new BlurFragment(context, editImageView);
        return blurFragment;
    }

    private BlurFragment(Context context, EditImageView editImageView) {
        this.context = context;
        this.editImageView = editImageView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blur_fragment, container, false);

        sliderBlurSize = (Slider) view.findViewById(R.id.sliderBlurSize);
        btnCheckBlur = (ImageButton) view.findViewById(R.id.btnCheckBlur);
        btnClearBlur = (ImageButton) view.findViewById(R.id.btnClearBlur);


        btnCheckBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditPhoto) context).onMsgFromFragToEdit("Blur", "CHECK", null);
            }
        });

        btnClearBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageView.rescale();
                ((EditPhoto) context).onMsgFromFragToEdit("Blur", "CHECK", null);
            }

        });



        sliderBlurSize.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                editImageView.fastblur((int) slider.getValue());
            }

        });

        return view;
    }
}
