package it.splineyellow.quizgame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Francesco on 29/04/2014.
 */
public class UtentiDatabaseAdapter {

    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GIOCATE = "giocate";
    public static final String KEY_VINTE = "vinte";
    public static final String KEY_PAREGGIATE = "pareggiate";
    public static final String KEY_PERSE = "perse";
    public static final String KEY_ULTIMA = "ultima_partita";

    public static final String TAG = "UtentiDatabaseAdapter";

    public static final String DATABASE_NAME = "utenti.db";

    public static final String TABLE_UTENTI = "utenti";

    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_UTENTI_CREATE = "create table " +
            TABLE_UTENTI + " (" +
            KEY_NICKNAME + " text primary key," +
            KEY_PASSWORD + " text not null," +
            KEY_GIOCATE + " integer not null," +
            KEY_VINTE + " integer not null," +
            KEY_PAREGGIATE + " integer not null," +
            KEY_PERSE + " integer not null," +
            KEY_ULTIMA + " text not null" + // aggiungere eventuali check
            ");";

    public static final String TABLE_UTENTI_DROP = "drop table if exists " + TABLE_UTENTI + ";";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public UtentiDatabaseAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper (Context context) {
            super (context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_UTENTI_CREATE);
        }

        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(TABLE_UTENTI_DROP);
            onCreate(db);
        }

    }

    public UtentiDatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public void checkOrInitializeDB () {
        try{
            String sql = "SELECT "+ KEY_NICKNAME +" FROM " + TABLE_UTENTI + ";";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.close();
        }
        catch(Exception s){
            db.execSQL(TABLE_UTENTI_DROP);
            db.execSQL(TABLE_UTENTI_CREATE);
        }
    }

    // query functions

}
