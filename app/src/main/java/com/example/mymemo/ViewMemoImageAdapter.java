package com.example.mymemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ViewMemoImageAdapter extends RecyclerView.Adapter<ViewMemoImageAdapter.ViewHolder> {
    private Context context;
    private List<String> items;

    public ViewMemoImageAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewMemoImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_memo_image_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMemoImageAdapter.ViewHolder holder, int position) {
        String path = items.get(position);

        Glide.with(context)
                .load(path)
                .into(holder.img);
        // @see https://github.com/bumptech/glide
        // Glide 라이브러리 사용
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.view_memo_item_imageView);
        }
    }
}
