package com.example.mymemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewMemoActivity extends AppCompatActivity {

    public RecyclerView view_img_recyclerView;
    private List<String> img_item = new ArrayList<>();
    private String title, contents;
    private String time; // equals to FileName
    private  ArrayList<String> images;
    private final ViewMemoImageAdapter vAdapter = new ViewMemoImageAdapter(ViewMemoActivity.this, img_item);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memo);

        // use Toolbar
        Toolbar toolbar = findViewById(R.id.view_memo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleView = findViewById(R.id.view_memo_title);
        TextView contentsView = findViewById(R.id.view_memo_contents);
        TextView timeView = findViewById(R.id.view_memo_time_stamp);

        // 리사이클러뷰 레이아웃 설정
        view_img_recyclerView = findViewById(R.id.view_memo_image_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ViewMemoActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        view_img_recyclerView.setHasFixedSize(true);
        view_img_recyclerView.setLayoutManager(layoutManager);
        view_img_recyclerView.setAdapter(vAdapter);

        title = getIntent().getStringExtra("title");
        contents = getIntent().getStringExtra("contents");
        time = getIntent().getStringExtra("time");
        images = getIntent().getStringArrayListExtra("images");

        titleView.setText(title);
        contentsView.setText(contents);
        String timeFormat = String.format("%s.%s.%s. %s:%s:%s", time.substring(0, 4), time.substring(4, 6),
                time.substring(6, 8), time.substring(8, 10), time.substring(10, 12), time.substring(12, 14));
        timeView.setText(timeFormat);

        if (getIntent().getStringArrayListExtra("images") != null) {
            img_item.addAll(images);
            vAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(ViewMemoActivity.this, AddMemoActivity.class);
                intent.putExtra("title", title)
                        .putExtra("contents", contents)
                        .putExtra("fileName", time)
                        .putStringArrayListExtra("images", images);
                startActivity(intent);
                break;
            case R.id.menu_delete:
                deleteMemo();
                break;
        }
        return true;
    }

    public void deleteMemo() {
        // 다이얼로그 띄워서 삭제 의사 묻기
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewMemoActivity.this);
        builder.setTitle("메모를 삭제합니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sharedPreference 에서 파일 이름 삭제
                MainActivity.fileNames.remove(time);
                MainActivity.setStringArrayPref(getBaseContext(), "fileName", MainActivity.fileNames);
                // 내부 저장소의 파일 삭제
                File file = new File("/data/data/com.example.mymemo/files/" + time);
                if (file.exists())
                    file.delete();
                // 사진 파일도 있다면 삭제
                if (getIntent().getStringArrayListExtra("images") != null) {
                    for(String path : images){
                        if(path.contains("com.example.mymemo")){
                            File f = new File(path);
                            if(f.exists())
                                f.delete();
                        }
                    }
                }
                Toast.makeText(ViewMemoActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewMemoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d("위치?", getApplicationContext().getFilesDir().getPath());
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
