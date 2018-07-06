package com.example.piusin.event;


import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Whats New Description fragment
 */
public class WhatsNewDes extends Fragment {
    //private static final String URL_NEW = "http://192.168.101.1/SuperMart/newProducts.php";
    RecyclerView recyclerView;
    Context context = null;
    ArrayList<WhatsNewMainDataProvider> newDataProviderList;
    String storeName, productName;
    WhatsDesAdapter adapter;
    View view;


    public WhatsNewDes() {
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
        newDataProviderList = new ArrayList<>();

        //getting bundle data
        Bundle bundle = getArguments();
        if(bundle != null){
            productName = bundle.getString("productName");
            storeName = bundle.getString("productStore");
            //Toast.makeText(context, productName + " " + storeName, Toast.LENGTH_SHORT).show();
            loadNew();
        }
        else
        {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("New Item Des");
        super.onResume();
    }


    //fetching products from server.
    private void loadNew(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_NEWPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list
                        if(productName.equals(product.getString("product_name")) && storeName.equals(product.getString("store_name")))
                        newDataProviderList.add(new WhatsNewMainDataProvider(
                                product.getString("category_name"),
                                product.getString("product_description"),
                                product.getString("store_name"),
                                product.getString("store_location"),
                                product.getString("product_image"),
                                product.getString("product_name"),
                                product.getDouble("product_cost"),
                                product.getString("store_latitude"),
                                product.getString("store_longitude")
                        ));

                    }

                    //creating adapter object and setting it to recyclerview
                    adapter = new WhatsDesAdapter(context, newDataProviderList);
                    recyclerView.setAdapter(adapter);
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

        //adding our stringrequest to queue
        Volley.newRequestQueue(context).add(stringRequest);
    }

}
