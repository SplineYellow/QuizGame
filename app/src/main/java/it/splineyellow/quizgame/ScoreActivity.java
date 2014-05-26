package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.SQLException;


public class ScoreActivity extends Activity {

    UtentiDatabaseAdapter db = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);

        Intent intent = getIntent();
        String score = intent.getStringExtra(QuestionActivity.EXTRA_MESSAGE);

        TextView textScore = (TextView) findViewById(R.id.score);
        textScore.setText("Hai totalizzato " + score + " punti!" );

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.updateScore(db.getCurrentUser(), Integer.parseInt(score));
        db.close();
    }
}
