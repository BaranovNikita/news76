package ru.krista.nbaranov.news76.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.krista.nbaranov.news76.News;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDatabase";
    private static final String TABLE_LIST = "my_news";
    public static final String KEY_ID = "id";
    public static final String GUID = "GUID";
    public static final String TITLE_NEWS = "news_title";
    public static final String LINK_NEWS = "news_link";
    public static final String DATE_NEWS = "news_date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_LIST + "(" + KEY_ID
                + " INTEGER,"+ GUID + " TEXT UNIQUE," + TITLE_NEWS + " TEXT," + LINK_NEWS + " TEXT," + DATE_NEWS + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        onCreate(db);
    }

    public void addNews(News news) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GUID, news.getId());
        values.put(TITLE_NEWS, news.getTitle());
        values.put(LINK_NEWS, news.getLink());
        values.put(DATE_NEWS,Utils.getDateTime(news.getDate()));
        db.insertWithOnConflict(TABLE_LIST, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Cursor getNews() {
        String selectQuery = "SELECT  * FROM " + TABLE_LIST;

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(selectQuery, null);
    }



}
