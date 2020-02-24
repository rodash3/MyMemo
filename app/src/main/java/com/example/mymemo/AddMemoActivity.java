package com.example.mymemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMemoActivity extends AppCompatActivity {

    public RecyclerView add_img_recyclerView;
    private List<String> img_item = new ArrayList<>();
    private AddMemoImageAdapter imgAdapter = new AddMemoImageAdapter(AddMemoActivity.this, img_item);
    private String CTAG = "Camera Permission-";
    private String currentImagePath; // 카메라 촬영시 저장되는 경로
    static int CAMERA_IMAGE = 100;
    static int GALLERY_IMAGE = 200;
    static boolean isEdit; // 편집으로 액티비티 들어온건지 아닌지
    private AlertDialog alert; // 사진선택 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        final EditText eTitle = findViewById(R.id.add_memo_title);
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

        isEdit = false;
        // 기존 메모를 수정하러 온 목적
        if(getIntent().getStringExtra("fileName") != null){
            isEdit = true;
            // 인텐트에서 넘어온 값으로 세팅
            if(getIntent().getStringArrayListExtra("images")!=null){
                img_item.addAll(getIntent().getStringArrayListExtra("images"));
            }
            imgAdapter.notifyDataSetChanged();
            eTitle.setText(getIntent().getStringExtra("title"));
            eContents.setText(getIntent().getStringExtra("contents"));
        }

        // 사진 추가 버튼 클릭
        imgAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라 권한 요청
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // 권한 허용되었을 때 사진 선택 다이얼로그 실행
                        photoDialogRadio();
                        Log.d(CTAG, "권한 설정 완료");
                    } else {
                        Log.d(CTAG, "권한 설정 요청");
                        ActivityCompat.requestPermissions(AddMemoActivity.this, new String[]{
                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            }
        });

        // 저장 버튼 클릭하면 파일 형태로 메모 저장
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eTitle.getText().toString() + "\n";
                String contents = eContents.getText().toString();
                String fileName;
                if(isEdit){ // 편집이면 파일명 동일, 새로 파일 만들지 않음
                    fileName = getIntent().getStringExtra("fileName");
                }else { // 저장시 시간을 기록하여 파일명으로 지정
                    SimpleDateFormat nameFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN);
                    fileName = nameFormat.format(new Date()) + ".txt";
                }

                FileOutputStream fos;
                try {
                    fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                    for (int i = 0; i < img_item.size(); i++) {
                        // 이미지 업로드한 경우 image 태그를 붙여 텍스트와 구별
                        bw.write("image#\n");
                        bw.write(img_item.get(i));
                        bw.write("\n");
                    }
                    bw.write(title);
                    bw.write(contents);
                    bw.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 저장이 끝나면 다시 메인화면으로 이동
                Intent intent = new Intent(AddMemoActivity.this, MainActivity.class);
                if(!isEdit)
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
        alt_bld.setTitle("사진 추가");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) { // 카메라로 사진 찍기
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File imageFile = null;
                        try {
                            // 이미지 저장 경로 및 임시 파일 생성
                            imageFile = createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if (imageFile != null) {
                            // 정상적으로 생성되면 카메라 인텐트 넘기며 onActivityResult 실행
                            Uri photoURI = FileProvider.getUriForFile(AddMemoActivity.this,
                                    "com.example.mymemo.provider", imageFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(cameraIntent, CAMERA_IMAGE);
                        }
                    }
                } else if (item == 1) { // 갤러리에서
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_IMAGE);
                } else if (item == 2) { // 외부 URL
                    // URL 을 입력할 editText가 포함된 다이얼로그 생성
                    final EditText et = new EditText(AddMemoActivity.this);
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(AddMemoActivity.this);
                    alt_bld.setTitle("URL 입력")
                            .setView(et)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String value = et.getText().toString();
                                    // check url validation
                                    if (URLUtil.isValidUrl(value)) {
                                        img_item.add(value);
                                        imgAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(AddMemoActivity.this,
                                                "URL이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    alert.dismiss();
                                    // 추가한 이미지로 포커싱
                                    add_img_recyclerView.scrollToPosition(imgAdapter.getItemCount()-1);
                                }
                            });
                    AlertDialog alert2 = alt_bld.create();
                    alert2.show();
                }
            }
        });
        alert = alt_bld.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_IMAGE) {  // 카메라에서 찍은 이미지
                try {
                    File file = new File(currentImagePath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) { // 경로에 이미지가 존재할 때 경로 저장
                        img_item.add(currentImagePath);
                        imgAdapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_IMAGE && data != null) { // 갤러리에서 가져온 이미지
                try {
                    // 앨범 사진이 위치한 경로 저장
                    img_item.add(getPath(AddMemoActivity.this, data.getData()));
                    imgAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 추가한 이미지로 포커싱
        add_img_recyclerView.scrollToPosition(imgAdapter.getItemCount()-1);
        alert.dismiss();
    }

    public File createImageFile() throws IOException {
        // 현재 시간으로 임시 사진 파일 생성
        String imageFileName = System.currentTimeMillis() + "";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isEdit) {
            // 카메라 촬영 후 메모를 저장하지 않을 때 사진 파일 삭제
            for (String path : img_item) {
                if (path.contains("com.example.mymemo")) {
                    File file = new File(path);
                    // 파일이 존재하는 경우 삭제
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    // 갤러리에서 이미지를 가져왔을때 Uri 를 통해 실제 이미지 파일의 경로 알아내기
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}