package com.example.piusin.event;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class WhatsNewFragment extends Fragment {


    //private static final String URL_PRODUCTS  = "http://192.168.101.1/SuperMart/newProducts.php";
    String categoryName, storeName;
    ProgressDialog progressDialog;

    RecyclerView recyclerView;
    Context context = null;
    List<WhatsNewMainDataProvider> productDataProviderList;
    WhatsNewMainAdapter adapter;
    View view;

    FragmentTransaction fragmentTransaction;
    AppCompatActivity activity;


    public WhatsNewFragment() {
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
        activity = (AppCompatActivity) view.getContext();
        progressDialog = new ProgressDialog(context);

        productDataProviderList = new ArrayList<>();

        loadProducts();
        return view;
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("WhatsNew");
        super.onResume();
    }

    //fetching products from server.
    private void loadProducts(){
        progressDialog.setMessage("Fetching Data From Server");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_NEWPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{

                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("productDescription", product.getString("product_description"));
                        bundle.putString("storeName", product.getString("store_name"));
                        bundle.putString("productImage", product.getString("product_image"));
                        bundle.putDouble("productCost", product.getDouble("product_cost"));
                        bundle.putString("productName", product.getString("product_name"));

                            //adding the product to product list
                            productDataProviderList.add(new WhatsNewMainDataProvider(
                                    product.getString("category_name"),
                                    product.getString("product_description"),
                                    product.getString("store_name"),
                                    product.getString("store_location"),
                                    product.getString("product_image"),
                                    product.getString("product_name"),
                                    product.getDouble("product_cost")
                            ));

                    }

                    //creating adapter object and setting it to recyclerview
                    adapter = new WhatsNewMainAdapter(context, productDataProviderList);
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

    private void serverDownAlert(){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Server Down")
                .setContentText("Ooh no you are not the problem!! " + "\n" + "Its Us...")
                .setConfirmText("Retry")
                .setCancelText("Exit")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                    }
                })
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
