package com.lkyear.lllauncher.Launcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lkyear on 17/5/30.
 */

public class ShelvesDataBaseHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;
    private final static String DB_NAME = "shelves.db";
    private final static String TABLE_NAME = "shelves";
    private final static String CREATE_TBL = "create table shelves(_id integer primary key autoincrement, title text, packages text)";
    private SQLiteDatabase db;

    public ShelvesDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //必须通过super 调用父类的构造函数
        super(context, name, factory, version);
    }

    public ShelvesDataBaseHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    public ShelvesDataBaseHelper(Context context){
        this(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Boolean insert(ContentValues values) {
        //获取SQLiteDatabase实例
        SQLiteDatabase db = getWritableDatabase();
        //插入数据库中
        try {
            db.insertOrThrow(TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            return false;
        }
        db.close();
        return true;
    }

    public Cursor query(){
        SQLiteDatabase db = getReadableDatabase();
        //获取Cursor
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        return c;

    }

    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
    }

    public void update(ContentValues values, int id){
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }

}
