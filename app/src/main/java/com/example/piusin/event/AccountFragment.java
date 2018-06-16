package com.example.piusin.event;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.piusin.event.LoginsPackage.SharedPrefManager;
import com.example.piusin.event.LoginsPackage.URLs;
import com.example.piusin.event.LoginsPackage.User;
import com.example.piusin.event.LoginsPackage.VolleySingleton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements Spinner.OnItemSelectedListener,View.OnClickListener {
    Spinner spinner;
    Context context = null;
    private ArrayList<String> counties;
    private JSONArray county;
   // EditText accountName, accountEmail, accountPassword;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    TextView startLogin;
    Button createAccount;
    ImageView custImage;
    String userCounty, imageEmail, name, password, path;
    AppCompatActivity activity;
    View view;

    //for image upload
    private int PICK_IMAGE_REQUEST = 1; //Image request code
    private static final int STORAGE_PERMISSION_CODE = 123; //storage permission code
    private Bitmap bitmap; ////Bitmap to get image froMm gallery
    private Uri filePath;   //Uri to store the image uri


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        requestStoragePermission();
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();
        counties = new ArrayList();
        spinner = view.findViewById(R.id.account_spinner);
        spinner.setOnItemSelectedListener(this);
        getData();

        textInputUsername = view.findViewById(R.id.text_input_username);
        textInputEmail = view.findViewById(R.id.text_input_email);
        textInputPassword = view.findViewById(R.id.text_input_password);
        startLogin = view.findViewById(R.id.txtStartLogin);
        custImage = view.findViewById(R.id.user_image);
        createAccount = view.findViewById(R.id.bCreateAccount);
        createAccount.setOnClickListener(this);
        custImage.setOnClickListener(this);
        startLogin.setOnClickListener(this);

        //if the user is already logged in directly start the accountDetails fragment
        if (SharedPrefManager.getInstance(getContext().getApplicationContext()).isLoggedIn()) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentAccountDetails()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("My Account");
        }

        return view;
    }

    private void getData() {
        StringRequest stringRequest = new StringRequest(Config.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j;
                        try {
                            j = new JSONObject(response);
                            county = j.getJSONArray(Config.JSON_ARRAY);
                            getCounties(county);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getCounties(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                //Adding the name of the county to array list
                counties.add(json.getString(Config.TAG_COUNTY));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<>(getContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, counties));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userCounty = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        userCounty = "";
    }

    //for customer registration
    private void registerUser() {
        final String custName = textInputUsername.getEditText().getText().toString().trim();
        final String custEmail = textInputEmail.getEditText().getText().toString().trim();
        final String custPassword = textInputPassword.getEditText().getText().toString().trim();


        //first we will do the validations
        if (TextUtils.isEmpty(custName)) {
            textInputUsername.getEditText().setError("Please enter username");
            textInputUsername.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(custEmail)) {
            textInputEmail.getEditText().setError("Please enter your email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(custEmail).matches()) {
            textInputEmail.getEditText().setError("Enter a valid email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(custPassword)) {
            textInputPassword.getEditText().setError("Enter a password");
            textInputPassword.getEditText().requestFocus();
            return;
        }
        if (userCounty.isEmpty()) {
            Toast.makeText(context, "Select Your County. Please!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getContext().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getString("cust_name"),
                                        userJson.getString("cust_email"),
                                        userJson.getString("password"),
                                        userJson.getString("county")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getContext().getApplicationContext()).userLogin(user);

                                //starting the profile activity
                                //finish();
                                //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", custName);
                params.put("email", custEmail);
                params.put("password", custPassword);
                params.put("county", userCounty);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
        clean();
    }

    private void clean() {
        textInputUsername.getEditText().setText("");
        textInputEmail.getEditText().setText("");
        textInputPassword.getEditText().setText("");
        textInputUsername.getEditText().requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bCreateAccount:
                uploadMultipart();
                break;

            case R.id.user_image:
                showFileChooser();
                break;

            case R.id.txtStartLogin:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentLogin()).addToBackStack(null).commit();
                activity.getSupportActionBar().setTitle("Login");
                break;

                default:
                    Toast.makeText(context, "End of Options", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    /*
    * This is the method responsible for image upload
    * We need the full image path and the name for the image in this method
    * */
    public void uploadMultipart() {
        //getting the actual path of the image
        path = getPath(filePath);
        validations(); // for file validations
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getActivity().getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        catch (Exception e){
            displayImageError();
        }
        return  path;
    }

    private void validations(){ //does fields uploads
        imageEmail = textInputEmail.getEditText().getText().toString().trim();
        name = textInputUsername.getEditText().getText().toString().trim();
        password = textInputPassword.getEditText().getText().toString().trim();
        //getting the actual path of the image
        // path = getPath(filePath);
        if (TextUtils.isEmpty(name)) {
            textInputUsername.getEditText().setError("Please enter username");
            textInputUsername.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(imageEmail)) {
            textInputEmail.getEditText().setError("Please enter your email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(imageEmail).matches()) {
            textInputEmail.getEditText().setError("Enter a valid email");
            textInputEmail.getEditText().requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            textInputPassword.getEditText().setError("Enter a password");
            textInputPassword.getEditText().requestFocus();
            return;
        }
        if(userCounty.isEmpty())
        {
            Toast.makeText(context, "Select County", Toast.LENGTH_SHORT).show();
            return;
        }
        if(path == null){
            Toast.makeText(context, "No Image Path.Select Image", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            uploadCode();
        }
    }

    private void uploadCode(){
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(context, uploadId, URLs.UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .addParameter("email", imageEmail)
                    .addParameter("password", password)
                    .addParameter("county", userCounty)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(5)
                    .startUpload(); //Starting the upload

            //notify complete account creation
            displayAccountCreation();
            clean();

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                custImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void displayPermissionNeed(){
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Permission Request")
                .setContentText("Required for image uploads")
                .setConfirmText("OK")
                .show();
    }

    public void displayImageError(){ // show image error
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Missing Parameter")
                .setContentText("Select Account Image Please")
                .setConfirmText("OK")
                .show();
    }

    public void displayAccountCreation(){ //account creation notification
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Successfully Created")
                .setContentText("Login to your account.!!")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentLogin()).addToBackStack(null).commit();
                        activity.getSupportActionBar().setTitle("Login");
                    }
                })
                .setCancelText("Later")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
             //Here you can explain why you need this permission
            //Explain here why you need this permission
           displayPermissionNeed();
           // Toast.makeText(getContext(), "Required for Image uploads", Toast.LENGTH_SHORT).show();

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getContext().getApplicationContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getContext().getApplicationContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

}