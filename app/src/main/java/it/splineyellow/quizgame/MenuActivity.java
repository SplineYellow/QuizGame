package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.sql.SQLException;

//Copyright SplineYellow - 2014

public class MenuActivity extends Activity {
    String userData;

    UtentiDatabaseAdapter utentiDatabaseAdapter = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_activity);

        Button buttonNewGame = (Button) findViewById(R.id.button_new_game);

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConnection();
            }
        });

        Button buttonListGame = (Button) findViewById(R.id.button_list_games);

        buttonListGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                goToListGames();
            }
        });

        Button buttonStatistics = (Button) findViewById(R.id.button_statistics);

        buttonStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStatistics();
            }
        });

        Button buttonLogout = (Button) findViewById(R.id.button_logout);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            utentiDatabaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userData = utentiDatabaseAdapter.getCurrentUser();

        utentiDatabaseAdapter.close();

        String[] parts = userData.split(",");

        String user = parts[0];

        setTitle("Utente: " + user);
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

    private void goToConnection() {
        Intent intent = new Intent(this, ConnectionActivity.class);

        startActivity(intent);
    }

    private void goToListGames() {
        Intent intent = new Intent(this, ListGamesActivity.class);

        startActivity(intent);
    }

    private void goToStatistics () {
        Intent intent = new Intent(this, StatisticsActivity.class);

        startActivity(intent);
    }
}
