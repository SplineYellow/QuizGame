package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

// Copyright SplineYellow - 2014

/*
    Classe per la gestione del tabellone di gioco.
 */
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
            new ReceiveTask().execute();

            gridView.setEnabled(false);

            /*
                CountDownTimer è un contatore che gestisce l'abilitazione dei bottoni presenti
                sul tabellone di gioco.
             */
            countDownTimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    countdown.setText("Secondi rimanenti: " + millisUntilFinished / 1000);

                    turnMyIdTextView.setText("Turno Avversario...");
                }

                public void onFinish() {
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
                new SendTask().execute();

                actualCategoryPosition = position;

                goToQuestion(categories[actualCategoryPosition + 2]);
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

    /*
        ImageAdapter è una classe per la visualizzazione grafica del tabellone di gioco implementata
        tramite GridView di dimensioni 3x3.
     */
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

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
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

    /*
        categoriesOrder() permette di disegnare correttamente le categorie.
     */
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

    /*
        goToQuestion() gestisce la selezione delle domande.
     */
    public void goToQuestion(String i) {
        Intent intent = new Intent (this, QuestionActivity.class);

        intent.putExtra(EXTRA_MESSAGE, i);

        intent.putExtra("Categories", categories[0] + "," + categories[1] + "," + categories[2] + "," +
                categories[3] + "," + categories[4] + "," + categories[5] + "," + categories[6] + "," + categories[7] + "," +
                categories[8] + "," + categories[9] + "," + categories[10]);

        startActivity(intent);
    }

    /*
        SendTask è un AsyncTask per inviare al server la categoria scelta dall'utente tramite
        la pressione del bottone.
     */
    public class SendTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket datagramSocket = null;

            DatagramPacket datagramPacket = null;

            questionData = Integer.toString(actualCategoryPosition + 1);

            byte[] buffer = questionData.getBytes();

            try {
                serverAddr = InetAddress.getByName(dstAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            if (turn == 2) {
                goToEndGame();
            }

            if (turn == myID) {
                try {
                    datagramSocket = new DatagramSocket();
                    
                    datagramSocket.setReuseAddress(true);

                    datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                    boolean continueSending = true;

                    int counter = 0;

                    while (continueSending && counter < 1) {
                        datagramSocket.send(datagramPacket);

                        counter++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                incrementCounter();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*
        ReceiveTask è un AsyncTask per permettere all'utente che non sta giocando di ricevere
        il tabellone aggiornato.
     */
    /*
        TODO
        La receive() indicata è il punto critico del nostro progetto in quanto non funziona.
        Sistemare la ricezione del tabellone permette di completare il programma anche da un
        punto di vista grafico.
     */
    public class ReceiveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket datagramSocket = null;

            DatagramPacket datagramPacket = null;

            byte[] buffer = new byte[4096];

            if (turn != myID) {

                try {
                    serverAddr = InetAddress.getByName(dstAddress);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                try {
                    datagramSocket.receive(datagramPacket);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String firstResponse = new String(datagramPacket.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*
        gameDataSplitter() permette di realizzare il parsing del tabellone per essere visualizzato
        come una matrice 3x3.
     */
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

    public void goToEndGame () {
        Intent intent = new Intent(this, EndGameActivity.class);

        startActivity(intent);
    }
}
