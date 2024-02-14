package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.timebook.Helper.DatabaseHelper;

public class profilePage extends AppCompatActivity {

    DatabaseHelper dbHelper;
    TextView tvName;
    TextView tvTextProg;
    TextView tvTextProg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        dbHelper = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvName);

        tvTextProg = findViewById(R.id.tvTextProg);
        tvTextProg2 = findViewById(R.id.tvTextProg2);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tvName = findViewById(R.id.tvName);

        tvTextProg = findViewById(R.id.tvTextProg);
        tvTextProg2 = findViewById(R.id.tvTextProg2);

        tvName.setText(dbHelper.getUserName());

        tvTextProg.setText("Уроков выполнено: " + dbHelper.getCountFinishTextLessons() + " из " + dbHelper.getCountLessons());
        tvTextProg2.setText("Видеоуроков просмотрено: " + dbHelper.getCountFinishVideoLessons() + " из " + dbHelper.getCountLessons());

        ProgressBar progressBar = findViewById(R.id.progressBar);
        float progress = (Float.parseFloat(dbHelper.getCountFinishTextLessons()) + Float.parseFloat(dbHelper.getCountFinishVideoLessons())) / (dbHelper.getCountLessons()*2)*100;

        progressBar.setProgress(Math.round(progress));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }


    public void toEditProfile(View view){
        startActivity(new Intent(this, editProfilePage.class));
    }

    public void toLessonsPage(View view){
        startActivity(new Intent(this, lessonsPage.class));
    }

    public void toQuiz(View view){
        startActivity(new Intent(this, quizPage.class));
    }
}