package es.ujaen.labtelema.festivalsUJA;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Trace;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;


import data.UserData;
import data.Preferences;

//
public class MainActivity extends AppCompatActivity implements FragmentAuth.OnFragmentInteractionListener {

    public static final String PREFS_PORT = "port";
    private static final String DEBUG_TAG = "HTTP";
    public static final String STATUS_DOMAIN = "domain";
    public static final String PREFS_DOMAIN = "domain";
    public static final String PREFS_USER = "user";
    ConnectTask mTask = null;


    private UserData ud=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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
        SharedPreferences sf = getPreferences(MODE_PRIVATE);
        String nombre = sf.getString("USER","");
        String expires = sf.getString("EXPIRES","");
        String sid = sf.getString("SDI","");
        if(nombre!="" && expires!=""){
            //Control de sesión
            Toast.makeText(this,"Bienvenido "+nombre, Toast.LENGTH_LONG).show();


            SimpleDateFormat sdf= new SimpleDateFormat("y-M-d-H-m-s");
            Date epirationDate=sdf.parse(expires,new ParsePosition(0));
            Date horaactual= new Date(System.currentTimeMillis());

            if(epirationDate.getTime()>horaactual.getTime()){
                //Autenticar de forma transparente
                Intent intent=new Intent (this,ServiceActivity.class);
                intent.putExtra(ServiceActivity.PARAMETER_USER,nombre);
                intent.putExtra(ServiceActivity.PARAMETER_EXPIRED, expires);
                intent.putExtra(ServiceActivity.PARAMETER_SID,sid);
                startActivity(intent);
            }else{
                Toast.makeText(this,"La sesión ha caducado", Toast.LENGTH_LONG).show();
            }

       }


    }

    @Override
    //TODO no se si hace falta
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //TODO no se si es necesario
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("domain",ud.getDomain());
    }

    @Override
    public void onFragmentInteraction(UserData udata) {

        Autentica auth=new Autentica();
        auth.execute(udata);

//        this.ud.setDomain(udata.getDomain());
//        changetitle(ud.getDomain());
//        ConnectTask task = new ConnectTask();
//        task.execute(udata);

        //Preferences.saveCredentials(this,udata);


    }

    public class Autentica extends  AsyncTask<UserData,Void,UserData>{

        private static final String RESOURCE="/ssmm/autentica.php";
        private static final String PARAM_USER="user";
        private static final String PARAM_PASS="pass";
        private static final int CODE_HTTP_OK = 200;

        @Override
        protected UserData doInBackground(UserData... userData) {
            UserData data;
            UserData result=null;

            if(userData!=null){
                data=userData[0];

                //A partir de ejemplos tema 3 del github

                String service ="http://"+data.getDomain()+":"+data.getPort()+RESOURCE+"?"
                        +PARAM_USER+"="+data.getUserName()+"&"
                        +PARAM_PASS+"="+data.getPassword();


                try {
                    URL urlService =new URL(service);
                    HttpURLConnection connection= (HttpURLConnection) urlService.openConnection();
                    connection.setReadTimeout(10000 /* milliseconds */);
                    connection.setConnectTimeout(15000 /* milliseconds */);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();

                    int code =connection.getResponseCode();
                    if(code==CODE_HTTP_OK) {
                        InputStreamReader is = new InputStreamReader(connection.getInputStream());
                        BufferedReader br = new BufferedReader(is);
                        String line = "";

                        //El siguiente código trocea la información que recibe de la url
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith("SESSION-ID=")) {
                                String parts[] = line.split("&");

                                if (parts.length == 2) {//Solo debe recibir una respuesta que tenga dos partes
                                    if (parts[1].startsWith("EXPIRES=")) {
                                        result = processSession(data, parts[0], parts[1]);
                                    }
                                }
                            }
                        }
                        br.close();
                        is.close();

                        //}else data=null;
                    }connection.disconnect();//del profesor

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    data=null;
                }catch (IOException ioex){
                    ioex.printStackTrace();
                }

                finally {
                    return result;
                }


            }else
                return null;
        }

        @Override
        protected void onPostExecute(UserData userData) {
            super.onPostExecute(userData);
            if(userData!=null){

                Toast.makeText(getApplicationContext(),"Autenticación correcta",Toast.LENGTH_LONG).show();//TODO poner como cadena de texto (strings.xml)

                //SharedPreferences sp=getPreferences(MODE_PRIVATE); //SOLO GUARDA EL ÚLTIMO USUARIO
                //DE ESTA FORMA PARA CADA USUARIO SE GUARDA UN ARCHIVO:
                SharedPreferences sp=getSharedPreferences(userData.getUserName(),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("USER",userData.getUserName());
                editor.putString("SID",userData.getSid());
                //editor.putString("EXPIRES",userData.getExpires()); //TODO da error [por el formato de la fecha]
                editor.commit();

//               // Para guardar varios ficheros con usuarios
//                SharedPreferences def= getPreferences(MODE_PRIVATE);
//                SharedPreferences.Editor edit2=def.edit();
//                edit2.putString("LAST_USER",userData.getUserName());
//                edit2.commit();


                //Intent intent =new Intent(getApplicationContext(),ServiceActivity.class);
                Intent intent=new Intent (MainActivity.this,ServiceActivity.class);
                intent.putExtra(ServiceActivity.PARAMETER_USER,userData.getUserName());
                intent.putExtra(ServiceActivity.PARAMETER_SID,userData.getSid());
                intent.putExtra(ServiceActivity.PARAMETER_EXPIRED,userData.getExpires());
                startActivity(intent);
            }else{
                SharedPreferences sp = getSharedPreferences(userData.getUserName(),MODE_PRIVATE);
                SharedPreferences.Editor editor= sp.edit();
                editor.putString("USER",userData.getUserName());
                editor.putString("SID","");
                editor.putString("EXPIRES","");
                editor.commit();
                Toast.makeText(getApplicationContext(),"Autenticación error",Toast.LENGTH_LONG).show();
            }


        }

        /**
         *
         * @param input the data of the current user
         * @param session string withc format SESSION-ID=****
         * @param expires string witch format EXPIRES=*****
         * @return updated user data
         */


        protected UserData processSession (UserData input, String session, String expires){

            session =session.substring(session.indexOf("=")+1,session.length());
            expires =expires.substring(expires.indexOf("=")+1,expires.length());
            input.setSid(session);
            //TODO convertir fecha a formato DATE
            //Date exp= DateFormat.getDateInstance().format(expires);
            //input.setExpires(exp);


            return input;
        }


    }




