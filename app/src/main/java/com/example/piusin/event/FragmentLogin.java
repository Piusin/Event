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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener{
    AppCompatActivity activity;
    View view;
    Context context = null;
    //EditText editTextEmail, editTextPassword;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    TextView createAccount;
    Button bLogin, bFPassword;


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
                Toast.makeText(context, "Forgot Password", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(activity, AccountActivity.class));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyAccountFragment()).addToBackStack(null).commit();
                activity.getSupportActionBar().setTitle("Account");
                break;

                default:
                    Toast.makeText(context, "End of Options", Toast.LENGTH_SHORT).show();
                    break;
        }

    }

    private void userLogin() {
        //first getting the values
        final String email = textInputEmail.getEditText().getText().toString().trim();
        final String password = textInputPassword.getEditText().getText().toString().trim();

        //validating inputs
        if (TextUtils.isEmpty(email)) {
            textInputEmail.setError("Please enter your email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            textInputPassword.setError("Please enter your password");
            textInputPassword.getEditText().requestFocus();
            return;
        }

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                               // clean();
                                //editTextEmail.requestFocus(); will come

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getString("name"),
                                        userJson.getString("email"),
                                        userJson.getString("county"),
                                        userJson.getString("url")
                                );

                                SharedPrefManager.getInstance(getContext().getApplicationContext()).userLogin(user);
                                clean();
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyAccountFragment()).addToBackStack(null).commit();
                                activity.getSupportActionBar().setTitle("My Account");

                            } else {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                               // clean();
                               //editTextEmail.requestFocus(); will come
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
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void clean(){
        //editTextEmail.setText("");
        //editTextPassword.setText("");
    }

}
