package com.example.mymemo;

import android.content.Context;
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
    public void onBindViewHolder(@NonNull AddMemoImageAdapter.ViewHolder holder, int position) {
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
        Button btn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.add_memo_item_imageView);
            btn = itemView.findViewById(R.id.add_memo_item_delBtn);
        }
    }
//
//
//    /* * String형을 BitMap으로 변환시켜주는 함수 * */
//    public static Bitmap StringToBitmap(String encodedString) {
//        try {
//            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
}
