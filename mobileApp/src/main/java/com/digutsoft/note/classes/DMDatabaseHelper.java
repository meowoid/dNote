package com.digutsoft.note.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digutsoft.note.R;
import com.digutsoft.note.classes.DMMemoTools;

import java.util.ArrayList;

public class DMDatabaseHelper extends SQLiteOpenHelper {
    Context mContext;

    public DMDatabaseHelper(Context context) {
        super(context, "dNoteDatabase.db", null, 2);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase database) {
        String mCategoryName = mContext.getResources().getString(R.string.default_category_name);
        database.execSQL("CREATE TABLE __CategoryList (cateIndex INTEGER PRIMARY KEY NOT NULL, cateName TEXT);");//, catePass TEXT);");
        database.execSQL("CREATE TABLE \"" + mCategoryName + "\" (memoId INTEGER PRIMARY KEY, memoTitle TEXT, memoContent TEXT, checkStatus INTEGER);");
        database.execSQL("INSERT INTO __CategoryList (cateName) VALUES (\"" + mCategoryName + "\");");

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ArrayList<String> categoryList = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM __CategoryList ORDER BY cateIndex ASC", null);
        while (cursor.moveToNext()) {
            categoryList.add(cursor.getString(1));
        }

        for (int i = 0; i < categoryList.size(); i++) {
            database.execSQL(String.format("ALTER TABLE \"%s\" ADD checkStatus INTEGER;", DMMemoTools.addSlashes(categoryList.get(i))));
        }
    }
}