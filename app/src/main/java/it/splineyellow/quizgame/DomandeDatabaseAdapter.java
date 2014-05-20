package it.splineyellow.quizgame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class DomandeDatabaseAdapter {

    public static final String KEY_DOMANDA = "domanda";
    public static final String KEY_ARGOMENTO = "argomento";
    public static final String KEY_RISPOSTA1 = "risposta1";
    public static final String KEY_RISPOSTA2 = "risposta2";
    public static final String KEY_RISPOSTA3 = "risposta3";
    public static final String KEY_RISPOSTA4 = "risposta4";
    public static final String KEY_ESATTA = "risp_esatta";

    public static final String KEY_CATEGORIA = "categoria";

    public static final String TAG = "DomandaDatabaseAdapter";
    public static final String DATABASE_NAME = "domande.db";
    public static final String TABLE_DOMANDE = "domande";
    public static final String TABLE_CATEGORIE = "categorie";

    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_CATEGORIE_CREATE = "create table " +
            TABLE_CATEGORIE + " (" +
            KEY_CATEGORIA + " text primary key" +
            ");";

    private static final String TABLE_DOMANDE_CREATE = "create table " +
            TABLE_DOMANDE + " (" +
            KEY_DOMANDA + " text primary key," +
            KEY_ARGOMENTO + " text not null references " + TABLE_CATEGORIE +
            "("+ KEY_CATEGORIA +")," +
            KEY_RISPOSTA1 + " text not null," +
            KEY_RISPOSTA2 + " text not null," +
            KEY_RISPOSTA3 + " text not null," +
            KEY_RISPOSTA4 + " text not null," +
            KEY_ESATTA + " integer not null," +
            "check (" + KEY_ESATTA + " between 1 and 4)" +
            ");";

    public static final String TABLE_CATEGORIE_DROP = "drop table if exists " + TABLE_CATEGORIE + ";";
    public static final String TABLE_DOMANDE_DROP = "drop table if exists " + TABLE_DOMANDE + ";";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DomandeDatabaseAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper (Context context) {
            super (context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CATEGORIE_CREATE);
            db.execSQL(TABLE_DOMANDE_CREATE);
        }

        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(TABLE_DOMANDE_DROP);
            db.execSQL(TABLE_CATEGORIE_DROP);
            onCreate(db);
        }

    }

    public DomandeDatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public void checkOrInitializeDB () {
        try{
            String sql = "SELECT "+ KEY_DOMANDA +" FROM " + TABLE_DOMANDE + ";";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.close();
        }
        catch(Exception s){
            db.execSQL(TABLE_DOMANDE_DROP);
            db.execSQL(TABLE_CATEGORIE_DROP);
            db.execSQL(TABLE_CATEGORIE_CREATE);
            db.execSQL(TABLE_DOMANDE_CREATE);
        }
    }

    // query functions

}