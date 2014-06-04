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


public class EndGameActivity extends Activity {

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game_activity);

        TextView resultText = (TextView) findViewById(R.id.game_result);

        String result = "v"; // valore di prova; si fa passare dalla StartGameActivity il risultato

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

        db.close();


        Button okButton = (Button) findViewById(R.id.endgame_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
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
        //getMenuInflater().inflate(R.menu.menu, menu);

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

    public void goToMenu () {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
