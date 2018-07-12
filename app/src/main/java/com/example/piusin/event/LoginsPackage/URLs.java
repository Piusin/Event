package com.example.piusin.event.LoginsPackage;

/**
 * Created by Piusin on 4/10/2018.
 */

public class URLs { //fragmentAccountDetails
    private static final String ROOT_URL = "http://10.42.0.1/Scripts/SuperMart/registerApi.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN= ROOT_URL + "login";

    //for image uploads
    public static final String UPLOAD_URL = "http://10.42.0.1/Scripts/SuperMart/testUpload.php";
    public static final String UPDATE_URL = "http://10.42.0.1/Scripts/SuperMart/updateAccounts.php";
    public static final String UPDATES_URL = "http://10.42.0.1/Scripts/SuperMart/updateAccount.php";
    public static final String RESETPASS = "http://10.42.0.1/Scripts/SuperMart/resetpassword.php";
    public static final String INVOKEPASS = "http://10.42.0.1/Scripts/SuperMart/forgotPassword.php";
}
