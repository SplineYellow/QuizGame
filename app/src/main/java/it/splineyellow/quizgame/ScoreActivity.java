package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;


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
    int turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);

        Intent intent = getIntent();
        score = intent.getStringExtra(QuestionActivity.EXTRA_MESSAGE);
        message = intent.getStringExtra("Categories");
        categories = message.toLowerCase().split(",");

        turn = Integer.parseInt(categories[0]);

        if (turn == 0) {

            turn = 1;
        }

        else if (turn == 1) {

            turn = 0;
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

        Log.v(TAG, "Scritto nel db");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(TAG, "Parte AsyncTask");

                goToStartGameActivity();
            }
        });
    }

    public void goToStartGameActivity () {

        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, Integer.toString(turn) + "," + categories[1] + "," + categories[2] + "," +
        categories[3] + "," + categories[4] + "," + categories[5] + "," + categories[6] + "," + categories[7] + "," +
        categories[8] + "," + categories[9] + "," + categories[10]);

        Log.v(TAG, "Parte intent");

        startActivity(intent);
    }

    public class SendScoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            DatagramSocket ds = null;

            try {
                Log.v(TAG, "Entrato nel try della SendScoreTask");
                InetAddress serverAddr = InetAddress.getByName(dstAddress);
                byte[] buffer = score.getBytes();
                ds = new DatagramSocket();
                ds.setReuseAddress(true);
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);
                ds.send(dp);
                Log.v(TAG, "Dopo la send");
                Log.v(TAG, "inviato punteggio " + score);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ds.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
