package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;

//Copyright SplineYellow - 2014

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

    TextView turnMyIdTextView;

    TextView countdown;

    String[] categories = {};

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_game);

        final GridView gridView = (GridView) findViewById(R.id.gridview);

        gridView.setAdapter(new ImageAdapter(this));

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

        turnMyIdTextView = (TextView) findViewById(R.id.turn_myID);

        turnMyIdTextView.setText("Tocca a Te");

        countdown = (TextView) findViewById(R.id.countdown);

        if (turn != myID) {

            gridView.setEnabled(false);

            countDownTimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    countdown.setText("Secondi rimanenti: " + millisUntilFinished / 1000);

                    turnMyIdTextView.setText("Turno Avversario...");
                }

                public void onFinish() {

                  //  new ReceiveTask().execute();

                    myID = turn;

                    turnMyIdTextView.setText("Tocca a Te");

                    categories[1] = Integer.toString(myID);

                    gridView.setEnabled(true);

                }

            }.start();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                new SendCategoryTask().execute();

                actualCategoryPosition = position;

                goToQuestion(categories[actualCategoryPosition + 2]);
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

                imageView.setScaleType(ImageView.ScaleType.CENTER);

                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);

            imageView.setLayoutParams(new GridView.LayoutParams((int)mContext.getResources().getDimension(R.dimen.width), (int)mContext.getResources().getDimension(R.dimen.height)));

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

    public class SendCategoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            DatagramSocket datagramSocket = null;

            try {
                serverAddr = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            questionData = Integer.toString(actualCategoryPosition + 1);

            byte[] buffer = questionData.getBytes();

            /*
            if (turn == 2) {
                goToEndGame(); // passare come parametro il risultato della partita
                Log.v("FINE PARTITA", "fine partita");
            }

            if (getBooleanReceivingScore()) {
                Log.v("TESTBOOL", "testbool");

                DatagramPacket datagramPacket = null;
                /*
                try {
                    datagramSocket = new DatagramSocket();


                } catch (SocketException e) {
                    Log.v("CATCH", "eccezione socket");
                    e.printStackTrace();
                }

                byte[] receiveBuffer = new byte[8192];

                setBooleanReceivingScore(false);


                try {

                    Log.v("CREAZIONESOCCA", "Sto per creare una socca");

                    datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, serverAddr, dstPort);

                    //receive del punteggio
                    Log.v("TESTBOOL", "prima della receive");

                    datagramSocket.receive(datagramPacket);

                    Log.v("TESTBOOL", "dopo receive");
                } catch (SocketTimeoutException e) {
                    Log.v("CATCH", "eccezione receive");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.v("CATCH", "eccezione receive");
                    e.printStackTrace();
                }



                String[] gameData = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split(";");

                Log.v("TESTBOOL", "turno: " + gameData[0] + " tabellone: " + gameData[1] + " " + gameData[2]);

                /*turn = Integer.parseInt(gameData[0]);

                String[][] completedBy = gameDataSplitter(gameData[1]);

                String[][] score = gameDataSplitter(gameData[2]);*/

            if (turn == myID) {

                Log.v("TESTIF", "testif");

                try {
                    datagramSocket = new DatagramSocket();

                    DatagramPacket datagramPacket = null;

                    datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                    datagramSocket.setSoTimeout(1000);

                    boolean continueSending = true;

                    int counter = 0;

                    while (continueSending && counter < 1) {
                        Log.v("SEND", "send della categoria");

                        datagramSocket.send(datagramPacket);

                        counter++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                setBooleanReceivingScore(true);
                incrementCounter();
            }



            try {
                datagramSocket.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            Log.v("CLOSE", "close");

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


            DatagramPacket datagramPacket;
            byte[] buffer;
            InetAddress serverAddr;

            try {

                DatagramSocket datagramSocket = new DatagramSocket();

                Log.v("CREAZIONESOCCA", "Sto per creare una socca");

                buffer = new byte[4096];

                serverAddr = InetAddress.getByName(dstAddress);

                datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                //receive del punteggio
                Log.v("TESTBOOL", "prima della receive");

                    datagramSocket.receive(datagramPacket);

                Log.v("TESTBOOL", "dopo receive");
            } catch (SocketTimeoutException e) {
                Log.v("CATCH", "eccezione receive");
                e.printStackTrace();
            } catch (IOException e) {
                Log.v("CATCH", "eccezione receive");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }

    public String[][] gameDataSplitter (String string) {
        String[][] matrix = new String[3][3];

        String[] stringArray = string.split(":");

        matrix[0] = stringArray[0].split(",");

        matrix[1] = stringArray[1].split(",");

        matrix[2] = stringArray[2].split(",");

        return matrix;
    }

    public void setBooleanReceivingScore (boolean value) {
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.setBooleanVariable("receivingScore", value);
        db.close();
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

    public void goToEndGame () { // farsi passare risultato della partita
        Intent intent = new Intent(this, EndGameActivity.class);
        startActivity(intent);
    }

    public void incrementCounter () {
        int oldCounter;
        String varName = "gameCounter";
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        oldCounter = db.getIntegerVariable(varName);
        int newCounter = oldCounter + 1;
        db.setIntegerVariable(varName, newCounter);
    }
}
