package com.example.mymemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewMemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memo);

        TextView titleView = findViewById(R.id.view_memo_title);
        TextView contentsView = findViewById(R.id.view_memo_contents);
        TextView timeView = findViewById(R.id.view_memo_time_stamp);

        String title = getIntent().getStringExtra("title");
        String contents = getIntent().getStringExtra("contents");
        String time = getIntent().getStringExtra("time");
        ArrayList<String> images = getIntent().getStringArrayListExtra("images");

        titleView.setText(title);
        contentsView.setText(contents);
        String timeFormat = String.format("%s.%s.%s. %s:%s:%s", time.substring(0,4), time.substring(4,6),
                time.substring(6,8), time.substring(8, 10),time.substring(10, 12), time.substring(12, 14));
        timeView.setText(timeFormat);
    }
}
