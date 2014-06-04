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

    public String[][] getQuestions (String category) {

        category = category.toUpperCase();
        String [][] questionByCategory = new String[QUESTION_NUMBER][6];

        String query = "SELECT " + KEY_DOMANDA + ", " + KEY_RISPOSTA1 + ", " +
                KEY_RISPOSTA2 + ", " + KEY_RISPOSTA3 + ", " + KEY_RISPOSTA4 + ", " + KEY_ESATTA +
                " FROM " + TABLE_DOMANDE +
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
                questionByCategory[i][0] = c.getString(0);
                questionByCategory[i][1] = c.getString(1);
                questionByCategory[i][2] = c.getString(2);
                questionByCategory[i][3] = c.getString(3);
                questionByCategory[i][4] = c.getString(4);
                questionByCategory[i][5] = Integer.toString(c.getInt(5));

                Log.v("Stampa questionByCategory: ", c.getString(0) + " " + c.getString(1) + " " + c.getString(2) +
                " " + c.getString(3) + " " + c.getString(4) + " " + c.getString(5));

                c.moveToNext();
            }
        }

        String [] firstQuestion = questionByCategory[rand1];
        String [] secondQuestion = questionByCategory[rand2];
        String [] thirdQuestion = questionByCategory[rand3];

        String[][] questionsToSend = new String[3][6];
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

        String argomento;

        ContentValues domanda = new ContentValues();

        // ARTE ============================================
        argomento = "ARTE";

        domanda.put(KEY_DOMANDA, "Chi ha dipinto la Cappella Sistina?\n");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Raffaello");
        domanda.put(KEY_RISPOSTA2, "Michelangelo");
        domanda.put(KEY_RISPOSTA3, "Leonardo");
        domanda.put(KEY_RISPOSTA4, "Perugino");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Verso che periodo si e' sviluppato il barocco?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "1600");
        domanda.put(KEY_RISPOSTA2, "1700");
        domanda.put(KEY_RISPOSTA3, "1500");
        domanda.put(KEY_RISPOSTA4, "1800");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Per cosa e' famoso Brunelleschi?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Pittura");
        domanda.put(KEY_RISPOSTA2, "Scultura");
        domanda.put(KEY_RISPOSTA3, "Archittettura");
        domanda.put(KEY_RISPOSTA4, "Incisione");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Nel Rinascimento ci fu:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "La riscoperta dell'arte etrusca");
        domanda.put(KEY_RISPOSTA2, "La riscoperta dell'arte classica");
        domanda.put(KEY_RISPOSTA3, "La riscoperta dell'arte bizantina");
        domanda.put(KEY_RISPOSTA4, "La riscoperta del manierismo");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Edgar Degas dipingeva soprattutto:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Ballerini");
        domanda.put(KEY_RISPOSTA2, "Uomini di prestigio");
        domanda.put(KEY_RISPOSTA3, "Uomini di Chiesa");
        domanda.put(KEY_RISPOSTA4, "Contadini");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il vero nome di Caravaggio era:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Michelangelo Buonarroti");
        domanda.put(KEY_RISPOSTA2, "Michelangelo Martini");
        domanda.put(KEY_RISPOSTA3, "Michelangelo Carracci");
        domanda.put(KEY_RISPOSTA4, "Michelangelo Merisi");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il Parmigianino era di:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Roma");
        domanda.put(KEY_RISPOSTA2, "Firenze");
        domanda.put(KEY_RISPOSTA3, "Parma");
        domanda.put(KEY_RISPOSTA4, "Padova");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il pittore piu' apprezzato dai Papi nel 500 era:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Giorgione da Castelfranco");
        domanda.put(KEY_RISPOSTA2, "Raffello Sanzio");
        domanda.put(KEY_RISPOSTA3, "Paolo Veronese");
        domanda.put(KEY_RISPOSTA4, "Correggio");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "L'allievo di Giorgione da Castelfranco era:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Tiziano Vecellio");
        domanda.put(KEY_RISPOSTA2, "Pontormo");
        domanda.put(KEY_RISPOSTA3, "Lorenzo Lotto");
        domanda.put(KEY_RISPOSTA4, "Dosso Dossi");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Benvenuto Cellini era:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Pittore");
        domanda.put(KEY_RISPOSTA2, "Architetto");
        domanda.put(KEY_RISPOSTA3, "Scultore");
        domanda.put(KEY_RISPOSTA4, "Scrittore");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Chi fu il precursore dei manga in Giappone?\n");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Ikki Kajiwara");
        domanda.put(KEY_RISPOSTA2, "Riyoko Ikeda");
        domanda.put(KEY_RISPOSTA3, "Katsushika Hokusai");
        domanda.put(KEY_RISPOSTA4, "Eiichiro Oda");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Pablo Picasso era di nazionalita':");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Portoghese");
        domanda.put(KEY_RISPOSTA2, "Spagnola");
        domanda.put(KEY_RISPOSTA3, "Francese");
        domanda.put(KEY_RISPOSTA4, "Brasiliana");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "I colori acrilici:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Asciugano lentamente e si applicano su molte superfici");
        domanda.put(KEY_RISPOSTA2, "Asciugano velocemente e si applicano solo su carta");
        domanda.put(KEY_RISPOSTA3, "Asciugano velocemente e si applicano su molte superfici");
        domanda.put(KEY_RISPOSTA4, "Asciugano lentamente e si applicano solo su carta");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Gli acquarelli si diluiscono con:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Acqua");
        domanda.put(KEY_RISPOSTA2, "Olio di lino");
        domanda.put(KEY_RISPOSTA3, "Acquaragia");
        domanda.put(KEY_RISPOSTA4, "Olio Extravergine");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quali sono i colori primari?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Rosso, Verde e Blu");
        domanda.put(KEY_RISPOSTA2, "Rosso, Giallo e Blu");
        domanda.put(KEY_RISPOSTA3, "Verde, Viola e Arancione");
        domanda.put(KEY_RISPOSTA4, "Bianco e Nero");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        // CINEMA =============================

        argomento = "CINEMA";

        domanda.put(KEY_DOMANDA, "Chi e' il regista del film La Migliore Offerta?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Tornatore");
        domanda.put(KEY_RISPOSTA2, "Morricone");
        domanda.put(KEY_RISPOSTA3, "Muccino");
        domanda.put(KEY_RISPOSTA4, "Spielberg");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Nel film Il Grande Gatsby(2013) chi e' l'attore protagonista?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Tom Cruise");
        domanda.put(KEY_RISPOSTA2, "Danny De Vito");
        domanda.put(KEY_RISPOSTA3, "Will Smith");
        domanda.put(KEY_RISPOSTA4, "Leonardo di Caprio");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Chi ha vinto il premio Oscar nel 2010 come Miglior Film?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Avatar");
        domanda.put(KEY_RISPOSTA2, "Bastardi senza Gloria");
        domanda.put(KEY_RISPOSTA3, "The Hurt Locker");
        domanda.put(KEY_RISPOSTA4, "Up");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quanti premi Oscar ha vinto Leonardo di Caprio?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "0");
        domanda.put(KEY_RISPOSTA2, "1");
        domanda.put(KEY_RISPOSTA3, "2");
        domanda.put(KEY_RISPOSTA4, "3");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quali tra questi film di James Cameron e' il meno recente?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Aliens");
        domanda.put(KEY_RISPOSTA2, "Titanic");
        domanda.put(KEY_RISPOSTA3, "Avatar");
        domanda.put(KEY_RISPOSTA4, "Terminator");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il protagonista di Edward Mani di Forbice?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Robert Downey Jr.");
        domanda.put(KEY_RISPOSTA2, "Jake Gyllenhaal");
        domanda.put(KEY_RISPOSTA3, "Johnny Depp");
        domanda.put(KEY_RISPOSTA4, "Patrick Swayze");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Interpreto' Joker in Il Cavaliere Oscuro poco prima di morire:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Jack Nicholson");
        domanda.put(KEY_RISPOSTA2, "Heath Ledger");
        domanda.put(KEY_RISPOSTA3, "Patrick Swayze");
        domanda.put(KEY_RISPOSTA4, "Paul Bettany");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Premio Oscar al Miglior Attore del 2001:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Russell Crowe - Il gladiatore");
        domanda.put(KEY_RISPOSTA2, "Tom Hanks - Cast Away");
        domanda.put(KEY_RISPOSTA3, "Ed Harris - Pollock");
        domanda.put(KEY_RISPOSTA4, "Javier Bardem - Prima che sia notte");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Premio Oscar alla Migliore Attrice del 2013:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Jessica Chastain - Zero Dark Thirty");
        domanda.put(KEY_RISPOSTA2, "Emmanuelle Riva - Amour");
        domanda.put(KEY_RISPOSTA3, "Jennifer Lawrence - Il lato positivo");
        domanda.put(KEY_RISPOSTA4, "Naomi Watts - The Impossible");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "La famosa Regina Amidala di Star Wars:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Keira Knightley");
        domanda.put(KEY_RISPOSTA2, "Natalie Portman");
        domanda.put(KEY_RISPOSTA3, "Penelope Cruz");
        domanda.put(KEY_RISPOSTA4, "Rebecca Hall");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Che personaggio Marvel interpreta Scarlett Johansson?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "La Donna Invisibile");
        domanda.put(KEY_RISPOSTA2, "Tempesta");
        domanda.put(KEY_RISPOSTA3, "Vedova Nera");
        domanda.put(KEY_RISPOSTA4, "Mystica");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        // GEOGRAFIA ====================================

        argomento = "GEOGRAFIA";

        domanda.put(KEY_DOMANDA, "La capitale del Giappone:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Osaka");
        domanda.put(KEY_RISPOSTA2, "Kyoto");
        domanda.put(KEY_RISPOSTA3, "Tokyo");
        domanda.put(KEY_RISPOSTA4, "Fukushima");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il fiume principale di Roma:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Po");
        domanda.put(KEY_RISPOSTA2, "Tevere");
        domanda.put(KEY_RISPOSTA3, "Aniene");
        domanda.put(KEY_RISPOSTA4, "Danubio");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Qual e' la lingua piu' parlata nel mondo?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Spagnolo");
        domanda.put(KEY_RISPOSTA2, "Inglese");
        domanda.put(KEY_RISPOSTA3, "Francese");
        domanda.put(KEY_RISPOSTA4, "Cinese");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Che lingua si parla in Brasile?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Brasiliano");
        domanda.put(KEY_RISPOSTA2, "Portoghese");
        domanda.put(KEY_RISPOSTA3, "Spagnolo");
        domanda.put(KEY_RISPOSTA4, "Inglese");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quanti stati compongono l'USA?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "45");
        domanda.put(KEY_RISPOSTA2, "50");
        domanda.put(KEY_RISPOSTA3, "55");
        domanda.put(KEY_RISPOSTA4, "40");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Dove si trova il lago Chad?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Africa");
        domanda.put(KEY_RISPOSTA2, "America del Sud");
        domanda.put(KEY_RISPOSTA3, "Oceania");
        domanda.put(KEY_RISPOSTA4, "Antartide");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Qual e' il lago piu' grande d'Italia?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Lago di Como");
        domanda.put(KEY_RISPOSTA2, "Lago Maggiore");
        domanda.put(KEY_RISPOSTA3, "Lago Trasimeno");
        domanda.put(KEY_RISPOSTA4, "Lago di Garda");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quanti sono i laghi che compongono i Grandi Laghi in America?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "2");
        domanda.put(KEY_RISPOSTA2, "4");
        domanda.put(KEY_RISPOSTA3, "5");
        domanda.put(KEY_RISPOSTA4, "7");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "In che continente si trova lo Zimbabwe?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Asia");
        domanda.put(KEY_RISPOSTA2, "Europa");
        domanda.put(KEY_RISPOSTA3, "Antartide");
        domanda.put(KEY_RISPOSTA4, "Africa");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Le mucche sono sacre in?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "India");
        domanda.put(KEY_RISPOSTA2, "Giappone");
        domanda.put(KEY_RISPOSTA3, "Cina");
        domanda.put(KEY_RISPOSTA4, "Kenya");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Gli hamburger di squalo sono un tipico piatto:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Thailandese");
        domanda.put(KEY_RISPOSTA2, "Giapponese");
        domanda.put(KEY_RISPOSTA3, "Australiano");
        domanda.put(KEY_RISPOSTA4, "Cinese");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        // INFORMATICA ======================================

        argomento = "INFORMATICA";

        domanda.put(KEY_DOMANDA, "Nel caso peggiore il costo dell'algoritmo di ordinamento quicksort?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "O(log n)");
        domanda.put(KEY_RISPOSTA2, "O(n log n)");
        domanda.put(KEY_RISPOSTA3, "O(n^2)");
        domanda.put(KEY_RISPOSTA4, "O(log n^2)");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quale dei dei seguenti non e' un algoritmo di scheduling della CPU?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Shortest seek time first");
        domanda.put(KEY_RISPOSTA2, "Short job first");
        domanda.put(KEY_RISPOSTA3, "Round Robin");
        domanda.put(KEY_RISPOSTA4, "Priorità");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "I trigger seguono il paradigma:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "azione-evento-condizione");
        domanda.put(KEY_RISPOSTA2, "evento-azione-condizione");
        domanda.put(KEY_RISPOSTA3, "evento-condizione-azione");
        domanda.put(KEY_RISPOSTA4, "condizione-evento-azione");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "L'operatore sizeof serve per:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Conoscere la dimensione in byte di una variabile.");
        domanda.put(KEY_RISPOSTA2, "Conoscere la dimensione in byte di un tipo di dato.");
        domanda.put(KEY_RISPOSTA3, "Conoscere la dimensione in Byte di una variabile e di un byte.");
        domanda.put(KEY_RISPOSTA4, "nessuna delle tre risposte");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "In C++ l'operazione di conversione di un tipo di dato in altro tipo di dato si dice:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "casst");
        domanda.put(KEY_RISPOSTA2, "casting");
        domanda.put(KEY_RISPOSTA3, "conversion");
        domanda.put(KEY_RISPOSTA4, "caasting");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "La paginazione e' un tipo di allocazione:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "contigua");
        domanda.put(KEY_RISPOSTA2, "non contigua");
        domanda.put(KEY_RISPOSTA3, "fissa");
        domanda.put(KEY_RISPOSTA4, "a volte sì a volte no, a volte chi lo sa");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il comando cat:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Concatena un file");
        domanda.put(KEY_RISPOSTA2, "Visualizza un file");
        domanda.put(KEY_RISPOSTA3, "Concatena e visualizza un file");
        domanda.put(KEY_RISPOSTA4, "nessuna delle tre");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        // LETTERATURA ======================================

        argomento = "LETTERATURA";

        domanda.put(KEY_DOMANDA, "Chi fu Erodoto?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Uno storico");
        domanda.put(KEY_RISPOSTA2, "Un aedo");
        domanda.put(KEY_RISPOSTA3, "Un rapsode");
        domanda.put(KEY_RISPOSTA4, "Un re");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quando nacque la letteratura latina e con chi?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "240a.C. Livio Andronico");
        domanda.put(KEY_RISPOSTA2, "100d.C. Cicerone");
        domanda.put(KEY_RISPOSTA3, "150a.C. Plauto");
        domanda.put(KEY_RISPOSTA4, "0 Seneca");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Chi e' l'autore dell'Odusia?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Cesare");
        domanda.put(KEY_RISPOSTA2, "Cicerone");
        domanda.put(KEY_RISPOSTA3, "Livio Andronico");
        domanda.put(KEY_RISPOSTA4, "Gneo Nevio");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Per il filosofo Talete che cos'era l'origine di tutto?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Dio");
        domanda.put(KEY_RISPOSTA2, "Acqua");
        domanda.put(KEY_RISPOSTA3, "Fuoco");
        domanda.put(KEY_RISPOSTA4, "Terra");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Chi fu il grande amore del Petrarca?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Chuck Norris");
        domanda.put(KEY_RISPOSTA2, "Laura");
        domanda.put(KEY_RISPOSTA3, "Beatrice");
        domanda.put(KEY_RISPOSTA4, "Lucia");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Come inizia il romanzo \"I Promessi Sposi\"?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Quel ramo del lago di Garda...");
        domanda.put(KEY_RISPOSTA2, "Nel mezzo del cammin...");
        domanda.put(KEY_RISPOSTA3, "Come ogni mattina Renzi...");
        domanda.put(KEY_RISPOSTA4, "Quel ramo del lago di Como...");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Chi e' la guida di Dante nell'Inferno?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Cicerone");
        domanda.put(KEY_RISPOSTA2, "Beatrice");
        domanda.put(KEY_RISPOSTA3, "Virgilio");
        domanda.put(KEY_RISPOSTA4, "Brunetto Latini");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Cavalcanti era uno scrittore:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Post-moderno");
        domanda.put(KEY_RISPOSTA2, "Stilnovista");
        domanda.put(KEY_RISPOSTA3, "Cortese");
        domanda.put(KEY_RISPOSTA4, "Illuminista");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Federigo Degli Alberighi dona a Monna Giovanna:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Un pezzo di pane");
        domanda.put(KEY_RISPOSTA2, "Una casa");
        domanda.put(KEY_RISPOSTA3, "Un falchetto");
        domanda.put(KEY_RISPOSTA4, "Un cavallo");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Nella Divina Commedia Virgilio e' allegoria di:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Sapienza");
        domanda.put(KEY_RISPOSTA2, "Saggezza");
        domanda.put(KEY_RISPOSTA3, "Teologia");
        domanda.put(KEY_RISPOSTA4, "Ragione");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Cavalcanti era:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Cattolico");
        domanda.put(KEY_RISPOSTA2, "Ateo");
        domanda.put(KEY_RISPOSTA3, "Protestante");
        domanda.put(KEY_RISPOSTA4, "Calvinista");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il filosofo di Dante e':");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Talete");
        domanda.put(KEY_RISPOSTA2, "Averroè");
        domanda.put(KEY_RISPOSTA3, "Agostino");
        domanda.put(KEY_RISPOSTA4, "San Tommaso");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);domanda.put(KEY_DOMANDA, "");

        domanda.put(KEY_DOMANDA, "Chi sono i protagonisti della I Bucolica:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Amarillide e Galatea");
        domanda.put(KEY_RISPOSTA2, "Titiro e Melibeo");
        domanda.put(KEY_RISPOSTA3, "Amerillide e Titiro");
        domanda.put(KEY_RISPOSTA4, "Galatea e Melibeo");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Socrate diceva:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Non so di sapere");
        domanda.put(KEY_RISPOSTA2, "So di non sapere");
        domanda.put(KEY_RISPOSTA3, "Non so di non sapere");
        domanda.put(KEY_RISPOSTA4, "Rosso di sera bel tempo si spera");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Aristotele riconosceva l'essere con:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Materia");
        domanda.put(KEY_RISPOSTA2, "Dio");
        domanda.put(KEY_RISPOSTA3, "Cielo");
        domanda.put(KEY_RISPOSTA4, "Terra");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        // MATEMATICA ======================================

        argomento = "MATEMATICA";

        domanda.put(KEY_DOMANDA, "Quanto fa 23/24-5/36 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "59/72");
        domanda.put(KEY_RISPOSTA2, "56/62");
        domanda.put(KEY_RISPOSTA3, "58/71");
        domanda.put(KEY_RISPOSTA4, "59/70");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Risolvi la seguente proporzione x:2/65=52/5:4");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "3/26");
        domanda.put(KEY_RISPOSTA2, "2/25");
        domanda.put(KEY_RISPOSTA3, "7/24");
        domanda.put(KEY_RISPOSTA4, "3/25");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il valore della seguente espressione: 59-(17-5-4)*2+13-(25-3*5) ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "46");
        domanda.put(KEY_RISPOSTA2, "39");
        domanda.put(KEY_RISPOSTA3, "43");
        domanda.put(KEY_RISPOSTA4, "47");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Cosa fa la radice terza 1728 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "(3*2^2)^3");
        domanda.put(KEY_RISPOSTA2, "3^3*2^6");
        domanda.put(KEY_RISPOSTA3, "12^2");
        domanda.put(KEY_RISPOSTA4, "12");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Cosa fa 1.4^2 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "1.16");
        domanda.put(KEY_RISPOSTA2, "1.96");
        domanda.put(KEY_RISPOSTA3, "1.86");
        domanda.put(KEY_RISPOSTA4, "1.06");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il numero binario corrispondente al numero decimale 1348 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "10101000101");
        domanda.put(KEY_RISPOSTA2, "11101000100");
        domanda.put(KEY_RISPOSTA3, "10101000100");
        domanda.put(KEY_RISPOSTA4, "10100100010");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Sapendo che 42*5=210, quanto fa 47*5 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "210+(42*5)");
        domanda.put(KEY_RISPOSTA2, "210+(210*5)");
        domanda.put(KEY_RISPOSTA3, "210+(5*5)");
        domanda.put(KEY_RISPOSTA4, "210+(5*5*5)");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il valore della seguente espressione: 44/11+48+12-28-15*2 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "2+3+1");
        domanda.put(KEY_RISPOSTA2, "6");
        domanda.put(KEY_RISPOSTA3, "3+3");
        domanda.put(KEY_RISPOSTA4, "2*3");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Scomponi in fattori primi il numero: 819");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "3^2*7*13");
        domanda.put(KEY_RISPOSTA2, "3^3*8*13");
        domanda.put(KEY_RISPOSTA3, "3^4*9*13");
        domanda.put(KEY_RISPOSTA4, "3^2*10*13");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Cosa fa la seguente moltiplicazione: 39/75*52/26 ?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "157/150");
        domanda.put(KEY_RISPOSTA2, "79/75");
        domanda.put(KEY_RISPOSTA3, "26/25");
        domanda.put(KEY_RISPOSTA4, "157/151");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        // MUSICA ======================================

        argomento = "MUSICA";

        domanda.put(KEY_DOMANDA, "Quanti Grammy Awards ha vinto la cantante Beyonce'?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "7");
        domanda.put(KEY_RISPOSTA2, "17");
        domanda.put(KEY_RISPOSTA3, "24");
        domanda.put(KEY_RISPOSTA4, "60");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Da chi era composto il gruppo inglese The Beatles?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Lennon, Hendrix");
        domanda.put(KEY_RISPOSTA2, "McCartney, Lennon, Harrison e Cobain");
        domanda.put(KEY_RISPOSTA3, "Starr, McCartney, Harrison e Lennon");
        domanda.put(KEY_RISPOSTA4, "Lennon, Starr, Jackson e McCartney");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quando e' morto Michael Jackson?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "27 Agosto 2005");
        domanda.put(KEY_RISPOSTA2, "25 Giugno 2009");
        domanda.put(KEY_RISPOSTA3, "24 Luglio 2009");
        domanda.put(KEY_RISPOSTA4, "20 Maggio 2010");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Lady Gaga e' una cantante americana di origini?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Francesi");
        domanda.put(KEY_RISPOSTA2, "Tedesche");
        domanda.put(KEY_RISPOSTA3, "Spagnole");
        domanda.put(KEY_RISPOSTA4, "Italiane");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Di che nazionalità è la cantante Zaz?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Francese");
        domanda.put(KEY_RISPOSTA2, "Canadese");
        domanda.put(KEY_RISPOSTA3, "Americana");
        domanda.put(KEY_RISPOSTA4, "Marocchina");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        // SPORT ======================================

        argomento = "SPORT";

        domanda.put(KEY_DOMANDA, "I primi giochi olimpici si svolsero:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Nel 1240a.C. ad Atene");
        domanda.put(KEY_RISPOSTA2, "Nel 1034a.C. a Maratona");
        domanda.put(KEY_RISPOSTA3, "Nel 776a.C. ad Olimpia");
        domanda.put(KEY_RISPOSTA4, "Nel 480a.C. alle Termopili");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quanti titoli mondiali ha conquistato Michael Schumacher?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "3");
        domanda.put(KEY_RISPOSTA2, "5");
        domanda.put(KEY_RISPOSTA3, "7");
        domanda.put(KEY_RISPOSTA4, "9");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "La nazione che ha vinto piu' mondiali di calcio:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Italia");
        domanda.put(KEY_RISPOSTA2, "Germania");
        domanda.put(KEY_RISPOSTA3, "Brasile");
        domanda.put(KEY_RISPOSTA4, "Spagna");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "La nazionalita' del famoso tennista Pete Sampras:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Australiana");
        domanda.put(KEY_RISPOSTA2, "Statunitense");
        domanda.put(KEY_RISPOSTA3, "Inglese");
        domanda.put(KEY_RISPOSTA4, "Canadese");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il record del mondo di salto in alto di J.Sotomayor e' di ben:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "2,01m");
        domanda.put(KEY_RISPOSTA2, "2,10m");
        domanda.put(KEY_RISPOSTA3, "2,34m");
        domanda.put(KEY_RISPOSTA4, "2,35m");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Tiratrice italiana di tiro a volo medaglia d'oro a Londra 2012");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Jessica Rossi");
        domanda.put(KEY_RISPOSTA2, "Marta Bianchi");
        domanda.put(KEY_RISPOSTA3, "Sara Fossati");
        domanda.put(KEY_RISPOSTA4, "Michaela Rosati");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quante medaglie d'oro ha vinto l'Italia di pallavolo?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "0");
        domanda.put(KEY_RISPOSTA2, "1");
        domanda.put(KEY_RISPOSTA3, "2");
        domanda.put(KEY_RISPOSTA4, "3");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quante medaglie d'oro olimpiche ha vinto Valentina Vezzali?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "3");
        domanda.put(KEY_RISPOSTA2, "4");
        domanda.put(KEY_RISPOSTA3, "6");
        domanda.put(KEY_RISPOSTA4, "7");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "La squadra che ha vinto piu' Champions League:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Milan");
        domanda.put(KEY_RISPOSTA2, "Real Madrid");
        domanda.put(KEY_RISPOSTA3, "Barcellona");
        domanda.put(KEY_RISPOSTA4, "Vallespluga");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        // STORIA ======================================

        argomento = "STORIA";

        domanda.put(KEY_DOMANDA, "Chi fu l'ultimo Re di Roma?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Tarquinio il Superbo");
        domanda.put(KEY_RISPOSTA2, "Servio Tullio");
        domanda.put(KEY_RISPOSTA3, "Anco Marzio");
        domanda.put(KEY_RISPOSTA4, "Tarquinio Prisco");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Dove nacque Napoleone?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Parigi");
        domanda.put(KEY_RISPOSTA2, "Ajaccio");
        domanda.put(KEY_RISPOSTA3, "Nizza");
        domanda.put(KEY_RISPOSTA4, "Fidenza");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Anno di inizio della Prima Guerra Mondiale:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "1918");
        domanda.put(KEY_RISPOSTA2, "1910");
        domanda.put(KEY_RISPOSTA3, "1912");
        domanda.put(KEY_RISPOSTA4, "1914");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "In che anno cadde il Muro di Berlino?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "1956");
        domanda.put(KEY_RISPOSTA2, "1965");
        domanda.put(KEY_RISPOSTA3, "1974");
        domanda.put(KEY_RISPOSTA4, "1989");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Il nome del cavallo di Alessandro Magno:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Pegaso");
        domanda.put(KEY_RISPOSTA2, "Bucefalo");
        domanda.put(KEY_RISPOSTA3, "Marengo");
        domanda.put(KEY_RISPOSTA4, "Nearco");
        domanda.put(KEY_ESATTA, 2);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Quando nacque Carlo V?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "1492");
        domanda.put(KEY_RISPOSTA2, "1400");
        domanda.put(KEY_RISPOSTA3, "1500");
        domanda.put(KEY_RISPOSTA4, "1512");
        domanda.put(KEY_ESATTA, 3);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Carlo V era nipote di?");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Cristoforo Colombo");
        domanda.put(KEY_RISPOSTA2, "Francesco II Sforza");
        domanda.put(KEY_RISPOSTA3, "Roberto D'Angiò");
        domanda.put(KEY_RISPOSTA4, "Massimiliano D'Asburgo");
        domanda.put(KEY_ESATTA, 4);
        db.insert(TABLE_DOMANDE, null, domanda);

        domanda.put(KEY_DOMANDA, "Famosa dea egizia dalla forma di gatto:");
        domanda.put(KEY_ARGOMENTO, argomento);
        domanda.put(KEY_RISPOSTA1, "Bastet");
        domanda.put(KEY_RISPOSTA2, "Nefti");
        domanda.put(KEY_RISPOSTA3, "Iside");
        domanda.put(KEY_RISPOSTA4, "Tueret");
        domanda.put(KEY_ESATTA, 1);
        db.insert(TABLE_DOMANDE, null, domanda);


    }

}