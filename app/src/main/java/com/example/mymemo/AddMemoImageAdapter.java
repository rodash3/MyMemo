package com.example.mymemo;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

public class AddMemoImageAdapter extends RecyclerView.Adapter<AddMemoImageAdapter.ViewHolder> {
    private Context context;
    private List<String> items; // 이미지의 경로 저장

    AddMemoImageAdapter(Context context, List<String> items) {
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

        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(20))
                .placeholder(R.drawable.loading) // 로딩 이미지
                .error(R.drawable.fail_to_load_image); // 로딩 실패 이미지
        // 이미지 경로를 통해 로드하여 이미지뷰에 세팅
        Glide.with(context)
                .load(path)
                .apply(options)
                .into(holder.img);
        // @see https://github.com/bumptech/glide
        // Glide 라이브러리 사용

        // 삭제 버튼 누르면 다이얼로그 띄워서 삭제 의사 묻기
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("사진을 삭제합니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // 카메라 촬영인 경우 내부에 저장된 사진 삭제
                        if(path.contains("com.example.mymemo")) {
                            File file = new File(path);
                            if (file.exists()) {
                                file.delete();
                            }
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
        ImageView img; // 유저가 업로드한 이미지 나타내는 이미지뷰
        Button btn; // 업로드한 사진 삭제 버튼

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.add_memo_item_imageView);
            btn = itemView.findViewById(R.id.add_memo_item_delBtn);
        }
    }

}
