package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.timebook.Adapters.lessonsAdapter;
import com.example.timebook.Class.Lessons;
import com.example.timebook.Helper.DatabaseHelper;

public class lessonsPage extends AppCompatActivity {

    private ListView list;
    private String[] array;

    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        lessonsAdapter adapter = new lessonsAdapter(this);


        adapter.setItemClickListener(new lessonsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                infoLessonTextPage.idLesson = position;
                toLesson();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getLessons();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Получаем данные из курсора
                int id = cursor.getInt(0);
                String title = cursor.getString(1);

                Lessons lessons = new Lessons(id, title);
                adapter.addData(lessons);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }


//        list = findViewById(R.id.listView);
//        array = getResources().getStringArray(R.array.lessons_array);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
//        list.setAdapter(adapter);
    }

    public void toLesson(){
        startActivity(new Intent(this, infoLessonTextPage.class));
    }
}