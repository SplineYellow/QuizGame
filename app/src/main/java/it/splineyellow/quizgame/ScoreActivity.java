package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class ScoreActivity extends Activity {

    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);
    String message;
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);

        Intent intent = getIntent();
        String score = intent.getStringExtra(QuestionActivity.EXTRA_MESSAGE);
        message = intent.getStringExtra("Categories");

        TextView textScore = (TextView) findViewById(R.id.score);
        textScore.setText("Hai totalizzato " + score + " punti!" );

        okButton = (Button) findViewById(R.id.buttonOK);

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.updateScore(db.getCurrentUser(), Integer.parseInt(score));
        db.close();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStartGameActivity();
            }
        });

        //SEND AL SERVER
    }

    public void goToStartGameActivity () {

        Intent intent = new Intent(this, StartGameActivity.class);

        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }
}
