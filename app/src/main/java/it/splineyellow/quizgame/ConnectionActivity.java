package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;


public class ConnectionActivity extends Activity {

    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    String userData;

    String nick;
    int myID;
    String timestamp;
    String color;
    int turn;
    String[] categories;
    String enemyNick;

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
        new ReceiveTask().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
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

    public class ReceiveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            byte[] data = null;

            InetAddress serverAddr = null;
            DatagramSocket recSocket = null;
            try {
                serverAddr = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                recSocket = new DatagramSocket();
            } catch (SocketException s) {
                s.printStackTrace();
            }
            if (recSocket != null) {
                try {
                    recSocket.setSoTimeout(60);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            DatagramPacket dp;
            byte[] bufferRec = userData.getBytes();
            dp = new DatagramPacket(bufferRec, bufferRec.length, serverAddr, dstPort);
            while (data != null) {
                try {
                    recSocket.receive(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (recSocket != null) {
                        recSocket.close();
                    }
                }
                data = dp.getData();
                String dataString = data.toString();
                String[] resp = dataString.split(",");

                if (resp[0].equals("errore")) {
                    backToMenu();
                } else {
                    nick = resp[0];
                    myID = Integer.parseInt(resp[1]);
                    timestamp = resp[2];

                    color = "rosso";

                    if (myID == 0) {
                        color = "blu";
                    }
                }
                new ReceiveCatTask().execute();

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class ReceiveCatTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params){
            byte[] data = null;

            InetAddress serverAddr = null;
            DatagramSocket recSocket = null;
            try {
                serverAddr = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                recSocket = new DatagramSocket();
            } catch (SocketException s) {
                s.printStackTrace();
            }
            if (recSocket != null) {
                try {
                    recSocket.setSoTimeout(60);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            DatagramPacket dp;
            byte[] bufferRec = userData.getBytes();
            dp = new DatagramPacket(bufferRec, bufferRec.length, serverAddr, dstPort);
            while (data != null) {
                try {
                    recSocket.receive(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (recSocket != null) {
                        recSocket.close();
                    }
                }
                data = dp.getData();
                String gameData = data.toString();
                String[] gameDataCont = gameData.split(",");

                turn = Integer.parseInt(gameDataCont[0]);
                for (int i = 1; i <= 9; i++) {
                    categories[i-1] = gameDataCont[i];
                }
                if (myID == 0) {
                    enemyNick = gameDataCont[11];
                }
                else {
                    enemyNick = gameDataCont[10];
                }

                goToStartGameActivity();

            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void goToStartGameActivity () {
        Intent intent = new Intent(this, StartGameActivity.class);
        String message = nick + "," + enemyNick + "," + color + "," + turn;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void backToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
