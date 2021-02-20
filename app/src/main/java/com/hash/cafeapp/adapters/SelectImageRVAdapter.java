package com.hash.cafeapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hash.cafeapp.ImageData;
import com.hash.cafeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectImageRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ImageData> data;
    private Context context;
    private ImageView selectedImageView;
    private Dialog dialog;

    public SelectImageRVAdapter(ArrayList<ImageData> data, Dialog dialog, ImageView selectedImageView, Context context) {
        this.data = data;
        this.context = context;
        this.selectedImageView = selectedImageView;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_select_image, parent, false);
        return new SelectImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Picasso.with(context).load(data.get(position).getUrl()).fit().centerCrop()
                .placeholder(R.drawable.ic_outline_blur_circular_24px)
                .error(R.drawable.ic_outline_error_outline_24px)
                .into(((SelectImageViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SelectImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        SelectImageViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.selectImageIV);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Picasso.with(context).load(data.get(getLayoutPosition()).getUrl()).fit()
                            .centerCrop().placeholder(R.drawable.rounded_rectangle)
                            .error(R.drawable.ic_outline_error_outline_24px)
                            .into(selectedImageView);
                    selectedImageView.setVisibility(View.VISIBLE);
                    selectedImageView.setTag(data.get(getLayoutPosition()).getUrl());
                    dialog.dismiss();
                }
            });
        }
    }

}
