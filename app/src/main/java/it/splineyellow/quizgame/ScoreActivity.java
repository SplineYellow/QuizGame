package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

        Log.v("MYID", Integer.toString(myID));

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

    /*
        Disable "hardware" back button.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);

        /*
            Disable action bar back button.
         */
        try {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem item = menu.findItem(R.id.action_settings);

        /*
            Remove "more action" setting in the action bar.
         */
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

        Log.v(TAG, "Parte intent");

        startActivity(intent);
    }

    public class SendScoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket datagramSocket = null;

            try {
                Log.v(TAG, "Entrato nel try della SendScoreTask");

                InetAddress serverAddr = InetAddress.getByName(dstAddress);

                byte[] buffer = score.getBytes();

                datagramSocket = new DatagramSocket();

                datagramSocket.setReuseAddress(true);

                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                datagramSocket.send(datagramPacket);

                Log.v(TAG, "Dopo la send");

                Log.v(TAG, "inviato punteggio " + score);
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
}