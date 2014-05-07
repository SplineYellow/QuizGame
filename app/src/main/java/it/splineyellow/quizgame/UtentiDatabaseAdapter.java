package it.splineyellow.quizgame;

import android.content.ContentValues;
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
    public static final String KEY_GIUSTE = "risp_giuste";
    public static final String KEY_ERRATE = "risp_errate";
    public static final String KEY_ULTIMO = "ultimo_accesso";

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
            KEY_GIUSTE + " integer not null," +
            KEY_ERRATE + " integer not null," +
            KEY_ULTIMO + " text not null" + // aggiungere eventuali check
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

    public boolean alreadyIn (String user) {
        String check = "select count(" + KEY_NICKNAME + ") from " + TABLE_UTENTI +
                " where " + KEY_NICKNAME + " = '" + user + "';";
        Cursor c = db.rawQuery(check, null);
        if (c != null && c.moveToFirst()) {
            if (c.getInt(0) == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void insertUser (String username, String password, String accessTimestamp) {

        accessTimestamp = formatDate (accessTimestamp);

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_NICKNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        initialValues.put(KEY_GIOCATE, 0);
        initialValues.put(KEY_VINTE, 0);
        initialValues.put(KEY_PAREGGIATE, 0);
        initialValues.put(KEY_PERSE, 0);
        initialValues.put(KEY_GIUSTE, 0);
        initialValues.put(KEY_ERRATE, 0);
        initialValues.put(KEY_ULTIMO, accessTimestamp);

        db.insert(TABLE_UTENTI, null, initialValues);

        Log.d(TAG, "Inserito: " + username + ", " + password + ", " + accessTimestamp);
    }

    public String formatDate (String ts) {

        ts = ts.substring(0, 2) + ":" + ts.substring(2,4) + ":" + ts.substring(4, 6) + ";" +
                ts.substring(6, 8) + "/" + ts.substring(8, 10) + "/" + ts.substring(10);

        return ts;
    }

    public void updateLastAccess (String timestamp, String username) {

        timestamp = formatDate(timestamp);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ULTIMO, timestamp);
        db.update(TABLE_UTENTI, initialValues, KEY_NICKNAME + " = '" + username + "'", null);

        Log.d(TAG, "Aggiornato a: " + timestamp);
    }

    public String getCurrentUser () {
        String query = "select " + KEY_NICKNAME + ", " +
            KEY_PASSWORD + ", max(" + KEY_ULTIMO + ") from " + TABLE_UTENTI + ";";
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getString(0) + "," + c.getString(1);
        }
        else return "Guest,guest";
    }

    public int getPlayData (String user, int parameter) {

        String camp;

        switch (parameter) {
            case 1: camp = KEY_GIOCATE;
                break;
            case 2: camp = KEY_VINTE;
                break;
            case 3: camp = KEY_PAREGGIATE;
                break;
            case 4: camp = KEY_PERSE;
                break;
            case 5: camp = KEY_GIUSTE;
                break;
            case 6: camp = KEY_ERRATE;
                break;
            default: return 0;
        }

        String query = "select " + camp + " from " + TABLE_UTENTI +
                " where " + KEY_NICKNAME + " = '" + user + "';";
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(0);
        }

        return 0;
    }

    public String getLastAccess (String user) {
        String query = "select " + KEY_ULTIMO + " from " + TABLE_UTENTI +
                " where " + KEY_NICKNAME + " = '" + user + "';";
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getString(0);
        }
        return "";

    }

}
