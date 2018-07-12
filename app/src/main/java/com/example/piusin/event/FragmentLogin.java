package com.example.piusin.event;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.piusin.event.LoginsPackage.SharedPrefManager;
import com.example.piusin.event.LoginsPackage.URLs;
import com.example.piusin.event.LoginsPackage.User;
import com.example.piusin.event.LoginsPackage.VolleySingleton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener{
    AppCompatActivity activity;
    View view;
    Context context = null;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputCustId;
    private TextView createAccount;
    private Button bLogin, bFPassword;
    String custId, email, password;


    public FragmentLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_login, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();

        textInputEmail = view.findViewById(R.id.text_input_email);
        textInputPassword = view.findViewById(R.id.text_input_password);
        textInputCustId = view.findViewById(R.id.text_input_custid);
        createAccount = view.findViewById(R.id.tvCreateAccount);
        bLogin = view.findViewById(R.id.bLogin);
        bFPassword = view.findViewById(R.id.bForgotPass);
        createAccount.setOnClickListener(this);
        bLogin.setOnClickListener(this);
        bFPassword.setOnClickListener(this);

        if (SharedPrefManager.getInstance(context).isLoggedIn()) {
          //  Toast.makeText(context, "Already Logged", Toast.LENGTH_SHORT).show();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyAccountFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("My Account");
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogin:
                userLogin();
                break;

            case R.id.tvCreateAccount:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AccountFragment()).addToBackStack(null).commit();
                activity.getSupportActionBar().setTitle("Account");
                break;

            case R.id.bForgotPass:
                invokePHPMirror();
               // activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentResetPassword()).addToBackStack(null).commit();
               // activity.getSupportActionBar().setTitle("Reset Password");
                break;

                default:
                    Toast.makeText(context, "End of Options", Toast.LENGTH_SHORT).show();
                    break;
        }

    }

    private void userLogin() {
         email = textInputEmail.getEditText().getText().toString().trim();
         password = textInputPassword.getEditText().getText().toString().trim();
         custId = textInputCustId.getEditText().getText().toString().trim();
        
        //validating inputs
        if (TextUtils.isEmpty(custId)) {
            textInputCustId.getEditText().setError("Please enter your Customer ID");
            textInputCustId.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            textInputEmail.getEditText().setError("Please enter your email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if(!isEmailValid(email)){
            textInputEmail.getEditText().setError("Enter a valid email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            textInputPassword.getEditText().setError("Please enter your password");
            textInputPassword.getEditText().requestFocus();
            return;
        }

        if(!isPasswordValid(password)){
            textInputPassword.getEditText().setError("Invalid Password Format");
            textInputPassword.getEditText().requestFocus();
            return;
        }

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userJson = obj.getJSONObject("user");
                                User user = new User(
                                        userJson.getString("cust_id"),
                                        userJson.getString("email"),
                                        userJson.getString("phone"),
                                        userJson.getString("county"),
                                        userJson.getString("url")
                                );
                                SharedPrefManager.getInstance(getContext().getApplicationContext()).userLogin(user);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyAccountFragment()).addToBackStack(null).commit();
                                activity.getSupportActionBar().setTitle("My Account");

                            } else {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cust_id", custId);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public boolean isEmailValid(String email)
      {
         String regExpn =
             "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                 +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                   +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                   +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                   +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                   +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

     CharSequence inputStr = email;

     Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
     Matcher matcher = pattern.matcher(inputStr);

     if(matcher.matches())
        return true;
     else
        return false;

     }

    public boolean isPasswordValid(String password){
        String regExpn = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        CharSequence inputStr = password;
        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
            return true;
        else
            return false;

    }

    private void invokePHPMirror(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.INVOKEPASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("message") , Toast.LENGTH_SHORT).show();
                               // displayPasswordReset();
                                displayPasswordSent();

                            } else {
                                Toast.makeText(context, obj.getString("message") , Toast.LENGTH_SHORT).show();
                                //displayPasswordResetFailure();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error Ocurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cust_id", "CUST/2018/0001");
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void displayPasswordSent(){ //account creation notification
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Update.")
                .setContentText("Password Reset Key has been sent, " + "\n" + "to your email address."
                + "\n" + "Use it as the current password")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                    }
                })
                .show();
    }
}
