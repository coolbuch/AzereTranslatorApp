package com.example.azuretranslation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class DB extends SQLiteOpenHelper
{

    ArrayList<Phrases> phraseArray = new ArrayList<>();
    public DB(Context context) {
        // конструктор суперкласса
        super(context, "DB", null, 1);
    }

    public void addPhrase(String ph1, String ph2)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ph1", ph1);
        cv.put("ph2", ph2);
        long rowID = db.insert("phrases", null, cv);
        getPhrases();
    }

    public ArrayList<Phrases> getPhrases()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("phrases", null, null, null, null, null, null);
        phraseArray.clear();
        if (c.moveToFirst())
        {

            int id = c.getColumnIndex("id");
            int ph1 = c.getColumnIndex("ph1");
            int ph2 = c.getColumnIndex("ph2");
            int date = c.getColumnIndex("date");
            do {
                phraseArray.add(new Phrases(c.getInt(id), c.getString(ph1), c.getString(ph2), c.getString(date)));
            } while (c.moveToNext());
        } else
            c.close();
        return phraseArray;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // создаем таблицу с полями
        db.execSQL("create table phrases ("
                + "id integer primary key autoincrement,"
                + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,"
                + "ph1 text,"
                + "ph2 text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    class Phrases
    {
        int id;
        String date, ph1, ph2;

        public Phrases(int id ,String date, String ph1, String ph2)
        {
            this.id = id;
            this.date = date;
            this.ph1 = ph1;
            this.ph2 = ph2;
        }

        @NonNull
        @Override
        public String toString() {
            return date + "_"  + ph1 + " : " + ph2;
        }
    }
}
