package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

//Copyright SplineYellow - 2014

public class ConnectionActivity extends Activity {
    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    String dstAddress = "thebertozz.no-ip.org";

    int dstPort = 9533;

    String userData;

    String nick;

    int turn;

    UtentiDatabaseAdapter utentiDatabaseAdapter = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.connection_activity);

        try {
            utentiDatabaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userData = utentiDatabaseAdapter.getCurrentUser();

        utentiDatabaseAdapter.close();

        new MyClientTask().execute();
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
        getMenuInflater().inflate(R.menu.menu, menu);

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

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket datagramSocket = null;

            try {
                InetAddress serverAddr = InetAddress.getByName(dstAddress);

                byte[] buffer = userData.getBytes();

                datagramSocket = new DatagramSocket();

                datagramSocket.setReuseAddress(true);

                DatagramPacket datagramPacket;

                datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                datagramSocket.send(datagramPacket);
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
                    datagramSocket.receive(datagramPacket);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (counter == 0) {
                    firstResponse = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split(",");

                    if (firstResponse[0].equals("errore")) {
                        backToMenu();
                    }

                    nick = firstResponse[0];

                    turn = Integer.parseInt(firstResponse[1]);
                }

                if (counter == 1) {
                    secondResponse = new String (datagramPacket.getData(), 0, datagramPacket.getLength()).split(",");

                    categories = secondResponse;

                    datagramSocket.close();

                    goToStartGameActivity(categories);

                    checkExecute = false;
                }

                counter ++;
            }

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

        String myID = categories[0];

        for (int i = 1; i <= 9; i++) {
            message = message + categories[i];

            if (i < 9) message = message + ",";
        }

        message = Integer.toString(turn) + "," + myID + "," + message;

        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void backToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);

        startActivity(intent);
    }
}
