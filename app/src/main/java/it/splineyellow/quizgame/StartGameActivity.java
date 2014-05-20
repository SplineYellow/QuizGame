package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StartGameActivity extends Activity {

    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    String actualCategory;

    public static final String TAG = "onItemClick --> posizione : ";

    public Integer[] mThumbIds = {};

    String questionData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ConnectionActivity.EXTRA_MESSAGE);
        final String[] categories = message.toLowerCase().split(",");

        mThumbIds = categoriesOrder(categories);

        setTitle("Nuova Partita");

        TextView textView = (TextView) findViewById(R.id.placeholder);
        textView.setText("Turno: " + categories[0]);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "" + position);
                goToQuestion(position, categories);

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

        for (int i = 1; i <= 9; i++) {
            if (categories[i].equals("arte")) categoriesId[i-1] = R.drawable.arte;
            if (categories[i].equals("cinema")) categoriesId[i-1] = R.drawable.cinema;
            if (categories[i].equals("geografia")) categoriesId[i-1] = R.drawable.geografia;
            if (categories[i].equals("informatica")) categoriesId[i-1] = R.drawable.informatica;
            if (categories[i].equals("letteratura")) categoriesId[i-1] = R.drawable.letteratura;
            if (categories[i].equals("matematica")) categoriesId[i-1] = R.drawable.matematica;
            if (categories[i].equals("musica")) categoriesId[i-1] = R.drawable.musica;
            if (categories[i].equals("sport")) categoriesId[i-1] = R.drawable.sport;
            if (categories[i].equals("storia")) categoriesId[i-1] = R.drawable.storia;
        }

        return categoriesId;
    }

    public void goToQuestion (int position, String[] categories) {

        String category = categories[position+1];
        actualCategory = category.substring(0,1).toUpperCase() + category.substring(1);

        Log.v("Actual Category: ", actualCategory);

        new SocketTask().execute();

        Intent intent = new Intent (this, QuestionActivity.class);
        startActivity(intent);
    }

    public class SocketTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatagramSocket ds = null;

            Log.v("SocketTask", "Partito");

            try {
                InetAddress serverAddr = InetAddress.getByName(dstAddress);
                questionData = actualCategory + "," + dstAddress;
                Log.v("INVIANDO: ", questionData);

                /* TODO
                    Ora funziona l'invio della categoria, ma restituisce il seguente errore:
                    [DEBUG] Categoria scelta: Arte,thebertozz.no-ip.org
                    Traceback (most recent call last):
                    File "server.py", line 282, in <module>
                    categoriaScelta = int(categoriaScelta)
                    ValueError: invalid literal for int() with base 10: 'Arte,thebertozz.no-ip.org'
                */

                byte[] buffer = questionData.getBytes();
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

            byte[] receiveBuffer = new byte[4096];

            String[] questions = {};

            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer,
                        receiveBuffer.length, inetAddress, dstPort);
                try {
                    ds.receive(datagramPacket);
                    Log.v("Tentativo ricezione UDP: ", "In ricezione");
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                questions = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split("_");

                Log.v("DOMANDA ARRIVATA: ", questions[0]);
                ds.close();
            }


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}

