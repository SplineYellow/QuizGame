package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class StartGameActivity extends Activity {

    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    int actualCategoryPosition;

    int turn;
    int myID;

    public static final String TAG = "onItemClick --> posizione : ";

    public Integer[] mThumbIds = {};

    String questionData;

    InetAddress serverAddr;

    String message;

    CountDownTimer countDownTimer;

    TextView turnMyIdTextview;

    TextView countdown;

    String[] categories = {};

    boolean punteggioDaRicevere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));



        Intent intent = getIntent();
        try {
            message = intent.getStringExtra(ConnectionActivity.EXTRA_MESSAGE);
        } catch (NullPointerException e) {
            e.printStackTrace();
            message = intent.getStringExtra(ScoreActivity.EXTRA_MESSAGE);
        }

        categories = message.toLowerCase().split(",");

        mThumbIds = categoriesOrder(categories);
        turn = Integer.parseInt(categories[0]);
        myID = Integer.parseInt(categories[1]);

        setTitle("Nuova Partita");

        turnMyIdTextview = (TextView) findViewById(R.id.turn_myID);
        turnMyIdTextview.setText("Tocca a Te");

        countdown = (TextView) findViewById(R.id.countdown);
        if (turn != myID) {

            gridview.setEnabled(false);

            countDownTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {

                    countdown.setText("Secondi rimanenti: " + millisUntilFinished / 1000);
                    turnMyIdTextview.setText("Turno Avversario...");


                }

                public void onFinish() {

                    myID = turn;
                    turnMyIdTextview.setText("Tocca a Te");
                    categories[1] = Integer.toString(myID);

                    gridview.setEnabled(true);

                }

            }.start();
        }


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                new SocketTask().execute();
                actualCategoryPosition = position;
                goToQuestion(categories[actualCategoryPosition+2]);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        MenuItem item = menu.findItem(R.id.action_settings);

        try {
            item.setVisible(false);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);

            imageView.setLayoutParams(new GridView.LayoutParams(
                    (int)mContext.getResources().getDimension(R.dimen.width),
                    (int)mContext.getResources().getDimension(R.dimen.height)));


            return imageView;
        }

    }

    public Integer[] categoriesOrder(String[] categories) {
        Integer[] categoriesId = {R.drawable.arte, R.drawable.cinema, R.drawable.geografia,
                R.drawable.informatica, R.drawable.letteratura, R.drawable.matematica,
                R.drawable.musica, R.drawable.sport, R.drawable.storia};

        for (int i = 2; i <= 10; i++) {
            if (categories[i].equals("arte")) categoriesId[i-2] = R.drawable.arte;
            if (categories[i].equals("cinema")) categoriesId[i-2] = R.drawable.cinema;
            if (categories[i].equals("geografia")) categoriesId[i-2] = R.drawable.geografia;
            if (categories[i].equals("informatica")) categoriesId[i-2] = R.drawable.informatica;
            if (categories[i].equals("letteratura")) categoriesId[i-2] = R.drawable.letteratura;
            if (categories[i].equals("matematica")) categoriesId[i-2] = R.drawable.matematica;
            if (categories[i].equals("musica")) categoriesId[i-2] = R.drawable.musica;
            if (categories[i].equals("sport")) categoriesId[i-2] = R.drawable.sport;
            if (categories[i].equals("storia")) categoriesId[i-2] = R.drawable.storia;
        }

        return categoriesId;
    }

    public void goToQuestion(String i) {

        Intent intent = new Intent (this, QuestionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, i);
        intent.putExtra("Categories", categories[0] + "," + categories[1] + "," + categories[2] + "," +
                categories[3] + "," + categories[4] + "," + categories[5] + "," + categories[6] + "," + categories[7] + "," +
                categories[8] + "," + categories[9] + "," + categories[10]);
        startActivity(intent);
    }

    public class SocketTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            DatagramSocket ds = null;

            try {
                serverAddr = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            questionData = Integer.toString(actualCategoryPosition + 1);
            byte[] buffer = questionData.getBytes();

            if (turn == 2) {

                //IMPLEMENTARE

                Log.v("FINE PARTITA", "fine partita");

            }

            if (punteggioDaRicevere) {


                Log.v("TESTBOOL", "testbool");
                DatagramPacket packet = null;

                try {
                    ds = new DatagramSocket();
                    byte[] receiveBuffer = new byte[8192];
                    try {
                        packet = new DatagramPacket(receiveBuffer, receiveBuffer.length, serverAddr, dstPort);

                        //receive del punteggio
                        Log.v("TESTBOOL", "prima receive");
                        ds.receive(packet);
                        Log.v("TESTBOOL", "dopo receive");
                    }
                    catch (SocketTimeoutException e) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }

                String[] gameData = new String(packet.getData(), 0, packet.getLength()).split(";");
                Log.v("TESTBOOL", "turno: " + gameData[0] + " tabellone: " + gameData[1] + " " + gameData[2]);

                /*turn = Integer.parseInt(gameData[0]);
                String[][] completedBy = gameDataSplitter(gameData[1]);
                String[][] score = gameDataSplitter(gameData[2]);*/

            }

            if (turn == myID) {

                Log.v("TESTIF", "testif");

                try {
                    ds = new DatagramSocket();
                    DatagramPacket dp = null;
                    DatagramPacket packet = null;
                    byte[] receiveBuffer = new byte[8192];
                    dp = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                    ds.setSoTimeout(1000);
                    boolean continueSending = true;
                    int counter = 0;

                    while (continueSending && counter < 1) {

                        //send della categoria

                        ds.send(dp);
                        counter++;
                        try {
                            packet = new DatagramPacket(receiveBuffer, receiveBuffer.length, serverAddr, dstPort);

                            //receive delle domande
                            Log.v("TESTIF", "prima receive");
                            ds.receive(packet);
                            Log.v("TESTIF", "dopo receive");
                            continueSending = false; // a packet has been received : stop sending
                        }
                        catch (SocketTimeoutException e) {
                            // no response received after 1 second. continue sending
                        }

                        String[] gameData = new String(packet.getData(), 0, packet.getLength()).split(";");
                        Log.v("TESTIF", "domanda prova: " + gameData[0]);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                punteggioDaRicevere = true;
            }

            try {
                ds.close();
            } catch (NullPointerException e) {

                e.printStackTrace();

            }
            Log.v("CLOSE", "close");

            return null;

        }

        public String[][] gameDataSplitter (String string) {
            String[][] matrix = new String[3][3];

            String[] stringArray = string.split(":");

            matrix[0] = stringArray[0].split(",");
            matrix[1] = stringArray[1].split(",");
            matrix[2] = stringArray[2].split(",");

            return matrix;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
