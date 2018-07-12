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
public class FragmentResetPassword extends Fragment implements View.OnClickListener {
    View view;
    Context context = null;
    private TextInputLayout textInputCPassword;
    private TextInputLayout textInputNPassword;
    private TextInputLayout textInputCNPassword;
    private Button btnClear, btnReset;
    private TextView txtBackToLogin;
    AppCompatActivity activity;
    String currentPassword, newPassword, confirmPassword, custId;

    public FragmentResetPassword() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_reset_password, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();
        textInputCPassword = view.findViewById(R.id.current_password);
        textInputNPassword = view.findViewById(R.id.new_password);
        textInputCNPassword = view.findViewById(R.id.confirm_password);
        txtBackToLogin = view.findViewById(R.id.txtBackToLogin);
        btnClear = view.findViewById(R.id.bClear);
        btnReset = view.findViewById(R.id.bReset);
        btnReset.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        txtBackToLogin.setOnClickListener(this);

        /*if (SharedPrefManager.getInstance(context).isLoggedIn()) {
            Toast.makeText(context, "Already Logged", Toast.LENGTH_SHORT).show();
           // activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyAccountFragment()).addToBackStack(null).commit();
            //activity.getSupportActionBar().setTitle("My Account");
        }*/

        return view;
    }

    private void sendData(){
        currentPassword = textInputCPassword.getEditText().getText().toString().trim();
        newPassword = textInputNPassword.getEditText().getText().toString().trim();
        confirmPassword = textInputCNPassword.getEditText().getText().toString().trim();
        custId = "CUST/2018/0001";

        if (TextUtils.isEmpty(currentPassword)) {
            textInputCPassword.getEditText().setError("Please enter your password");
            textInputCPassword.getEditText().requestFocus();
            return;
        }
        if(!isPasswordValid(currentPassword)){
            textInputCPassword.getEditText().setError("Invalid Password Format");
            textInputCPassword.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            textInputNPassword.getEditText().setError("Please enter your new password");
            textInputNPassword.getEditText().requestFocus();
            return;
        }
        if(!isPasswordValid(newPassword)){
            textInputNPassword.getEditText().setError("Invalid Password Format");
            textInputNPassword.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            textInputCNPassword.getEditText().setError("Please confirm your password");
            textInputCNPassword.getEditText().requestFocus();
            return;
        }

        if(!isPasswordValid(confirmPassword)){
            textInputCNPassword.getEditText().setError("Invalid Password Format");
            textInputCNPassword.getEditText().requestFocus();
            return;
        }

        if(!newPassword.equals(confirmPassword)){
            textInputCNPassword.getEditText().setError("Password Mismatch");
            textInputCNPassword.getEditText().requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RESETPASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(context, obj.getString("message") , Toast.LENGTH_SHORT).show();
                                displayPasswordReset();

                            } else {
                                //Toast.makeText(context, obj.getString("message") , Toast.LENGTH_SHORT).show();
                                displayPasswordResetFailure();
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
                params.put("password", currentPassword);
                params.put("npassword", newPassword);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
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

    private void clean(){
        textInputCNPassword.getEditText().setText("");
        textInputNPassword.getEditText().setText("");
        textInputCPassword.getEditText().setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bClear:
                clean();
                break;

            case R.id.bReset:
                sendData();
                break;

            case R.id.txtBackToLogin:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentLogin()).addToBackStack(null).commit();
                activity.getSupportActionBar().setTitle("Login");
                break;

                default:
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    public void displayPasswordReset(){ //account creation notification
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Update.")
                .setContentText("Password Reset Successful")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        clean();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).addToBackStack(null).commit();
                        activity.getSupportActionBar().setTitle("All Mart");
                    }
                })
                .show();
    }

    public void displayPasswordResetFailure(){ //account creation notification
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Update.")
                .setContentText("Password Reset Unsuccessful!. " + "\n" + "Please try again.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        clean();
                    }
                })
                .show();
    }
}
