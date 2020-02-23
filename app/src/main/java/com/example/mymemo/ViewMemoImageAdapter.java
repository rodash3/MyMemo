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

        File file = new File(path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bitmap != null) {
            holder.img.setImageBitmap(bitmap);
        }else if(URLUtil.isValidUrl(path)){
            Glide.with(context)
                    .load(path)
                    .into(holder.img);
        }else {
            Toast.makeText(context,
                    "사진파일이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }
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
