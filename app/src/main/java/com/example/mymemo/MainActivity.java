package com.example.mymemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public RecyclerView main_recyclerView;
    private List<MemoListInfo> memo_item = new ArrayList<>();
    private final MainMemoListAdapter mainAdapter = new MainMemoListAdapter(MainActivity.this, memo_item);
    static public ArrayList<String> fileNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //그리드 레이아웃으로 한줄에 2개씩 보여주기
        main_recyclerView = findViewById(R.id.main_memo_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        main_recyclerView.setHasFixedSize(true);
        main_recyclerView.setLayoutManager(layoutManager);
        main_recyclerView.setAdapter(mainAdapter);

        FloatingActionButton addBtn = findViewById(R.id.main_add_floatingButton);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMemoActivity.class));
            }
        });

        fileNames = getStringArrayPref(MainActivity.this, "fileName");
        // 이전 액티비티에서 새로운 메모를 저장했다면 파일 이름 리스트 갱신
        if(getIntent() != null) {
            String newMemoFile = getIntent().getStringExtra("fileName");
            if(!TextUtils.isEmpty(newMemoFile)){
                fileNames.add(newMemoFile);
                setStringArrayPref(MainActivity.this, "fileName", fileNames);
            }
        }

        memo_item.clear();
        String title;
        String contents;
        boolean hasImage;
        FileInputStream fis;

        for (int i=0; i<fileNames.size(); i++){
            StringBuilder contentsBuilder = new StringBuilder();
            hasImage = false;
            ArrayList<String> imgs = new ArrayList<>();
            try {
                String name = fileNames.get(i);
                fis = openFileInput(name);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                while (true){
                    title = br.readLine();
                    if(title.equals("image#")){
                        hasImage = true;
                        imgs.add(br.readLine());
                    }else {
                        break;
                    }
                }
                contents = br.readLine();
                while (contents != null){
                    contentsBuilder.append(contents).append("\n");
                    contents = br.readLine();
                }
                br.close();
                fis.close();
                if(hasImage) memo_item.add(new MemoListInfo(title, contentsBuilder.toString(), imgs.get(0), imgs, name));
                else memo_item.add(new MemoListInfo(title, contentsBuilder.toString(), name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mainAdapter.notifyDataSetChanged();

    }

    // SharedPreference 에 json 으로 ArrayList 저장
    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    // SharedPreference 에서 json 으로 저장된 값을 ArrayList 로 얻어오기
    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> fileNames = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String name = a.optString(i);
                    fileNames.add(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fileNames;
    }
}
