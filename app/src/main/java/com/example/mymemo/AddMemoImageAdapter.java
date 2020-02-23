package com.example.mymemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddMemoImageAdapter extends RecyclerView.Adapter<AddMemoImageAdapter.ViewHolder> {
    private Context context;
    private List<String> items;

    public AddMemoImageAdapter(Context context, List<String> items) {
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
    public void onBindViewHolder(@NonNull AddMemoImageAdapter.ViewHolder holder, final int position) {
        final String path = items.get(position);

        Glide.with(context)
                .load(path)
                .into(holder.img);
        // @see https://github.com/bumptech/glide
        // Glide 라이브러리 사용

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 띄워서 삭제 의사 묻기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("사진을 삭제합니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // 카메라 촬영인 경우 내부에 저장된 사진 삭제
                        if(path.contains("com.example.mymemo")) {
                            File file = new File(path);
                            if (file.exists())
                                file.delete();
                        }
                        // 아이템 리스트에서 제거
                        items.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        Button btn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.add_memo_item_imageView);
            btn = itemView.findViewById(R.id.add_memo_item_delBtn);
        }
    }

}
