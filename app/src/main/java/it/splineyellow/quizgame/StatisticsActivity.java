package it.splineyellow.quizgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.sql.SQLException;

public class StatisticsActivity extends Activity {

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String currentUserData = db.getCurrentUser();
        String [] data = currentUserData.split(",");
        String currentUser = data[0];

        TextView username = (TextView) findViewById(R.id.statistics_nickname);
        username.setText(currentUser);
        int result = db.getPlayData(currentUser, 1);

        TextView giocate = (TextView) findViewById(R.id.statistics_giocate);
        giocate.setText(Integer.toString(result));
        result = db.getPlayData(currentUser, 2);

        TextView vinte = (TextView) findViewById(R.id.statistics_vinte);
        vinte.setText(Integer.toString(result));
        result = db.getPlayData(currentUser, 3);

        TextView pareggiate = (TextView) findViewById(R.id.statistics_pareggiate);
        pareggiate.setText(Integer.toString(result));
        result = db.getPlayData(currentUser, 4);

        TextView perse = (TextView) findViewById(R.id.statistics_perse);
        perse.setText(Integer.toString(result));
        result = db.getPlayData(currentUser, 5);

        TextView giuste = (TextView) findViewById(R.id.statistics_giuste);
        giuste.setText(Integer.toString(result));
        result = db.getPlayData(currentUser, 6);

        TextView sbagliate = (TextView) findViewById(R.id.statistics_sbagliate);
        sbagliate.setText(Integer.toString(result));

        String access = db.getLastAccess(currentUser);

        String[] accessData = access.split(";");
        access = accessData[0] + " " + accessData[1];

        TextView lastAccess = (TextView) findViewById(R.id.statistics_ultimo_accesso);
        lastAccess.setText(access);
        db.close();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics, menu);
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
    }*/
}
