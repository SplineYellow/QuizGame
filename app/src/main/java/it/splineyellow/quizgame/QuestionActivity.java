package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class QuestionActivity extends Activity {

    DomandeDatabaseAdapter db = new DomandeDatabaseAdapter(this);
    int contatore = 0;
    boolean answered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);

        Intent intent = getIntent();
        String category = intent.getStringExtra(StartGameActivity.EXTRA_MESSAGE);

        String[] questionsArray = {};
        try {
            questionsArray = getQuestion(category);
            String k = Integer.toString(questionsArray.length);
            Log.v("QUESTIONS ARRAY: ", k);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextView question = (TextView) findViewById(R.id.testo_domanda);

        question.setText(questionsArray[0]);

        Button risposta1 = (Button) findViewById(R.id.risposta1);
        Button risposta2 = (Button) findViewById(R.id.risposta2);
        Button risposta3 = (Button) findViewById(R.id.risposta3);
        Button risposta4 = (Button) findViewById(R.id.risposta4);

        risposta1.setText(questionsArray[1]);
        risposta2.setText(questionsArray[2]);
        risposta3.setText(questionsArray[3]);
        risposta4.setText(questionsArray[4]);

        risposta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 1", Toast.LENGTH_SHORT);
                t.show();

                if (!answered) {
                    answered = true;
                    contatore++;
                }

            }
        });

        risposta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 2", Toast.LENGTH_SHORT);
                t.show();

                if (!answered) {
                    answered = true;
                    contatore++;
                }

            }
        });

        risposta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 3", Toast.LENGTH_SHORT);
                t.show();

                if (!answered) {
                    answered = true;
                    contatore++;
                }

            }
        });

        risposta4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 4", Toast.LENGTH_SHORT);
                t.show();

                if (!answered) {
                    answered = true;
                    contatore++;
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
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
    }

    private boolean checkAnswer() {

        return false;
    }

    private String[] getQuestion(String category) throws SQLException {

        //Send al db della categoria

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.fillCategoryTable();
        db.fillQuestionsTable();

        String[] questions = {};
        questions = db.getQuestion(category.toUpperCase());

        db.close();

        return questions;
    }

}
