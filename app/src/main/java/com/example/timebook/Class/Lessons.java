package com.example.timebook.Class;

public class Lessons {

    private int id;
    private String title;

    public Lessons(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
