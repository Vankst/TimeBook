package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timebook.Helper.DatabaseHelper;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class infoLessonTextPage extends AppCompatActivity {


    public static int idLesson;

    public String videoLink;
    public String text;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_lesson_text);

        dbHelper = new DatabaseHelper(this);

        Cursor cursor = dbHelper.getLessonInformation(idLesson);

        if(cursor != null && cursor.moveToFirst()){
            text = cursor.getString(1);
        }

        TextView tvDescription = findViewById(R.id.tvDescription);
        tvDescription.setText(text);

        ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (diff == 0) {
                    endReadLessons();
                    // ScrollView прокручен до конца
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(dbHelper.isEndLessons(idLesson, 1)){
            Button btnText = findViewById(R.id.btnText);
            btnText.setText("Текстовый урок(завершён)");
        }

        if(dbHelper.isEndLessons(idLesson, 2)){
            Button btnVideo = findViewById(R.id.btnVideo);
            btnVideo.setText("Видео урок(завершён)");
        }
    }

    public void toInfoLessonVideoPage(View view){
        infoLessonVideoPage.idLesson = idLesson;
        startActivity(new Intent(this, infoLessonVideoPage.class));
        overridePendingTransition(0,0);
    }

    public void endReadLessons(){
        dbHelper.newFinishLessons(idLesson, 1);

        onResume();

        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Kostya/profile/userProgress?iduser=" + dbHelper.getUserId() + "&idlessons=" + idLesson + "&idtypelessons=1";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());

                Gson gson = new Gson();
                resp responses = gson.fromJson(json, resp.class);

                if(!responses.status.equals("success")){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), responses.message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });

    }


    public class resp{
        private String message;
        private String status;
    }

}