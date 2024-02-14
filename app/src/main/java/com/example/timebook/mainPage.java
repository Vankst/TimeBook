package com.example.timebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.timebook.Helper.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class mainPage extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_profile);

        if(!dbHelper.isTableExists("users")){
            menuItem.setTitle(R.string.menu_auth);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                if(Objects.equals(menuItem.getTitle(), "Профиль")){
                    startActivity(new Intent(this, profilePage.class));
                }else{
                    startActivity(new Intent(this, signInPage.class));
                }
                return true;
            } else if (item.getItemId() == R.id.nav_about) {
                startActivity(new Intent(this, aboutPage.class));
            } else if (item.getItemId() == R.id.nav_lessons) {
                if(!dbHelper.isTableExists("users")){
                    Toast.makeText(this, "Авторизируйтесь", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(this, lessonsPage.class));
                }
            }
            return false;
        });

    }
}