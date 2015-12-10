package in.appsplanet.wedsource.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * @author pravin
 */
public class AppSettings {

    private String TAG = "AppSettings";
    private String mSharedPrefName = "WeddSource";
    private String mUserId = "userId";

    private String REMEMBER_USERNAME = "Username";
    private String REMEMBER_PASSWORD = "Password";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson mGson;

    public AppSettings(Context context) {
        preferences = context.getSharedPreferences(mSharedPrefName,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        mGson = new Gson();
    }

    /**
     * @param login
     */
    public void setUserId(String id) {
        editor.putString(mUserId, id).commit();
    }

    public String getUserId() {
        return preferences.getString(mUserId, null);
    }


    public void setRememberUserName(String name) {
        editor.putString(REMEMBER_USERNAME, name).commit();
    }

    public String getRememberUserName() {
        return preferences.getString(REMEMBER_USERNAME, null);
    }


    public void setRememberPassword(String name) {
        editor.putString(REMEMBER_PASSWORD, name).commit();
    }

    public String getRememberPassword() {
        return preferences.getString(REMEMBER_PASSWORD, null);
    }

}