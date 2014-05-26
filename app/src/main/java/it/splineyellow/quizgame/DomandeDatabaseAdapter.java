package it.splineyellow.quizgame;

import android.content.ContentValues;
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

    public static final int QUESTION_NUMBER = 5;

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

    public String[] getQuestion (String category) {

        String[] question = new String[6];
        String query = "SELECT " + KEY_DOMANDA + ", " + KEY_RISPOSTA1 + ", " + KEY_RISPOSTA2 +
                ", " + KEY_RISPOSTA3 + ", " + KEY_RISPOSTA4 + ", " + KEY_ARGOMENTO +
                " FROM " + TABLE_DOMANDE + " WHERE " + KEY_ARGOMENTO + " = '" + category + "';";
        Cursor c = db.rawQuery(query, null);

        String domanda = "";
        String risp1 = "";
        String risp2 = "";
        String risp3 = "";
        String risp4 = "";
        String esatta = "";


        if (c != null && c.moveToFirst()) {
            domanda = c.getString(0);  // question
            risp1 = c.getString(1);  // answer
            risp2 = c.getString(2);  // answer
            risp3 = c.getString(3);  // answer
            risp4 = c.getString(4);  // answer
            esatta = c.getString(5);  // right answer (0-3)
        }

        question [0] = domanda;
        question [1] = risp1;
        question [2] = risp2;
        question [3] = risp3;
        question [4] = risp4;
        question [5] = esatta;

        return question;

    }

    public String[][] getQuestions (String category) {

        category = category.toUpperCase();
        String [][] questionByCategory = new String[QUESTION_NUMBER][5];

        String query = "SELECT * FROM " + TABLE_DOMANDE +
                " WHERE " + KEY_ARGOMENTO + " = '" + category + "';";

        int rand1, rand2, rand3;
        rand1 = (int) (Math.random() * (QUESTION_NUMBER));
        rand2 = rand1;
        rand3 = rand1;

        while (rand1 == rand2) {
            rand2 = (int) (Math.random() * QUESTION_NUMBER);
        }
        while (rand3 == rand1 || rand3 == rand2) {
            rand3 = (int) (Math.random() * QUESTION_NUMBER);
        }

        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            for (int i = 0; i < QUESTION_NUMBER; i++) {
                questionByCategory[i][0] = c.getString(0 + i * 6);
                questionByCategory[i][1] = c.getString(1 + i * 6);
                questionByCategory[i][2] = c.getString(2 + i * 6);
                questionByCategory[i][3] = c.getString(3 + i * 6);
                questionByCategory[i][4] = c.getString(4 + i * 6);
                questionByCategory[i][5] = Integer.toString(c.getInt(5 + i * 6));
            }
        }

        String [] firstQuestion = questionByCategory[rand1];
        String [] secondQuestion = questionByCategory[rand2];
        String [] thirdQuestion = questionByCategory[rand3];

        String[][] questionsToSend = new String[3][5];
        questionsToSend [0] = firstQuestion;
        questionsToSend [1] = secondQuestion;
        questionsToSend [2] = thirdQuestion;

        return questionsToSend;

    }


    /* PROVISIONAL METHODS */

    public void fillCategoryTable () {
        db.delete(TABLE_CATEGORIE, null, null);

        ContentValues category = new ContentValues();
        category.put(KEY_CATEGORIA, "ARTE");
        category.put(KEY_CATEGORIA, "CINEMA");
        category.put(KEY_CATEGORIA, "MATEMATICA");
        category.put(KEY_CATEGORIA, "INFORMATICA");
        category.put(KEY_CATEGORIA, "LETTERATURA");
        category.put(KEY_CATEGORIA, "STORIA");
        category.put(KEY_CATEGORIA, "GEOGRAFIA");
        category.put(KEY_CATEGORIA, "MUSICA");

        db.insert(TABLE_CATEGORIE, null, category);

    }
    public void fillQuestionsTable () {
        db.delete(TABLE_DOMANDE, null, null);

        ContentValues domanda = new ContentValues();

        domanda.put(KEY_DOMANDA, "Quanto ne sa Bettoli?");
        domanda.put(KEY_ARGOMENTO, "ARTE");
        domanda.put(KEY_RISPOSTA1, "A pacchi");
        domanda.put(KEY_RISPOSTA2, "22");
        domanda.put(KEY_RISPOSTA3, "Dipende da Sasha");
        domanda.put(KEY_RISPOSTA4, "Chi minchia è Bettoli?");
        domanda.put(KEY_ESATTA, 1);

        db.insert(TABLE_DOMANDE, null, domanda);

    }

}