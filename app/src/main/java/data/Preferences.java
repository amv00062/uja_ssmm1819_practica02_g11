package data;

import android.content.Context;
import android.content.SharedPreferences;
import es.ujaen.labtelema.festivalsUJA.MainActivity;

public class Preferences {

    public static void saveCredentials(Context context, UserData userData){
        SharedPreferences sp = context.getSharedPreferences("default",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sp.edit();
        editor.putString(MainActivity.PREFS_DOMAIN,userData.getDomain());
        editor.putString(MainActivity.PREFS_USER,userData.getUserName());
        editor.putInt(MainActivity.PREFS_PORT,userData.getPort());
        editor.commit();
    }
}
