package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class editProfilePage extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText nameEditText;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        dbHelper = new DatabaseHelper(this);

        nameEditText = findViewById(R.id.edNameProf);
        emailEditText = findViewById(R.id.edEmailProf);

        nameEditText.setText(dbHelper.getUserName());
        emailEditText.setText(dbHelper.getUserEmail());
    }

    public void saveChanges(View view){

        String name = nameEditText.getText().toString();

        String email = emailEditText.getText().toString();

        if(isValidEmail(email) && name.trim().length() > 0){

            OkHttpClient client = new OkHttpClient();
            String url = "https://minexc.ru/Kostya/profile/editInfo?email=" + email + "&name=" + name + "&iduser=" + dbHelper.getUserId();
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
                    defaultResponse dfR = gson.fromJson(json, defaultResponse.class);

                    if (dfR.status.equals("error")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), dfR.messages, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if(dfR.status.equals("success")){


                            dbHelper.changeUserInformation(email, name);

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

    public  class defaultResponse{
        private String status;
        private String messages;
    }
}