package com.example.mymemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddMemoActivity extends AppCompatActivity {

    public RecyclerView add_img_recyclerView;
    private List<Bitmap> img_item = new ArrayList<>();
    private final AddMemoImageAdapter imgAdapter = new AddMemoImageAdapter(AddMemoActivity.this, img_item);
    private String CTAG = "Camera Permission-";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        final EditText eTitle= findViewById(R.id.add_memo_title);
        final EditText eContents = findViewById(R.id.add_memo_contents);
        Button saveBtn = findViewById(R.id.add_memo_saveBtn);
        ImageButton imgAddBtn = findViewById(R.id.add_memo_image_addBtn);

        // 리사이클러뷰 레이아웃 설정
        add_img_recyclerView = findViewById(R.id.add_memo_image_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(AddMemoActivity.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        add_img_recyclerView.setHasFixedSize(true);
        add_img_recyclerView.setLayoutManager(layoutManager);
        add_img_recyclerView.setAdapter(imgAdapter);

        // 사진 추가 버튼 클릭
        imgAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라 권한 요청
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(CTAG, "권한 설정 완료");
                    } else {
                        Log.d(CTAG, "권한 설정 요청");
                        ActivityCompat.requestPermissions(AddMemoActivity.this, new String[]{
                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

                photoDialogRadio();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eTitle.getText().toString() + "\n";
                String contents = eContents.getText().toString();
                SimpleDateFormat nameFormat = new SimpleDateFormat( "yyyyMMddHHmmss");
                String fileName = nameFormat.format(new Date()) + ".txt";

                FileOutputStream fos;
                try {
                    fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                    fos.write(title.getBytes());
                    fos.write(contents.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(AddMemoActivity.this, MainActivity.class);
                intent.putExtra("fileName", fileName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    // 카메라 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(CTAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(CTAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    // 사진 가져오기 다이얼로그
    private void photoDialogRadio() {
        final CharSequence[] PhotoModels = {"카메라로 촬영하기", "갤러리에서 가져오기", "이미지 URL 입력하기"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("사진 추가");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) { // 카메라로 사진 찍기

                } else if (item == 1) { // 갤러리에서

                } else if(item == 2) { // 외부 링크

                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

}
