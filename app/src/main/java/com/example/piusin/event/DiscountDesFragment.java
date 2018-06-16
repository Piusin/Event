package com.example.piusin.event;

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


public class DiscountDesFragment extends Fragment {

   //private static final String URL_DISCOUNTS = "http://192.168.101.1/SuperMart/discounts.php";
    RecyclerView recyclerView;
    Context context = null; 
    ArrayList<DiscountDataProvider> productDataProviderList;
    String discountName, storeName, category;
    AppCompatActivity activity;
    View view;


    public DiscountDesFragment() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        productDataProviderList = new ArrayList<>();
        activity = (AppCompatActivity) view.getContext();

        //getting bundle data
        Bundle bundle = getArguments();

        if(bundle != null){
            discountName = bundle.getString("discountName");
            storeName = bundle.getString("storeName");
            category = bundle.getString("category");
           // Toast.makeText(context, category, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
        loadDiscounts();
        return view;
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("Discount Des");
        super.onResume();
    }


    public void loadDiscounts(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_DISCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list
                        if(discountName.equals(product.getString("product_name")) && storeName.equals(product.getString("store_name")))
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
                    DiscountDesAdapter adapter = new DiscountDesAdapter(context, productDataProviderList);
                    recyclerView.setAdapter(adapter);
                }

                catch (JSONException e) {
                    e.printStackTrace();
                    //serverDownAlert();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  serverDownAlert();
                    }
                });
        Volley.newRequestQueue(context).add(stringRequest);
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
                        loadDiscounts();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new DiscountDesFragment()).addToBackStack(null).commit();
                        activity.getSupportActionBar().setTitle("Discount Des");
                    }
                })
                .show();
    }

}

