package com.example.myapplication.Apdapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.ImageItem;
import com.example.myapplication.R;


public class GridApdapter extends ArrayAdapter<ImageItem>
{
    private final Context context;
    private final ImageItem[] items;

    public GridApdapter( Context context, int layoutToBeInflated, ImageItem[] items)
    {
        super(context, R.layout.custom_item_list, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_item_list, null);

        TextView txtName = row.findViewById(R.id.txtName);
        ImageView avatar = row.findViewById(R.id.avatar);

        txtName.setText(items[position].getName());
        Log.e("ADebugTag", "============================================================");
        Log.e("ADebugTag", "Item=    " + items[position].getAvatar());
        avatar.setImageResource(items[position].getAvatar());
        return (row);
    }


}
