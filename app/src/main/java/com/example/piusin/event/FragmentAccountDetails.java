package com.example.piusin.event;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAccountDetails extends Fragment implements View.OnClickListener,Spinner.OnItemSelectedListener{
    View view;
    Context context = null;
    AppCompatActivity activity;
    Button bLogout, bUpdate;
    EditText name, email;
    ImageView imageView;

    private int PICK_IMAGE_REQUEST = 1; //Image request code
    private static final int STORAGE_PERMISSION_CODE = 123; //storage permission code
    private Bitmap bitmap; ////Bitmap to get image froMm gallery
    private Uri filePath;   //Uri to store the image uri
    private String path, cname, cemail, userCounty, oldEmail ;
    Spinner spinner;
    private ArrayList<String> counties;
    private JSONArray county;


    public FragmentAccountDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fragment_account_details, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();
        requestStoragePermission();
        //if the user is not logged in start login fragment
        if (!SharedPrefManager.getInstance(context).isLoggedIn()) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentLogin()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Login");
        }

        name = view.findViewById(R.id.ad_name);
        email = view.findViewById(R.id.ad_email);
        //county = view.findViewById(R.id.ad_county);
        imageView = view.findViewById(R.id.ad_image);
        bLogout = view.findViewById(R.id.ad_bLogout);
        bUpdate = view.findViewById(R.id.ad_bUpdate);
        bUpdate.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        imageView.setOnClickListener(this);
        counties = new ArrayList();
        spinner = view.findViewById(R.id.account_spinners);
        spinner.setOnItemSelectedListener(this);
        getData();

        //getting the current user
        User user = SharedPrefManager.getInstance(context).getUser();
        //setting the values to the textviews
        name.setText(user.getCust_name());
        email.setText(user.getCust_email());

        oldEmail = user.getCust_email().trim();
       // county.setText(user.getCust_county());
        Picasso.with(context)
                .load(user.getUrl())
                .into(imageView);

        // spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(user.getCust_county()));

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_bLogout:
                logout();
                break;

            case R.id.ad_bUpdate:
                uploadMultipart();
                break;

            case R.id.ad_image:
                showFileChooser();
                break;

            default:
                Toast.makeText(context, "End of options.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

   private void logout(){
        SharedPrefManager.getInstance(getContext()).logout();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FragmentLogin()).addToBackStack(null).commit();
        activity.getSupportActionBar().setTitle("Login");
    }


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
            //displayImageError();
        }
        return  path;
    }

    private void validations() {
       cname = name.getText().toString().trim();
       cemail = email.getText().toString().trim();
      // ccounty = county.getText().toString().trim();
        if(TextUtils.isEmpty(cname)){
            name.setError("Please enter username");
            name.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(cemail)){
            email.setError("Please enter Email");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(cemail).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(userCounty)){
           // county.setError("Please enter username");
            //county.requestFocus();
            return;
        }
        if(path == null){
            updateUser();
        }
        else{
            uploadCode();
        }

    }

    private void updateUser() { //for update when no image update
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                displayAccountUpdation();
                                //Toast.makeText(getContext().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("name", cname);
                params.put("email", cemail);
                params.put("county", userCounty);
                params.put("old_email", oldEmail);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void uploadCode(){ //has Image Updates
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(context, uploadId, URLs.UPDATE_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", cname) //Adding text parameter to the request
                    .addParameter("email", cemail)
                    .addParameter("county", userCounty)
                    .addParameter("old_email", oldEmail)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(5)
                    .startUpload(); //Starting the upload

            //notify complete account creation
            displayAccountUpdation();

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void displayAccountUpdation(){ //account updation notification
        SharedPrefManager.getInstance(getContext()).logout();
        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Successfully Updated")
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
                imageView.setImageBitmap(bitmap);

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







