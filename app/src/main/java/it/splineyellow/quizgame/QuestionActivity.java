package it.splineyellow.quizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class QuestionActivity extends Activity {
    public final static String EXTRA_MESSAGE = "it.splineyellow.quizgame.MESSAGE";

    DomandeDatabaseAdapter domandeDatabaseAdapter = new DomandeDatabaseAdapter(this);

    String[][] questionsMatrix = new String[3][6];

    int counter = 0;

    int score = 0;

    boolean answered = false;

    TextView question;

    TextView countdown;

    Button question1;

    Button question2;

    Button question3;

    Button question4;

    String message;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question_activity);

        Intent intent = getIntent();

        String category = intent.getStringExtra(StartGameActivity.EXTRA_MESSAGE);

        message = intent.getStringExtra("Categories");

        countdown = (TextView) findViewById(R.id.countdown);

        question = (TextView) findViewById(R.id.testo_domanda);

        question1 = (Button) findViewById(R.id.risposta1);

        question2 = (Button) findViewById(R.id.risposta2);

        question3 = (Button) findViewById(R.id.risposta3);

        question4 = (Button) findViewById(R.id.risposta4);

        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                countdown.setText("Secondi rimanenti: " + l / 1000);
            }

            @Override
            public void onFinish() {
                Toast t = Toast.makeText(getApplicationContext(), "Fine turno!", Toast.LENGTH_LONG);

                t.show();

                goToScoreActivity();
            }
        }.start();

        try {
            questionsMatrix = getQuestion(category);

            Log.v("questionMatrix", "Tento di riempire la matrice");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (counter == 0) {
            setQuestions(counter);
        }

        question1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                String buttonPosition = "1";

                if (!answered) {
                    answered = true;

                    if (questionsMatrix[counter][5].equals(buttonPosition)) {
                        score++;

                        Log.v("Contatore: ", Integer.toString(counter));

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 1 esatta!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 1 errata!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    }

                    setQuestions(counter);
                }
            }
        });

        question2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                String buttonPosition = "2";

                if (!answered) {
                    answered = true;

                    if (questionsMatrix[counter][5].equals(buttonPosition)) {
                        score++;

                        Log.v("Contatore: ", Integer.toString(counter));

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 2 esatta!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 2 errata!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    }

                    setQuestions(counter);
                }
            }
        });

        question3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                String buttonPosition = "3";

                if (!answered) {
                    answered = true;

                    if (questionsMatrix[counter][5].equals(buttonPosition)) {
                        score++;

                        Log.v("Contatore: ", Integer.toString(counter));

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 3 esatta!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 3 errata!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    }

                    setQuestions(counter);
                }
            }
        });

        question4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chiama funzione checkRisposta
                String buttonPosition = "4";

                if (!answered) {
                    answered = true;

                    if (questionsMatrix[counter][5].equals(buttonPosition)) {
                        score++;

                        Log.v("Contatore: ", Integer.toString(counter));

                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 4 esatta!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), "Risposta 4 errata!", Toast.LENGTH_SHORT);

                        t.show();

                        counter++;
                    }

                    setQuestions(counter);
                }
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

    private boolean checkAnswer() {
        return false;
    }

    private String[][] getQuestion(String category) throws SQLException {
        //Send al db della categoria
        try {
            domandeDatabaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        domandeDatabaseAdapter.fillCategoryTable();

        domandeDatabaseAdapter.fillQuestionsTable();

        String[][] questions = domandeDatabaseAdapter.getQuestions(category);

        Log.v("getQuestion", "Chiamata getQuestions");

        domandeDatabaseAdapter.close();

        return questions;
    }

    public void goToScoreActivity () {
        countDownTimer.cancel();

        Intent intent = new Intent(this, ScoreActivity.class);

        String punti = Integer.toString(score);

        intent.putExtra(EXTRA_MESSAGE, punti);

        intent.putExtra("Categories", message);

        startActivity(intent);
    }

    public void setQuestions (int counter) {
        answered = false;

        Log.v("Contatore dentro setQuestions: ", Integer.toString(counter));

        if (counter == 3) {
            goToScoreActivity();
        }

        if (counter < 3) {
            question.setText(questionsMatrix[counter][0]);

            question1.setText(questionsMatrix[counter][1]);

            question2.setText(questionsMatrix[counter][2]);

            question3.setText(questionsMatrix[counter][3]);

            question4.setText(questionsMatrix[counter][4]);
        }
    }
}