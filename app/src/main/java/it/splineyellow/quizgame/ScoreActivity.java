package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;

// Copyright SplineYellow - 2014

/*
    Classe per la gestione del punteggio ottenuto.
 */
public class ScoreActivity extends Activity {
    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    public final static String dstAddress = "thebertozz.no-ip.org";

    public final static int dstPort = 9533;

    public final static String TAG = "ScoreActivity";

    public String score;

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    String message;

    Button okButton;

    String[] categories = {};

    int myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.score_activity);

        Intent intent = getIntent();

        score = intent.getStringExtra(QuestionActivity.EXTRA_MESSAGE);

        message = intent.getStringExtra("Categories");

        categories = message.toLowerCase().split(",");

        myID = Integer.parseInt(categories[1]);

        if (myID == 0) {
            myID = 1;
        }
        else if (myID == 1) {
            myID = 0;
        }

        new SendScoreTask().execute();

        TextView textScore = (TextView) findViewById(R.id.score);

        textScore.setText("Hai totalizzato " + score + " punti!" );

        okButton = (Button) findViewById(R.id.buttonOK);

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.updateScore(db.getCurrentUser(), Integer.parseInt(score));

        db.close();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStartGameActivity();
            }
        });
    }

    /*
        onKeyDown() permette di disabilitare la pressione del BackButton di Android.
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event));
    }

    /*
        onCreateOptionsMenu() permette di disabilitare la visualizzazione del bottone Indietro
        all'interno del programma.
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        try {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /*
        onPrepareOptionsMenu() permette di disabilitare la pressione del tasto Settings di Android.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem item = menu.findItem(R.id.action_settings);

            item.setVisible(false);
        } catch(NullPointerException n) {
            n.printStackTrace();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void goToStartGameActivity () {
        Intent intent = new Intent(this, StartGameActivity.class);

        intent.putExtra(EXTRA_MESSAGE, categories[0] + "," + Integer.toString(myID) + "," + categories[2] + "," +
                categories[3] + "," + categories[4] + "," + categories[5] + "," + categories[6] + "," + categories[7] + "," +
                categories[8] + "," + categories[9] + "," + categories[10]);

        startActivity(intent);
    }

    /*
        SendScoreTask è un AsyncTask per inviare al server il punteggio del giocatore.
     */
    public class SendScoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket datagramSocket = null;

            DatagramPacket datagramPacket;

            byte[] buffer;

            InetAddress serverAddr;

            try {
                serverAddr = InetAddress.getByName(dstAddress);

                buffer = score.getBytes();

                datagramSocket = new DatagramSocket();

                datagramSocket.setReuseAddress(true);

                datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                datagramSocket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
            datagramSocket.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public boolean getBooleanReceivingScore () {
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = db.getBooleanVariable("receivingScore");

        db.close();

        return value;
    }

    public int getGameCounter () {
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int counter = db.getIntegerVariable("gameCounter");

        db.close();

        return counter;
    }
}