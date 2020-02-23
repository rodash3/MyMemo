package com.example.mymemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class MainMemoListAdapter extends RecyclerView.Adapter<MainMemoListAdapter.ViewHolder> {

    Context context;
    List<MemoListInfo> items;

    public MainMemoListAdapter(Context context, List<MemoListInfo> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MainMemoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_memolist_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainMemoListAdapter.ViewHolder holder, final int position) {
        final MemoListInfo item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.contents.setText(item.getContents());
        // 사진이 있을 경우 URL 으로 프리뷰 이미지 나타냄
        if(!TextUtils.isEmpty(item.getPreviewImg())){
            holder.preview_img.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getPreviewImg())
                    .into(holder.preview_img);
        }else {
            holder.preview_img.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewMemoActivity.class);
                intent.putExtra("title" ,item.getTitle());
                intent.putExtra("contents", item.getContents());
                intent.putExtra("images", item.getImgs());
                intent.putExtra("time", item.getFileName());
                context.startActivity(intent);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // 다이얼로그 띄워서 삭제 의사 묻기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("메모를 삭제합니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sharedPreference 에서 파일 이름 삭제
                        MainActivity.fileNames.remove(item.getFileName());
                        MainActivity.setStringArrayPref(context, "fileName",MainActivity.fileNames);
                        // 내부 저장소의 파일 삭제
                        File file = new File("/data/data/com.example.mymemo/files/"+item.getFileName());
                        if(file.exists())
                            file.delete();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView contents;
        ImageView preview_img;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.main_memo_list_title);
            contents = itemView.findViewById(R.id.main_memo_list_contents);
            preview_img = itemView.findViewById(R.id.main_memo_list_preview_img);
            deleteBtn = itemView.findViewById(R.id.main_memo_list_delBtn);
        }
    }
}
