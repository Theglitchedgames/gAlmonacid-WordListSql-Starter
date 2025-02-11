package com.android.example.wordlistsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WordListOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wordlist.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_WORDS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_WORD + " TEXT" + ");";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        fillDatabaseWithData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    private void fillDatabaseWithData(SQLiteDatabase db) {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask", "Android Studio", "SQLiteDatabase", "SQLOpenHelper", "Data model", "ViewHolder", "AndroidPerformance", "OnClickListener"};
        ContentValues values = new ContentValues();

        for (String word : words) {
            values.put(COLUMN_WORD, word);
            db.insert(TABLE_WORDS, null, values);
        }
    }

    public WordItem query(int position) {
        String query = "SELECT * FROM " + TABLE_WORDS + " ORDER BY " + COLUMN_WORD + " ASC " + " LIMIT " + position + ",1";
        Cursor cursor = null;
        WordItem entry = new WordItem();

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                entry.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                entry.setWord(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)));
            }
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION! " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return entry;
    }

    public long insert(String word) {
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(TABLE_WORDS, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public long count() {
        if (mReadableDB == null){
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, TABLE_WORDS);
    }

    public int delete(int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(TABLE_WORDS, COLUMN_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public int update(int id, String word) {
        int mNumberOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(COLUMN_WORD, word);
            mNumberOfRowsUpdated = mWritableDB.update(TABLE_WORDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION: " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public Cursor search(String searchString) {
        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            String[] columns = new String[]{COLUMN_WORD};
            searchString = "%" + searchString + "%";
            String where = COLUMN_WORD + " LIKE ?";
            String[] whereArgs = new String[]{searchString};

            cursor = mReadableDB.query(TABLE_WORDS, columns, where, whereArgs, null, null, null);
        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e.getMessage());
        }
        return cursor;
    }
}