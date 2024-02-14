package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.timebook.Helper.DatabaseHelper;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("CustomSplashScreen")
public class splashScreen extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_activity);

        dbHelper = new DatabaseHelper(this);

        verificationUserAuth();
    }

    public void verificationUserAuth(){
        String email = dbHelper.getUserEmail();
        String password = dbHelper.getUserPassword();

        if(dbHelper.isTableExists("users")){
            OkHttpClient client = new OkHttpClient();
            String url = "https://minexc.ru/Kostya/profile/auth?email=" + email + "&password=" + password;
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
                    authResponse user = gson.fromJson(json, authResponse.class);
                    dbHelper.deleteTable("users");
                    if (user.error != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), user.error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        if(!user.status.equals("success")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Произошла ошибка, попробуйте позже!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("local.db", MODE_PRIVATE, null);
                            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER, email TEXT, password TEXT, name TEXT)");
                            String exception = String.format("INSERT INTO users (id, email, password, name) VALUES (%s, '%s', '%s', '%s')", user.id, email, password, user.name);
                            db.execSQL(exception);
                        }

                    }
                    getLessons();
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    String error = e.toString();
                }
            });
        }else{
            getLessons();
        }
    }

    public void getLessons(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Kostya/selectLessons";
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
                Lessons[] lessons = gson.fromJson(json, Lessons[].class);

                SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("local.db", MODE_PRIVATE, null);

                dbHelper.deleteTable("lessons");

                db.execSQL("CREATE TABLE IF NOT EXISTS lessons (id INTEGER, title TEXT, text TEXT, videoLink TEXT)");

                for(int i = 0; i < lessons.length; i++){
                    String exception = String.format("INSERT INTO lessons (id, title, text, videoLink) VALUES (%s, '%s', '%s', '%s')", lessons[i].id, lessons[i].title, lessons[i].text, lessons[i].videoLink);
                    db.execSQL(exception);
                }
                if(dbHelper.isTableExists("users")){
                    getUserProgress();
                }else{
                    toAuth();
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });
    }

    public void getUserProgress(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Kostya/profile/selectUserProgress?iduser="+dbHelper.getUserId();
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
                userProgress[] userProgresses = gson.fromJson(json, userProgress[].class);

                SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("local.db", MODE_PRIVATE, null);

                dbHelper.deleteTable("userProgress");

                if(userProgresses[0].message == null){
                    db.execSQL("CREATE TABLE IF NOT EXISTS userProgress (idUser INTEGER, idLessons INTEGER, idTypeLessons INTEGER)");

                    for(int i = 0; i < userProgresses.length; i++){
                        String exception = String.format("INSERT INTO userProgress (idUser, idLessons, idTypeLessons) VALUES ('%s', '%s', '%s')", userProgresses[i].idUser, userProgresses[i].idLessons, userProgresses[i].idTypeLessons);
                        db.execSQL(exception);
                    }
                }

              getQuiz();

            }

            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });
    }


    public void getQuiz(){
        dbHelper.deleteTable("quiz");
        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Kostya/selectQuiz";
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
                Quiz[] quiz = gson.fromJson(json, Quiz[].class);

                SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("local.db", MODE_PRIVATE, null);


                db.execSQL("CREATE TABLE IF NOT EXISTS quiz (id INTEGER, question TEXT, answer INTEGER)");

                for(int i = 0; i < quiz.length; i++){
                    String exception = String.format("INSERT INTO quiz (id, question, answer) VALUES (%s, '%s', %s)", quiz[i].id, quiz[i].question, quiz[i].answer);
                    db.execSQL(exception);
                }

                if(dbHelper.isTableExists("users")){
                    toProfile();
                }else{
                    toAuth();
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });
    }

    public void toProfile(){
        startActivity(new Intent(this, profilePage.class));
    }

    public void toAuth(){
        startActivity(new Intent(this, signInPage.class));
    }

    public class authResponse{
        private String id;
        private String status;
        private String error;
        private String name;
    }

    public class Lessons{
        private int id;
        private String title;
        private String text;
        private String videoLink;
    }

    public class userProgress{
        private String message;
        private int idUser;
        private int idLessons;
        private int idTypeLessons;
    }

    public class Quiz{
        private int id;
        private String question;
        private int answer;
    }
}