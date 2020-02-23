package com.example.mymemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddMemoImageAdapter extends RecyclerView.Adapter<AddMemoImageAdapter.ViewHolder> {
    Context context;
    List<Bitmap> items;

    public AddMemoImageAdapter(Context context, List<Bitmap> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AddMemoImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_memo_image_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddMemoImageAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = items.get(position);

        holder.img.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.add_memo_item_imageView);
            btn = itemView.findViewById(R.id.add_memo_item_delBtn);
        }
    }
}
