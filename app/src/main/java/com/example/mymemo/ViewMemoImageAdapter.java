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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ViewMemoImageAdapter extends RecyclerView.Adapter<ViewMemoImageAdapter.ViewHolder> {
    private Context context;
    private List<String> items; // 이미지들 경로

    ViewMemoImageAdapter(Context context, List<String> items) {
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

        // 경로에 있는 파일이 비트맵으로 변환될 수 있으면 정상적이라고 판단
        File file = new File(path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 경로에 파일이 정상적인 경우 or URL 이 valid 한 경우만 이미지 보이기
        if(bitmap != null || URLUtil.isValidUrl(path)){
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(20))
                    .placeholder(R.drawable.loading) // 로딩 이미지
                    .error(R.drawable.fail_to_load_image); // valid 하지만 로딩 실패하였을 경우 이미지

            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .into(holder.img);
            // @see https://github.com/bumptech/glide
            // Glide 라이브러리 사용
        }else {
            // unValid: 사진을 로드할 수 없음
            Toast.makeText(context,
                    " 사진파일이 이동되었거나 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img; // 메모 상세보기의 이미지

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.view_memo_item_imageView);
        }
    }
}
