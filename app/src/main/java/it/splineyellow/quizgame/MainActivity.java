package it.splineyellow.quizgame;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends Activity {

    EditText editTextUser, editTextPassword;
    Button buttonLogin;

    //parametri per connessione al server

    String dstAddress = "thebertozz.no-ip.org";
    int dstPort = 9533;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUser = (EditText)findViewById(R.id.user);
        editTextPassword = (EditText)findViewById(R.id.password);
        buttonLogin = (Button)findViewById(R.id.login);

        buttonLogin.setOnClickListener(buttonLoginOnClickListener);

    }

    View.OnClickListener buttonLoginOnClickListener =
            new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask();

                    if (!getUser().equals("") || !getPassword().equals("")) {
                        myClientTask.execute();
                    }
                    else {
                        Toast t = Toast.makeText(getApplicationContext(), "Completare tutti i campi", Toast.LENGTH_LONG);
                        t.show();
                    }
                }};

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            DatagramSocket ds = null;

            try
            {
                InetAddress serverAddr = InetAddress.getByName(dstAddress);

                String data = getUser() + "," + getPassword();
                byte[] buffer = data.getBytes();

                ds = new DatagramSocket();
                DatagramPacket dp;
                dp = new DatagramPacket(buffer, buffer.length, serverAddr, dstPort);

                ds.send(dp);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (ds != null)
                {
                    ds.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }

    public String getUser(){

        return editTextUser.getText().toString();
    }

    public String getPassword(){

        return editTextPassword.getText().toString();
    }
}
