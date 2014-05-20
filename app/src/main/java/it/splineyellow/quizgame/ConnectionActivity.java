package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;


public class ConnectionActivity extends Activity {

    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    String userData;

    String nick;
    int turn;


    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_activity);

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userData = db.getCurrentUser();
        db.close();

        new MyClientTask().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
            }

            InetAddress inetAddress = null;

            try {
                inetAddress = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            Boolean checkExecute = true;
            String[] firstResponse;
            String[] secondResponse;
            String[] categories = {};
            byte[] receiveBuffer = new byte[4096];
            int counter = 0;

            while (checkExecute && counter < 2) {
                DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, inetAddress, dstPort);

                try {
                    ds.receive(datagramPacket);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (counter == 0) {
                    firstResponse = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split(",");
                    if (firstResponse[0].equals("errore")) {
                        Toast t = Toast.makeText(getApplicationContext(), "Errore di connessione", Toast.LENGTH_LONG);
                        t.show();
                        backToMenu();
                    }
                    nick = firstResponse[0];
                    turn = Integer.parseInt(firstResponse[1]);
                 }
                if (counter == 1) {
                    secondResponse = new String (datagramPacket.getData(), 0, datagramPacket.getLength()).split(",");
                    for (int i = 0; i <= 9; i++) {
                        // categories [0] ---> turn
                        categories = secondResponse;
                    }
                    goToStartGameActivity(categories);
                    checkExecute = false;
                }

                counter ++;
            }

            ds.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void goToStartGameActivity (String[] categories) {
        Intent intent = new Intent(this, StartGameActivity.class);

        String message = "";

        for (int i = 1; i <= 9; i++) {
            message = message + categories[i];
            if (i < 9) message = message + ",";
        }

        message =  Integer.toString(turn) + "," + message;

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void backToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
