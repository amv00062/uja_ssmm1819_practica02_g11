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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

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
/*
        new Trace(new Runnable()){
            @Override
            public void run(){
                try{
                    URL url = new URL(udata.getDomain());
                    HttpURLConnection connection =(HttpURLConnection) url.openConnection();

                    Socket socket =new Socket("www4.ujaen.es", 80);
                    DataOutputStream dataOutputStream = DataOutputStream (connection.getOutputStream());
                    dataOutputStream.writeUTF("Get /~jccuevas/ssmm/autentica.php?user=user1&pass=12345/ HTTP/1.1\r\nhost:www4.ujaen.es\r\n");
                    dataOutputStream.flush();

                    BufferedReader bit;
                    bit=new BufferedReader(new InputStreamReader(connection.getInputStream()));


                    String datos= bit.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),datos,Toast,)
                        }
                    });


                }catch (MalformedURLException e){
                    e.printStackTrace();
                }
            }
        }
*/


        this.ud.setDomain(udata.getDomain());
        changetitle(ud.getDomain());
    }
}
