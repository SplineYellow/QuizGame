package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class QuestionActivity extends Activity {

    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    DomandeDatabaseAdapter db = new DomandeDatabaseAdapter(this);
    String[][] questionsMatrix = new String[3][6];
    int contatore = 0;
    int punteggio = 0;
    boolean answered = false;
    TextView question;
    Button risposta1;
    Button risposta2;
    Button risposta3;
    Button risposta4;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);

        Intent intent = getIntent();
        String category = intent.getStringExtra(StartGameActivity.EXTRA_MESSAGE);
        message = intent.getStringExtra("Categories");

        question = (TextView) findViewById(R.id.testo_domanda);
        risposta1 = (Button) findViewById(R.id.risposta1);
        risposta2 = (Button) findViewById(R.id.risposta2);
        risposta3 = (Button) findViewById(R.id.risposta3);
        risposta4 = (Button) findViewById(R.id.risposta4);

        try {

           questionsMatrix = getQuestion(category);
           Log.v("questionMatrix", "Tento di riempire la matrice");

        } catch (SQLException e) {

           e.printStackTrace();

        }

        if (contatore == 0) {

            setQuestions(contatore);

        }

        risposta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta

                String posizioneBottone = "1";

                if (!answered) {
                    answered = true;
                    SystemClock.sleep(1000);
                    if (questionsMatrix[contatore][5].equals(posizioneBottone)) {
                        punteggio++;
                        Log.v("Contatore: ", Integer.toString(contatore));
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 1 esatta!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;
                    }

                    else {

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 1 errata!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;

                    }

                    setQuestions(contatore);
                }

            }
        });

        risposta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta


                String posizioneBottone = "2";

                if (!answered) {
                    answered = true;
                    SystemClock.sleep(1000);
                    if (questionsMatrix[contatore][5].equals(posizioneBottone)) {
                        punteggio++;
                        Log.v("Contatore: ", Integer.toString(contatore));
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 2 esatta!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;
                    }

                    else {

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 2 errata!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;

                    }
                    setQuestions(contatore);
                }

            }
        });

        risposta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta


                String posizioneBottone = "3";

                if (!answered) {
                    answered = true;
                    SystemClock.sleep(1000);
                    if (questionsMatrix[contatore][5].equals(posizioneBottone)) {
                        punteggio++;
                        Log.v("Contatore: ", Integer.toString(contatore));
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 3 esatta!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;
                    }

                    else {

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 3 errata!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;

                    }
                    setQuestions(contatore);
                }

            }
        });

        risposta4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta


                String posizioneBottone = "4";

                if (!answered) {
                    answered = true;
                    SystemClock.sleep(1000);
                    if (questionsMatrix[contatore][5].equals(posizioneBottone)) {
                        punteggio++;
                        Log.v("Contatore: ", Integer.toString(contatore));
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 4 esatta!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;
                    }

                    else {

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 4 errata!", Toast.LENGTH_SHORT);
                        t.show();
                        contatore++;

                    }
                    setQuestions(contatore);
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

    private String[][] getQuestion(String category) throws SQLException {

        //Send al db della categoria

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.fillCategoryTable();
        db.fillQuestionsTable();

        String[][] questions = db.getQuestions(category);
        Log.v("getQuestion", "Chiamata getQuestions");

        db.close();

        return questions;
    }

    public void goToScoreActivity () {

        Intent intent = new Intent(this, ScoreActivity.class);

        String punti = Integer.toString(punteggio);
        intent.putExtra(EXTRA_MESSAGE, punti);
        intent.putExtra("Categories", message);
        startActivity(intent);
    }

    public void setQuestions (int contatore) {

        answered = false;

        Log.v("Contatore dentro setQuestions: ", Integer.toString(contatore));

        if (contatore == 3) {
            goToScoreActivity();
        }

        if (contatore < 3) {
            question.setText(questionsMatrix[contatore][0] + "\n Risposta esatta: " + questionsMatrix[contatore][5]);
            risposta1.setText(questionsMatrix[contatore][1]);
            risposta2.setText(questionsMatrix[contatore][2]);
            risposta3.setText(questionsMatrix[contatore][3]);
            risposta4.setText(questionsMatrix[contatore][4]);
        }
    }

}
