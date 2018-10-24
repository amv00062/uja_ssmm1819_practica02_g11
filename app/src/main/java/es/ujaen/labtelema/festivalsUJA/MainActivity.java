package es.ujaen.labtelema.festivalsUJA;

import android.os.Trace;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;

import data.UserData;

//
public class MainActivity extends AppCompatActivity implements FragmentAuth.OnFragmentInteractionListener {

    private UserData ud=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO Añadir algo aquí
        Log.d("ARRANCANDO","La aplicación móvil se está iniciando");
        FragmentManager fm = getSupportFragmentManager();
        Fragment temp = fm.findFragmentById(R.id.main_container);
        if(temp==null) {
            FragmentTransaction ft = fm.beginTransaction();
            FragmentAuth fragment = FragmentAuth.newInstance("", "");
            ft.add(R.id.main_container, fragment, "login");
            ft.commit();
        } else
            Toast.makeText(this,getString(R.string.mainactivity_fragmentepresent), Toast.LENGTH_SHORT).show();

       if(savedInstanceState!=null){
           String domain = savedInstanceState.getString("domain");
           ud = new UserData();
           ud.setDomain(domain);

       }
       else {
           ud = new UserData();

       }

        changetitle(ud.getDomain());
    }

    public void changetitle(String title){
        TextView tuser = findViewById(R.id.main_apptitle);
        tuser.setText(title);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("domain",ud.getDomain());
    }

    @Override
    public void onFragmentInteraction(UserData udata) {
        this.ud.setDomain(udata.getDomain());
        changetitle(ud.getDomain());
        ConnectTask task = new ConnectTask();
        task.execute(udata);
    }
    public String readServer(UserData udata){
        try {
            //URL url = new URL(domain);
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            Socket socket = new Socket(udata.getDomain(),udata.getPort());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("GET /~jccuevas/ssmm/login.php?user=user1&pass=12341234 HTTP/1.1\r\nhost:www4.ujaen.es\r\n\r\n");
            dataOutputStream.flush();
            StringBuilder sb = new StringBuilder();
            BufferedReader bis;
            bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = "";
            while((line = bis.readLine())!=null) {
                sb.append(line);
            }
            final String datos= sb.toString();
            return datos;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    class ConnectTask extends AsyncTask<UserData,Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView banner = findViewById(R.id.main_degree);
            banner.setText(R.string.main_connecting);
        }

        @Override
        protected String doInBackground(UserData... userData) {
            try {
                //URL url = new URL(domain);
                //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                Socket socket = new Socket(userData[0].getDomain(),userData[0].getPort());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("GET /~jccuevas/ssmm/login.php?user=user1&pass=12345 HTTP/1.1\r\nhost:www4.ujaen.es\r\n\r\n");
                dataOutputStream.flush();
                StringBuilder sb = new StringBuilder();
                BufferedReader bis;
                bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = "";
                while((line = bis.readLine())!=null) {
                    sb.append(line);
                    publishProgress(line.length());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final String datos= sb.toString();
                return datos;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
/*
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),getString(R.string.main_progress)+" "+String.valueOf(values[0]),Toast.LENGTH_LONG).show();
        }
*/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            TextView banner= findViewById(R.id.main_degree);
            banner.setText(R.string.main_connected);

        }
    }
}
