package com.example.piusin.event.LoginsPackage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Piusin on 4/10/2018.
 */

public class SharedPrefManager {
   //the constants
    private static final String SHARED_PREF_NAME = "superman";
    private static final String KEY_CUSTNAME = "custname";
    private static final String KEY_CUSTEMAIL = "custemail";
    private static final String KEY_URL = "custurl";
    private static final String KEY_COUNTY = "custcounty";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CUSTNAME, user.getCust_name());
        editor.putString(KEY_CUSTEMAIL, user.getCust_email());
        editor.putString(KEY_COUNTY, user.getCust_county());
        editor.putString(KEY_URL, user.getUrl());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CUSTNAME, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_CUSTNAME, null),
                sharedPreferences.getString(KEY_CUSTEMAIL, null),
                sharedPreferences.getString(KEY_COUNTY, null),
                sharedPreferences.getString(KEY_URL, null)

        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
       //mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
