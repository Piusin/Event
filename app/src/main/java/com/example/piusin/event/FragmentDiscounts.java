package com.example.piusin.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentDiscounts extends Fragment {

    RecyclerView recyclerView;
    Context context = null;
    ArrayList<DiscountDataProvider> productDataProviderList;
    String categoryName;
    AppCompatActivity activity;
    ProgressDialog progressDialog;
    public int loadvalue;
    View view;


    public FragmentDiscounts() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context contexts) {
        super.onAttach(contexts);
        context = contexts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_products, container, false);
        recyclerView = view.findViewById(R.id.products_reyclerviews);
        recyclerView.setHasFixedSize(true);
        activity = (AppCompatActivity) view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        productDataProviderList = new ArrayList<>();
        progressDialog = new ProgressDialog(context);
        receiveBundleData();

        return view;
    }

    @Override
    public void onResume() {
        activity.getSupportActionBar().setTitle("Discounts");
        super.onResume();
    }


    public void loadDiscounts(){
        progressDialog.setMessage("Fetching Data From Server...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_DISCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list
                        productDataProviderList.add(new DiscountDataProvider(
                                product.getString("product_name"),
                                product.getString("product_image"),
                                product.getDouble("product_cost"),
                                product.getDouble("new_cost"),
                                product.getString("store_name"),
                                product.getString("product_description"),
                                product.getString("category_name")
                        ));
                    }
                    DiscountAdapter adapter = new DiscountAdapter(context, productDataProviderList);
                    recyclerView.setAdapter(adapter);
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        serverDownAlert();
                    }
                });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void loadFilteredDiscounts(){
        progressDialog.setMessage("Fetching Data From Server...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_DISCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list
                        if(categoryName.equals(product.getString("category_name")))
                        productDataProviderList.add(new DiscountDataProvider(
                                product.getString("product_name"),
                                product.getString("product_image"),
                                product.getDouble("product_cost"),
                                product.getDouble("new_cost"),
                                product.getString("store_name"),
                                product.getString("product_description"),
                                product.getString("category_name")
                        ));
                    }
                    DiscountAdapter adapter = new DiscountAdapter(context, productDataProviderList);
                    recyclerView.setAdapter(adapter);
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        serverDownAlert();
                    }
                });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void receiveBundleData(){  //check bundleData
        Bundle bundle = getArguments();

        if(bundle != null){
            categoryName = bundle.getString("category");
        }
        else
        {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }

        if(categoryName.equals("All")){
            loadDiscounts();
            loadvalue = 1;
        }
        else{
            loadFilteredDiscounts();
            loadvalue = 0;
        }
    }

    private void serverDownAlert(){ //check whether server is accessible
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Server Down")
                .setContentText("Ooh no you are not the problem!! " + "\n" + "Its Us...")
                .setConfirmText("Retry")
                .setCancelText("Exit")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        //Toast.makeText(context, "Load Value: " + loadvalue, Toast.LENGTH_SHORT).show();
                        switch (loadvalue){
                            case 0:
                                //Toast.makeText(context, "Load 1: " + productDataProviderList.size(), Toast.LENGTH_SHORT).show();
                                loadFilteredDiscounts();
                                break;
                            case 1:
                               // Toast.makeText(context, "Load 2: " + productDataProviderList.size(), Toast.LENGTH_SHORT).show();
                                loadDiscounts();
                                break;
                                default:
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    startActivity(intent);
                                    break;
                        }
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

}
