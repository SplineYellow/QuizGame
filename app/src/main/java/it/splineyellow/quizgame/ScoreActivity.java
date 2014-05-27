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

    public String score;

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);
    String message;
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);

        Intent intent = getIntent();
        score = intent.getStringExtra(QuestionActivity.EXTRA_MESSAGE);
        message = intent.getStringExtra("Categories");

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

        //SEND AL SERVER
    }

    public void goToStartGameActivity () {

        new SendScoreTask().execute();

        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public class SendScoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket ds = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(dstAddress);
                byte[] buffer = score.getBytes();
                ds = new DatagramSocket();
                DatagramPacket dp;
                dp = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);
                ds.send(dp);
                Log.v("Send score", "inviato punteggio " + score);
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
