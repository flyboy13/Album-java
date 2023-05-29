package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {

    private List<Tool> toolItemList;
    private Context context;
    private EditImageView editImageView, tempeIV;
    private EditPhoto activity;

    public ToolAdapter(List<Tool> toolItemList, Context context, EditImageView editImageView) {
        this.toolItemList = toolItemList;
        this.context = context;
        this.editImageView = editImageView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tool_item, parent, false);
        activity = (EditPhoto) context;
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.btnLogoTool.setImageResource(toolItemList.get(position).getIconID());
        holder.txtNameTool.setText(toolItemList.get(position).getName());

        holder.btnLogoTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toolItemList.get(holder.getAdapterPosition()).getName().equals("Filter")) {
                    activity.inflateFragment(FilterFragment.getInstance(context, editImageView, editImageView.getBitmapResource()));
                }

                if (toolItemList.get(holder.getAdapterPosition()).getName().equals("Brush")) {
                    tempeIV=editImageView;
                    editImageView.setIsBrush(true);
                    activity.inflateFragment(BrushFragment.getInstance(context, editImageView));
                }

                if (toolItemList.get(holder.getAdapterPosition()).getName().equals("Flip")) {
                    activity.inflateFragment(FlipFragment.getInstance(context, editImageView));
                }

                if (toolItemList.get(holder.getAdapterPosition()).getName().equals("Resize")) {
                    activity.inflateFragment(ResizeFragment.getInstance(context, editImageView));
                }

                if (toolItemList.get(holder.getAdapterPosition()).getName().equals("Blur")) {
                    activity.inflateFragment(BlurFragment.getInstance(context, editImageView));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnLogoTool;
        TextView txtNameTool;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnLogoTool = (ImageButton) itemView.findViewById(R.id.btnLogoTool);
            txtNameTool = (TextView) itemView.findViewById(R.id.txtNameTool);
        }
    }
}
