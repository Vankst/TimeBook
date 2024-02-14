package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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

public class quizPage extends AppCompatActivity {

    private int idQuestion = 1;
    private int countQuestion = 0;
    private String question = "";
    private int answer = 0;

    private int countCorrectAnswer = 0;

    DatabaseHelper dbHelper;

    TextView questionTextView;
    TextView countQuestionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        dbHelper = new DatabaseHelper(this);

        countQuestion = dbHelper.getCountQuestion();



        questionTextView = findViewById(R.id.questionTextView);

        countQuestionTextView = findViewById(R.id.countQuestionTextView);

        selectQuestion();

    }

    public void selectQuestion(){

        if(idQuestion <= countQuestion){

            Cursor cursor = dbHelper.getQuestion(idQuestion);

            if(cursor != null && cursor.moveToFirst()){
                question = cursor.getString(0);
                answer = cursor.getInt(1);
            }

            questionTextView.setText(question);

            countQuestionTextView.setText("Вопрос " + idQuestion + " из " + countQuestion);


            cursor.close();
        }
        if(idQuestion == countQuestion+1){
            Toast.makeText(this, "Правильно = " + countCorrectAnswer + " из " + countQuestion, Toast.LENGTH_SHORT).show();

            toProfile();
        }
    }

    public void getAnswer(View view){

        int id = view.getId();


        if (id == R.id.yes) {

            idQuestion++;

            if(answer == 1){
                countCorrectAnswer++;
            }

            selectQuestion();

        } else if (id == R.id.no) {
            idQuestion++;

            if(answer == 0){
                countCorrectAnswer++;
            }

            selectQuestion();
        }
    }

    public void toProfile(){
        startActivity(new Intent(this, profilePage.class));
    }
}