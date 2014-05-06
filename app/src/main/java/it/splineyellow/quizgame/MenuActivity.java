package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Francesco on 06/05/2014.
 */

public class MenuActivity extends Activity {


    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    String userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        Button buttonLogout = (Button) findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();

        userData = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] parts = userData.split(",");
        String user = parts[0];
        String password = parts[1];

        setTitle("Utente: " + user);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    MyClientTask myClientTask = new MyClientTask();

    // si attiva con il click su "Nuova partita"
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket ds = null;

            try {
                InetAddress serverAddr = InetAddress.getByName(dstAddress);

                byte[] buffer = userData.getBytes();

                ds = new DatagramSocket();
                DatagramPacket dp;
                dp = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                ds.send(dp);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ds != null) {
                    ds.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }

}
