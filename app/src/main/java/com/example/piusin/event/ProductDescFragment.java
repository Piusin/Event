 package com.example.piusin.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;


 public class ProductDescFragment extends Fragment {
     private String productName;

     RecyclerView recyclerView;
     Context context = null;
     List<ProductDescDataProvider> productDataProviderList;
     ProgressDialog progressDialog;
     AppCompatActivity activity;
     View view;


    public ProductDescFragment() {
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

        view = inflater.inflate(R.layout.fragment_product_desc, container, false); //for future upgrades

        recyclerView = view.findViewById(R.id.products_reyclerviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        progressDialog = new ProgressDialog(context);
        productDataProviderList = new ArrayList<>();
        progressDialog = new ProgressDialog(context);
        activity = (AppCompatActivity) view.getContext();

        //getting bundle data
        Bundle bundle = getArguments();

        if(bundle != null){
            productName = bundle.getString("productName");
        }
        else
        {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }

        loadProducts();
        return view;

    }

     @Override
     public void onResume() {
         AppCompatActivity activity = (AppCompatActivity) view.getContext();
         activity.getSupportActionBar().setTitle("Product Des");
         super.onResume();
     }

     //fetching products from server.
     private void loadProducts(){
         progressDialog.setMessage("Fetching Data From Server...");
         progressDialog.show();

         StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_PRODUCTS, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 progressDialog.dismiss();
                 try{

                     JSONArray array = new JSONArray(response);
                     for (int i = 0; i < array.length(); i++) {
                         JSONObject product = array.getJSONObject(i);
                         if(product.getString("product_name").equals(productName))
                         //adding the product to product list
                         productDataProviderList.add(new ProductDescDataProvider(
                                 product.getString("product_image"),
                                 product.getString("product_name"),
                                 product.getDouble("product_cost"),
                                 product.getString("product_description"),
                                 product.getString("store_name")
                         ));
                     }
                     //creating adapter object and setting it to recyclerview
                     ProductDescAdapter adapter = new ProductDescAdapter(context, productDataProviderList);
                     recyclerView.setAdapter(adapter);
                 } catch (JSONException e) {
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

         //adding our stringrequest to queue
         Volley.newRequestQueue(context).add(stringRequest);

     }

     private void serverDownAlert(){ //check whether server is accessible
         new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                 .setTitleText("Server Down")
                 .setContentText("Ooh no you are not the problem!! " + "\n" + "Its Us...")
                 .setConfirmText("Retry")
                 .setCancelText("Exit")
                 /*.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                     @Override
                     public void onClick(SweetAlertDialog sweetAlertDialog) {
                         sweetAlertDialog.dismissWithAnimation();
                         activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProductsFragment()).addToBackStack(null).commit();
                         activity.getSupportActionBar().setTitle("Products");

                     }
                 })*/
                 .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                     @Override
                     public void onClick(SweetAlertDialog sweetAlertDialog) {
                         sweetAlertDialog.dismissWithAnimation();
                         loadProducts();
                     }
                 })
                 .show();
     }
}
