package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timebook.Helper.DatabaseHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class registerPage extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);

        dbHelper = new DatabaseHelper(this);
    }

    public void toAuth(View view){
        startActivity(new Intent(this, signInPage.class));
    }

    public void toProfile(){
        startActivity(new Intent(this, profilePage.class));
    }

    public void registerAccount(View view){
        dbHelper.deleteTable("users");

        EditText emailEditText =  findViewById(R.id.editEmail);
        String email = emailEditText.getText().toString();

        EditText passwordEditText = findViewById(R.id.edPassword);
        String password = passwordEditText.getText().toString();

        EditText nameEditText = findViewById(R.id.editName);
        String name = nameEditText.getText().toString();


        if(password.trim().length() > 0 && isValidEmail(email) && name.trim().length() > 0){

            OkHttpClient client = new OkHttpClient();
            String url = "https://minexc.ru/Kostya/profile/register?email=" + email + "&password=" + sha256(password) + "&name=" + name;
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
                    registerResponse user = gson.fromJson(json, registerResponse.class);

                    if (user.status.equals("error")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), user.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if(user.status.equals("success")){

                            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("local.db", MODE_PRIVATE, null);
                            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER, email TEXT, password TEXT, name TEXT)");
                            String exception = String.format("INSERT INTO users (id, email, password, name) VALUES (%s, '%s', '%s', '%s')", user.id, email, sha256(password), user.name);
                            db.execSQL(exception);
                            db.close();

                            toProfile();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Успешно!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Произошла ошибка, попробуйте позже!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    String error = e.toString();
                }
            });

        }
        else{
            Toast.makeText(getApplicationContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    public static boolean isValidEmail(String email) {
        // Проверяем на null
        if (email == null) {
            return false;
        }

        // Задаем паттерн регулярного выражения для email
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        // Сравниваем строку с паттерном
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String sha256(String input) {
        try {
            // Создаем объект MessageDigest с использованием алгоритма SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Преобразуем входную строку в байтовый массив
            byte[] inputBytes = input.getBytes();

            // Обновляем хэш функцией с входными данными
            byte[] hashedBytes = digest.digest(inputBytes);

            // Преобразуем хэш в строку шестнадцатеричного формата
            StringBuilder builder = new StringBuilder();
            for (byte b : hashedBytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class registerResponse{
        private String status;
        private String id;
        private String name;
        private String message;
    }
}