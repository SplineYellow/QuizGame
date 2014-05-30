package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    public void goToMenu () {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
