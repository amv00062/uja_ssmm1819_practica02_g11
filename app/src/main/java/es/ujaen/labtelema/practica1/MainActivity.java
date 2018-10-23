package es.ujaen.labtelema.practica1;

import android.os.TokenWatcher;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    }
}
