package com.example.timebook.Helper;

import static android.content.Context.MODE_PRIVATE;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.widget.Toast;

        //import com.example.minexcmobile.Class.Data;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local.db";
    private static final int DATABASE_VERSION = 1;
    Context cont;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // Метод для проверки существования таблицы
    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        db.close();
        return false;
    }

    public void deleteTable(String nameTable){
        if(isTableExists(nameTable)){
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("DROP TABLE " + nameTable);
            db.close();
        }
    }

    public String getUserName(){
        String name = "";
        if(isTableExists("users")){
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT name FROM users WHERE id = ?", new String[]{String.valueOf(getUserId())});

            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(0);
                cursor.close();
            }
        }
        return name;
    }

    public String getUserEmail(){
        String email = "";
        if(isTableExists("users")){
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT email FROM users WHERE id = ?", new String[]{String.valueOf(getUserId())});

            if (cursor != null && cursor.moveToFirst()) {
                email = cursor.getString(0);
                cursor.close();
            }
        }
        return email;
    }

    public String getUserPassword(){
        String password = "";
        if(isTableExists("users")){
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT password FROM users WHERE id = ?", new String[]{String.valueOf(getUserId())});

            if (cursor != null && cursor.moveToFirst()) {
                password = cursor.getString(0);
                cursor.close();
            }
        }
        return password;
    }

    public int getUserId(){
        int id = -1;
        if(isTableExists("users")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id FROM (SELECT * FROM users ORDER BY id DESC LIMIT 1) t ORDER BY id;", null);

            if(cursor != null && cursor.moveToFirst()){
                id = cursor.getInt(0);
                cursor.close();
            }

            return id;
        }
        return id;
    }

    public void changeUserInformation(String email, String name){
        if(isTableExists("users")){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("email", email);
            values.put("name", name);

            db.update("users", values, "id = ?", new String[]{String.valueOf(getUserId())});
            db.close();
        }
    }

    public int getCountLessons(){
        int count = 0;
        if(isTableExists("lessons")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT count(*) FROM lessons", null);

            if(cursor != null && cursor.moveToFirst()){
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        }
        return count;
    }

    public Cursor getLessons(){
        if (isTableExists("lessons")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, title From lessons ";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }


    public Cursor getLessonInformation(int idLesson){
        if (isTableExists("lessons")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT title, text, videoLink From lessons where id = " + idLesson;
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public String getCountFinishTextLessons(){
        String count = "0";
        if(isTableExists("userProgress")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT count(*) FROM userProgress WHERE idUser = ? and idTypeLessons = ?", new String[]{String.valueOf(getUserId()), String.valueOf(1)});

            if(cursor != null && cursor.moveToFirst()){
                count = cursor.getString(0);
                cursor.close();
            }

            return count;
        }
        return count;
    }

    public String getCountFinishVideoLessons(){
        String count = "0";
        if(isTableExists("userProgress")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT count(*) FROM userProgress WHERE idUser = ? and idTypeLessons = ?", new String[]{String.valueOf(getUserId()), String.valueOf(2)});

            if(cursor != null && cursor.moveToFirst()){
                count = cursor.getString(0);
                cursor.close();
            }

            return count;
        }
        return count;
    }

    public void newFinishLessons(int idLessons, int idTypeLessons){
        SQLiteDatabase db = this.getWritableDatabase();

        if(isTableExists("userProgress")){
            String query = "SELECT * FROM userProgress WHERE idTypeLessons = " + idTypeLessons + " and idLessons  = " + idLessons;
            Cursor cursor = db.rawQuery(query, null);
            boolean isFinished = cursor.getCount() > 0;
            cursor.close();

            if(!isFinished){
                ContentValues values = new ContentValues();
                values.put("idUser", getUserId());
                values.put("idLessons", idLessons);
                values.put("idTypeLessons", idTypeLessons);

                db.insert("userProgress", null, values);
                db.close();
            }
        }else{
            db.execSQL("CREATE TABLE IF NOT EXISTS userProgress (idUser INTEGER, idLessons INTEGER, idTypeLessons INTEGER)");
            newFinishLessons(idLessons, idTypeLessons);
        }
    }

    public String getVideoUrl(int idLessons){
        String url = "";
        if(isTableExists("lessons")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT videoLink FROM lessons WHERE id = ?", new String[]{String.valueOf(idLessons)});

            if(cursor != null && cursor.moveToFirst()){
                url = cursor.getString(0);
                cursor.close();
            }

            return url;
        }
        return url;
    }


    public Boolean isEndLessons(int idLessons, int idTypeLessons){

        if(isTableExists("userProgress") && isTableExists("lessons")){
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM userProgress WHERE idTypeLessons = " + idTypeLessons + " and idLessons  = " + idLessons;
            Cursor cursor = db.rawQuery(query, null);
            boolean isFinished = cursor.getCount() > 0;
            cursor.close();
            return  isFinished;
        }
        return false;
    }

    public int getCountQuestion(){
        if(isTableExists("quiz")){
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT count(id) FROM quiz";
            Cursor cursor = db.rawQuery(query, null);

            int count = 0;
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

            cursor.close();
            return count;
        }
        return 0;
    }


    public Cursor getQuestion(int idQuestion){
        if (isTableExists("quiz")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT question, answer From quiz where id = " + idQuestion;
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }
}