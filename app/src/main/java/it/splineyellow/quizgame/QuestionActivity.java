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
    String[] questionsArray = {};
    int contatore = 0;
    int punteggio = 0;
    boolean answered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);

        Intent intent = getIntent();
        String category = intent.getStringExtra(StartGameActivity.EXTRA_MESSAGE);

        TextView question = (TextView) findViewById(R.id.testo_domanda);
        Button risposta1 = (Button) findViewById(R.id.risposta1);
        Button risposta2 = (Button) findViewById(R.id.risposta2);
        Button risposta3 = (Button) findViewById(R.id.risposta3);
        Button risposta4 = (Button) findViewById(R.id.risposta4);

        //DA IMPLEMENTARE PER 3 DOMANDE, FARE FUNZIONE

        long start = System.currentTimeMillis();
        long end = start + 60*1000; // 60 seconds * 1000 ms/sec

            try {
                questionsArray = getQuestion(category);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            question.setText(questionsArray[0]);
            risposta1.setText(questionsArray[1]);
            risposta2.setText(questionsArray[2]);
            risposta3.setText(questionsArray[3]);
            risposta4.setText(questionsArray[4]);

//        goToStartGameActivity();

        risposta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 1", Toast.LENGTH_SHORT);
                t.show();
                String posizioneBottone = "1";

                if (!answered) {
                    answered = true;
                    contatore++;
                    if (questionsArray[5].equals(posizioneBottone)) punteggio++;
                }

            }
        });

        risposta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 2", Toast.LENGTH_SHORT);
                t.show();

                String posizioneBottone = "2";

                if (!answered) {
                    answered = true;
                    contatore++;
                    if (questionsArray[5].equals(posizioneBottone)) punteggio++;
                }

            }
        });

        risposta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 3", Toast.LENGTH_SHORT);
                t.show();

                String posizioneBottone = "3";

                if (!answered) {
                    answered = true;
                    contatore++;
                    if (questionsArray[5].equals(posizioneBottone)) punteggio++;
                }

            }
        });

        risposta4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                Toast t = Toast.makeText(getApplicationContext(), "Risposta 4", Toast.LENGTH_SHORT);
                t.show();

                String posizioneBottone = "4";

                if (!answered) {
                    answered = true;
                    contatore++;

                    if (questionsArray[5].equals(posizioneBottone)) punteggio++;
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

    public void goToStartGameActivity () {

        Intent intent = new Intent(this, StartGameActivity.class);
        startActivity(intent);
    }

}
