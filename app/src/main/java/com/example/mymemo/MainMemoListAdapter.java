package com.example.mymemo;

import android.annotation.SuppressLint;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

public class MainMemoListAdapter extends RecyclerView.Adapter<MainMemoListAdapter.ViewHolder> {

    private Context context;
    private List<MemoListInfo> items; // 메모에 저장되는 모든 값들이 담김

    MainMemoListAdapter(Context context, List<MemoListInfo> items) {
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

        // 텍스트 세팅
        holder.title.setText(item.getTitle());
        holder.contents.setText(item.getContents());
        String date = item.getFileName();
        String dateFormat = String.format("%s.%s.%s", date.substring(2, 4), date.substring(4, 6), date.substring(6, 8));
        holder.date.setText(dateFormat);
        // 메모에 사진이 있을 경우 프리뷰 이미지 나타냄
        if(!TextUtils.isEmpty(item.getPreviewImg())){
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(30));

            holder.preview_img.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getPreviewImg())
                    .apply(options)
                    .into(holder.preview_img);
            // @see https://github.com/bumptech/glide
            // Glide 라이브러리 사용
        }else {
            // 메모에 사진이 없으면 이미지뷰 보이지 않음
            holder.preview_img.setVisibility(View.GONE);
        }

        // 메모 클릭시 상세보기로 이동
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

        // 삭제 버튼 누르면 다이얼로그 띄워서 삭제 의사 묻기
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("메모를 삭제합니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sharedPreference 에서 파일 이름 삭제
                        MainActivity.fileNames.remove(item.getFileName());
                        MainActivity.setStringArrayPref(context, "fileName",MainActivity.fileNames);
                        // 내부 저장소의 파일 삭제
                        @SuppressLint("SdCardPath")
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;  // 제목
        TextView contents;  // 내용
        ImageView preview_img; // 썸네일
        ImageButton deleteBtn; // 메모 삭제 버튼
        TextView date;  // 메모 생성 날짜

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.main_memo_list_title);
            contents = itemView.findViewById(R.id.main_memo_list_contents);
            preview_img = itemView.findViewById(R.id.main_memo_list_preview_img);
            deleteBtn = itemView.findViewById(R.id.main_memo_list_delBtn);
            date = itemView.findViewById(R.id.main_memo_date);
        }
    }
}
