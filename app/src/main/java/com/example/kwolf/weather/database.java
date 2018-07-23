package com.example.kwolf.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kwolf on 2017/11/19/0019.
 */
public class database extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_database";
    private static final String TABLE_NAME = "my_record";
    private static final int DB_VERSION = 1;

    public database (Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "+TABLE_NAME+" (_id INTEGER PRIMARY KEY, name TEXT, tempreture TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert2DB (String name,String tempre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("tempreture",tempre);
        db.insert(TABLE_NAME,null,cv);
        db.close();
    }

    public Cursor query () {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }

    public Cursor query1(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where name = '" + name + "'", null);
        return cursor;
    }

    public void delete1(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name=?";
        String[] whereArgs = {name};
        db.delete(TABLE_NAME,whereClause,whereArgs);
        db.close();
    }

    public void inserttempre(String name, String tempre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("tempreture",tempre);
        String whereClause = "name=?";
        String[] whereArgs={name};
        db.update(TABLE_NAME,cv,whereClause,whereArgs);
    }

}