/*
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
*/
    //TODO download url
    private String downloadURL (String domain, String user, String pass) throws IOException {
        InputStream is = null;
        String result = "";

        HttpURLConnection conect = null;

        try {
            String contentAsString = "";
            String tempString = "";
            String url = "http://" + domain + "/ssmm/autentica.php" + "?user=" + user + "&pass=" + pass;
            URL service_url = new URL(url);
            System.out.println("Abriendo conexión: " + service_url.getHost()
                    + " puerto=" + service_url.getPort());
            conect =(HttpURLConnection) service_url.openConnection();
            //añadido para pruebas
            conect.setReadTimeout(10000 /* miliseconds */);
            conect.setConnectTimeout(15000 /*miliseconds*/);
            conect.setRequestMethod("GET");
            conect.setDoInput(true);
                    //hasta aqui
            conect.connect();
            final int response = conect.getResponseCode();
            final int contentLength = conect.getHeaderFieldInt("Content-length", 1000);
            String mimeType = conect.getHeaderField("Content-Type");
            String encoding = mimeType.substring(mimeType.indexOf(";"));
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((tempString = br.readLine()) != null) {
                contentAsString = contentAsString + tempString;
                //task.onProgressUpdate(contentAsString.length());
            }

            return contentAsString;

        } catch (MalformedURLException mex) {
            result = "URL mal formateada: " + mex.getMessage();
            System.out.println(result);
        } catch (IOException e) {
            result = "Excepción: " + e.getMessage();
            System.out.println(result);
        }finally{
            if(is != null){
                is.close();
                conect.disconnect();
            }
        }


        return result;
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
                String url = "http://" + userData[0].getDomain();
                return downloadURL(url, userData[0].getUserName(), userData[0].getPassword());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),getString(R.string.main_progress)+" "+String.valueOf(values[0]),Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            TextView banner= findViewById(R.id.main_degree);
            banner.setText(R.string.main_connected);

        }
    }
}
