package com.digutsoft.note.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DMMemoTools {
    public static String addSlashes(String mString) {
        return mString.replace("\"", "\"\"");
    }

    public static ArrayList<String> getCategoryList(Context mContext) {
        ArrayList<String> categoryList = new ArrayList<>();

        DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM __CategoryList ORDER BY cateIndex ASC", null);

        while (cursor.moveToNext()) {
            categoryList.add(cursor.getString(1));
        }

        cursor.close();
        database.close();
        databaseHelper.close();

        return categoryList;
    }

    public static int createCategory(Context mContext, String mCategoryName) {
        /**
         * createCategory returns:
         * 0: successfully created category
         * 1: failed to create category: general error
         * 2: failed to create category: reserved by dMemo
         * 3: failed to create category: category name is too long (max 20 letters)
         * 4: failed to create category: empty category name
         * 5: failed to create category: category name already exists
         **/
        if (mCategoryName.equals("")) return 4;
        if (mCategoryName.trim().equals("")) return 4;
        if (mCategoryName.startsWith("__")) return 2;
        if (mCategoryName.length() > 20) return 3;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();

            Cursor cursor = database.rawQuery(String.format("SELECT cateName FROM \"__CategoryList\" WHERE cateName = \"%s\";",
                    addSlashes(mCategoryName)), null);
            while (cursor.moveToNext()) {
                if (!cursor.getString(0).equals("")) {
                    cursor.close();
                    database.close();
                    databaseHelper.close();
                    return 5;
                }
            }

            database.execSQL(String.format("CREATE TABLE \"%s\" (memoId INTEGER PRIMARY KEY, memoTitle TEXT, memoContent TEXT, checkStatus INTEGER);",
                    addSlashes(mCategoryName)));
            database.execSQL(String.format("INSERT INTO \"__CategoryList\" (cateName) VALUES (\"%s\");",
                    addSlashes(mCategoryName)));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static int renameCategory(Context mContext, String mCategoryName, String mNewCategoryName) {
        /**
         * renameCategory returns:
         * 0: successfully renamed category
         * 1: failed to rename category: general error
         * 2: failed to rename category: category name already exists
         * 3: failed to rename category: old and new category name are same
         * 4: failed to rename category: new category name is empty
         * 5: failed to rename category: reserved by dMemo
         * 6: failed to rename category: new category name is too long (max 20 letters)
         */

        if (mNewCategoryName.equals("")) return 4;
        if (mNewCategoryName.trim().equals("")) return 4;
        if (mNewCategoryName.equals(mCategoryName)) return 3;
        if (mNewCategoryName.startsWith("__")) return 5;
        if (mNewCategoryName.length() > 20) return 6;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();

            Cursor cursor = database.rawQuery(String.format("SELECT cateName FROM \"__CategoryList\" WHERE cateName = \"%s\";",
                    addSlashes(mNewCategoryName)), null);
            while (cursor.moveToNext()) {
                if (!cursor.getString(0).equals("")) {
                    cursor.close();
                    database.close();
                    databaseHelper.close();
                    return 2;
                }
            }

            database.execSQL(String.format("ALTER TABLE \"%s\" RENAME TO \"%s\";",
                    addSlashes(mCategoryName), addSlashes(mNewCategoryName)));
            database.execSQL(String.format("UPDATE \"__CategoryList\" SET cateName = \"%s\" WHERE cateName = \"%s\";",
                    addSlashes(mNewCategoryName), addSlashes(mCategoryName)));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public static int deleteCategory(Context mContext, String mCategoryName) {
        /**
         * deleteCategory returns:
         * 0: successfully deleted category
         * 1: failed to delete category: general error
         * 2: failed to delete category: at least 1 category is required
         **/
        if (getCategoryList(mContext).size() == 1) return 2;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("DELETE FROM \"__CategoryList\" WHERE cateName = \"%s\";",
                    addSlashes(mCategoryName)));
            database.execSQL(String.format("DROP TABLE \"%s\";",
                    addSlashes(mCategoryName)));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static ArrayList<DMMemoList> getMemoList(Context mContext, String mCategoryName) {
        ArrayList<DMMemoList> memoList = new ArrayList<>();

        DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM \"%s\";",
                addSlashes(mCategoryName)), null);

        while (cursor.moveToNext()) {
            String memoTitle, memoContent;
            if (cursor.getString(1).isEmpty()) {
                String tmpTitle = cursor.getString(2);
                tmpTitle = tmpTitle.replaceAll("\n", " ");
                if (tmpTitle.length() > 20) memoTitle = tmpTitle.substring(0, 20) + "...";
                else memoTitle = tmpTitle;
            } else {
                memoTitle = cursor.getString(1);
            }
            memoContent = cursor.getString(2);

            memoList.add(new DMMemoList(cursor.getInt(0), memoTitle, memoContent, cursor.getInt(3) != 0));
        }

        cursor.close();
        database.close();
        databaseHelper.close();

        return memoList;
    }

    public static String getMemo(Context mContext, String mCategoryName, int mMemoId, boolean getTitle) {
        String mMemoTitle = "";
        String mMemoContent = "";

        DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM \"%s\" WHERE memoId = %d;",
                addSlashes(mCategoryName), mMemoId), null);

        while (cursor.moveToNext()) {
            if (cursor.getString(1).isEmpty()) {
                String tmpTitle = cursor.getString(2);
                tmpTitle = tmpTitle.replaceAll("\n", " ");
                if (tmpTitle.length() > 20) mMemoTitle = tmpTitle.substring(0, 20) + "...";
                else mMemoTitle = tmpTitle;
            } else {
                mMemoTitle = cursor.getString(1);
            }
            mMemoContent = cursor.getString(2);
        }

        cursor.close();
        database.close();
        databaseHelper.close();

        if (getTitle) return mMemoTitle;
        else return mMemoContent;
    }

    public static int saveMemo(Context mContext, String mCategoryName, String mMemoContent) {
        /**
         * saveMemo returns:
         * 0: memo successfully saved
         * 1: failed to save memo: general error
         * 2: failed to save memo: empty memo
         **/
        if (mMemoContent.equals("")) return 2;
        if (mMemoContent.trim().equals("")) return 2;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("INSERT INTO \"%s\" (memoTitle, memoContent) VALUES (\"\", \"%s\");",
                    addSlashes(mCategoryName), addSlashes(mMemoContent)));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static int saveMemo(Context mContext, String mCategoryName, String mMemoTitle, String mMemoContent) {
        /**
         * saveMemo returns:
         * 0: memo successfully saved
         * 1: failed to save memo: general error
         * 2: failed to save memo: empty memo
         * 3: failed to save memo: memo title is too long (max 20 letters)
         **/
        if (mMemoContent.equals("")) return 2;
        if (mMemoContent.trim().equals("")) return 2;
        if (mMemoTitle.length() > 20) return 3;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("INSERT INTO \"%s\" (memoTitle, memoContent) VALUES (\"%s\", \"%s\");",
                    addSlashes(mCategoryName), addSlashes(mMemoTitle), addSlashes(mMemoContent)));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static int setMemoTitle(Context mContext, String mCategoryName, int mMemoId, String mMemoTitle) {
        /**
         * setMemoTitle returns:
         * 0: memo title successfully set
         * 1: failed to set title: general error
         * 2: failed to set title: title is too long (max 20 letters)
         */

        if (mMemoTitle.length() > 20) return 2;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("UPDATE \"%s\" SET memoTitle = \"%s\" WHERE memoId = %d;",
                    addSlashes(mCategoryName), addSlashes(mMemoTitle), mMemoId));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static int editMemo(Context mContext, String mCategoryName, int mMemoId, String mNewMemoContent) {
        /**
         * editMemo returns:
         * 0: memo successfully edited
         * 1: failed to edit memo: general error
         * 2: failed to edit memo: empty memo
         **/
        if (mNewMemoContent.equals("")) return 2;
        if (mNewMemoContent.trim().equals("")) return 2;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("UPDATE \"%s\" SET memoContent = \"%s\" WHERE memoId = %d;",
                    addSlashes(mCategoryName), addSlashes(mNewMemoContent), mMemoId));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static int editMemo(Context mContext, String mCategoryName, int mMemoId, String mNewMemoTitle, String mNewMemoContent) {
        /**
         * editMemo returns:
         * 0: memo successfully edited
         * 1: failed to edit memo: general error
         * 2: failed to edit memo: empty memo
         * 3: failed to edit memo: memo title is too long (max 20 letters)
         **/
        if (mNewMemoContent.equals("")) return 2;
        if (mNewMemoContent.trim().equals("")) return 2;
        if (mNewMemoTitle.length() > 20) return 3;

        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("UPDATE \"%s\" SET memoTitle = \"%s\", memoContent = \"%s\" WHERE memoId = %d;",
                    addSlashes(mCategoryName), addSlashes(mNewMemoTitle), addSlashes(mNewMemoContent), mMemoId));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static int deleteMemo(Context mContext, String mCategoryName, int mMemoId) {
        /**
         * deleteMemo returns:
         * 0: memo successfully deleted
         * 1: failed to delete memo
         *
         * Actually deleteMemo can be replaced to boolean type, but because of moving category function,
         * it became int type. moving category adds return value of saveMemo and deleteMemo,
         * and if result comes to zero(0), moving category is successful, otherwise not.
         */
        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("DELETE FROM \"%s\" WHERE memoId = %d;",
                    addSlashes(mCategoryName), mMemoId));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public static boolean checkMemo(Context mContext, String mCategoryName, int mMemoId, boolean checkStatus) {
        /**
         * checkMemo returns:
         * true: successfully checked/un-checked memo
         * false: failed to check/un-check memo
         */
        try {
            DMDatabaseHelper databaseHelper = new DMDatabaseHelper(mContext);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.execSQL(String.format("UPDATE \"%s\" SET checkStatus = %d WHERE memoId = %d;",
                    addSlashes(mCategoryName), (checkStatus ? 1 : 0), mMemoId));
            Log.d("DMMemoTools", String.format("UPDATE \"%s\" SET checkStatus = %d WHERE memoId = %d;",
                    addSlashes(mCategoryName), (checkStatus ? 1 : 0), mMemoId));
            database.close();
            databaseHelper.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}