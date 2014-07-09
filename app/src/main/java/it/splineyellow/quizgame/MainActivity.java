package it.splineyellow.quizgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Copyright SplineYellow - 2014

/*
    Classe per la gestione iniziale del programma.
 */
public class MainActivity extends Activity {
    EditText editTextUser;

    EditText editTextPassword;

    Button buttonLogin;

    UtentiDatabaseAdapter utentiDatabaseAdapter = new UtentiDatabaseAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        editTextUser = (EditText) findViewById(R.id.user);

        editTextPassword = (EditText) findViewById(R.id.password);

        buttonLogin = (Button) findViewById(R.id.login);

        buttonLogin.setOnClickListener(buttonLoginOnClickListener);

        try {
            utentiDatabaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        utentiDatabaseAdapter.insertDefaultBooleanVariables();

        utentiDatabaseAdapter.setBooleanVariable("receivingScore", false);

        utentiDatabaseAdapter.setIntegerVariable("gameCounter", 0);

        utentiDatabaseAdapter.close();
    }

    /*
        ClickListener per l'inserimento di utente e password nel database; in caso fosse già
        presente si controlla l'esattezza dei dati inseriti.
     */
    View.OnClickListener buttonLoginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String nick = getUser();

                String passwd = getPassword();

                SimpleDateFormat ts = new SimpleDateFormat("hhmmssddMMyyyy");

                String timestamp = ts.format(new Date());

                boolean correctUserPass = false;

                if (!nick.equals("") && !passwd.equals("")) {
                    try {
                        utentiDatabaseAdapter.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (utentiDatabaseAdapter.alreadyIn(nick)) {
                        if (!passwd.equals(utentiDatabaseAdapter.getPasswordByUser(nick))) {
                            Toast t = Toast.makeText(getApplicationContext(), "Username o Password errati", Toast.LENGTH_LONG);

                            t.show();
                        }
                        else {
                            correctUserPass = true;

                            utentiDatabaseAdapter.updateLastAccess(timestamp, nick);
                        }
                    }
                    else {
                        utentiDatabaseAdapter.insertUser(nick, passwd, timestamp);

                        correctUserPass = true;
                    }

                    utentiDatabaseAdapter.close();

                    if (correctUserPass) {
                        goToMenu();
                    }
                } else {
                    Toast t = Toast.makeText(getApplicationContext(), "Completare tutti i campi", Toast.LENGTH_LONG);

                    t.show();
                }
            }
        };

    public String getUser() {
        return editTextUser.getText().toString();
    }

    public String getPassword() {
        return editTextPassword.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /*
        OptionsItemSelected per la rimozione del database Utenti tramite popup.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_db:
                AlertDialog.Builder dropBuilder = new AlertDialog.Builder(this);

                dropBuilder.setMessage("Sei sicuro di voler eliminare il database utenti?")

                        .setCancelable(false)

                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Context context = getApplicationContext();

                                try {
                                    context.deleteDatabase("utenti.db");
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                                if (context!=null) {
                                    Toast t = Toast.makeText(context, "Dati eliminati!", Toast.LENGTH_LONG);

                                    t.show();
                                }
                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog dropAlert = dropBuilder.create();

                dropAlert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        editTextPassword.setText("");
    }

    /*
        goToMenu() permette di passare all'activity specificata.
     */
    public void goToMenu () {
        Intent intent = new Intent(this, MenuActivity.class);

        startActivity(intent);
    }
}