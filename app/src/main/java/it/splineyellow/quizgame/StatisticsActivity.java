package it.splineyellow.quizgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.SQLException;

// Copyright SplineYellow - 2014

/*
    Classe per la gestione delle statistiche di gioco.
 */
/*
    TODO
    Nel database sono implementati i metodi relativi a questa activity; una volta risolti i problemi
    di ricezione del tabellone, sarà possibile anche aggiornare le varie statistiche relative alle
    partite vinte, perse e pareggiate; alle risposta corrette e sbagliate.
 */
public class StatisticsActivity extends Activity {
    UtentiDatabaseAdapter utentiDatabaseAdapter = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.statistics_activity);

        try {
            utentiDatabaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String currentUserData = utentiDatabaseAdapter.getCurrentUser();

        String [] data = currentUserData.split(",");

        String currentUser = data[0];

        TextView username = (TextView) findViewById(R.id.statistics_nickname);

        username.setText(currentUser);

        int result = utentiDatabaseAdapter.getPlayData(currentUser, 1);

        TextView giocate = (TextView) findViewById(R.id.statistics_giocate);

        giocate.setText(Integer.toString(result));

        result = utentiDatabaseAdapter.getPlayData(currentUser, 2);

        TextView vinte = (TextView) findViewById(R.id.statistics_vinte);

        vinte.setText(Integer.toString(result));

        result = utentiDatabaseAdapter.getPlayData(currentUser, 3);

        TextView pareggiate = (TextView) findViewById(R.id.statistics_pareggiate);

        pareggiate.setText(Integer.toString(result));

        result = utentiDatabaseAdapter.getPlayData(currentUser, 4);

        TextView perse = (TextView) findViewById(R.id.statistics_perse);

        perse.setText(Integer.toString(result));

        result = utentiDatabaseAdapter.getPlayData(currentUser, 5);

        TextView giuste = (TextView) findViewById(R.id.statistics_giuste);

        giuste.setText(Integer.toString(result));

        result = utentiDatabaseAdapter.getPlayData(currentUser, 6);

        TextView sbagliate = (TextView) findViewById(R.id.statistics_sbagliate);

        sbagliate.setText(Integer.toString(result));

        String access = utentiDatabaseAdapter.getLastAccess(currentUser);

        String[] accessData = access.split(";");

        access = accessData[0] + " " + accessData[1];

        TextView lastAccess = (TextView) findViewById(R.id.statistics_ultimo_accesso);

        lastAccess.setText(access);

        utentiDatabaseAdapter.close();
    }
}
