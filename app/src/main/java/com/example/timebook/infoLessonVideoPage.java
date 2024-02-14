package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timebook.Helper.DatabaseHelper;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class infoLessonVideoPage extends AppCompatActivity {
    public static int idLesson;
    private WebView webView;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_lesson_video);

        dbHelper = new DatabaseHelper(this);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        View linearLayout = findViewById(R.id.linearLayout);

        webView = findViewById(R.id.webVideo);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private CountDownTimer timer;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                webView.setVisibility(View.GONE);
                frameLayout.addView(view, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER));
                frameLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);

                timer = new CountDownTimer(3 * 60 * 1000, 1000) { // 3 минуты
                    public void onTick(long millisUntilFinished) {}

                    public void onFinish() {
                        endLessons();
                    }
                }.start();
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null) return;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                webView.setVisibility(View.VISIBLE);
                frameLayout.removeView(mCustomView);
                frameLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                mCustomView = null;

                // Остановка таймера при выходе из полноэкранного режима
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
        });
        // Загрузка ссылки YouTube в WebView
        webView.loadUrl(dbHelper.getVideoUrl(idLesson));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
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


    public void toInfoTextPage(View view){
        infoLessonTextPage.idLesson = idLesson;
        startActivity(new Intent(this, infoLessonTextPage.class));
        overridePendingTransition(0,0);
    }

    public void endLessons(){
        dbHelper.newFinishLessons(idLesson, 2);

        onResume();

        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Kostya/profile/userProgress?iduser=" + dbHelper.getUserId() + "&idlessons=" + idLesson + "&idtypelessons=2";
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