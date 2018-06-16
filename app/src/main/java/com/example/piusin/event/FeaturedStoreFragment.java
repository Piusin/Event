package com.example.piusin.event;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.List;


public class FeaturedStoreFragment extends Fragment {

   // private static final String URL_PRODUCTS  = "http://192.168.101.1/SuperMart/getStores.php";
    RecyclerView recyclerView;
    Context context = null;
    ArrayList<FeaturedStoreMainDataProvider> featuredStoreDataProviderList;
    View view;

    public FeaturedStoreFragment() {
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
       // recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        featuredStoreDataProviderList = new ArrayList<>();
        loadProducts();
        return view;
    }

    //fetching products from server.
    private void loadProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_PRODUCTSFSTORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);

                            featuredStoreDataProviderList.add(new FeaturedStoreMainDataProvider(
                                    product.getString("store_image"),
                                    product.getString("store_name"),
                                    product.getString("store_opentime"),
                                    product.getString("store_location"),
                                    product.getDouble("store_distance"),
                                    product.getString("store_closetime")
                            ));
                    }

                    //creating adapter object and setting it to recyclerview
                    FeaturedStoreAdapter adapter = new FeaturedStoreAdapter(context, featuredStoreDataProviderList);
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



