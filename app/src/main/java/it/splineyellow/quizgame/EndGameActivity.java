package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLException;

// Copyright SplineYellow - 2014

/*
    Classe per la gestione della fine della partita.
 */
/*
    TODO
    Dato che non si riesce a ricevere il tabellone aggiornato dal Server, per i problemi esposti,
    questa classe è stata realizzata per una futura implementazione.
    Attualmente non è raggiungibile dal programma.
 */
public class EndGameActivity extends Activity {
    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.end_game_activity);

        TextView resultText = (TextView) findViewById(R.id.game_result);

        String result = "v";

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result.equals("v")) {
            resultText.setText("Hai vinto!");

            db.updateWin();
        }
        else if (result.equals("s")) {
            resultText.setText("Hai Perso!");

            db.updateLost();
        }
        else if (result.equals("p")) {
            resultText.setText("Pareggio!");

            db.updateDraw();
        }

        db.setBooleanVariable("receivingScore", false);

        db.close();

        Button okButton = (Button) findViewById(R.id.endgame_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event));
    }

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

    public void goToMenu () {
        Intent intent = new Intent(this, MenuActivity.class);

        startActivity(intent);
    }
}
