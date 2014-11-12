package com.digutsoft.note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DMDatabaseHelper extends SQLiteOpenHelper {
    Context mContext;

    public DMDatabaseHelper(Context context) {
        super(context, "dNoteDatabase.db", null, 1);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase database) {
        String mCategoryName = mContext.getResources().getString(R.string.default_category_name);
        database.execSQL("CREATE TABLE __CategoryList (cateIndex INTEGER PRIMARY KEY NOT NULL, cateName TEXT);");
        database.execSQL("CREATE TABLE \"" + mCategoryName + "\" (memoId INTEGER PRIMARY KEY, memoTitle TEXT, memoContent TEXT);");
        database.execSQL("INSERT INTO __CategoryList (cateName) VALUES (\"" + mCategoryName + "\");");

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}