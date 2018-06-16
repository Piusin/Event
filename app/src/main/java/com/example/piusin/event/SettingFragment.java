package com.example.piusin.event;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

//for product search coz of bundle checks
public class SettingFragment extends Fragment implements SearchView.OnQueryTextListener{


    //private static final String URL_PRODUCTS  = "http://192.168.101.1/SuperMart/products.php";
    ProductAdapter adapter;
    RecyclerView recyclerView;
    Context context = null;
    List<ProductDataProvider> productDataProviderList;
    View view;

    public SettingFragment() {
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

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_products, container, false);
        recyclerView = view.findViewById(R.id.products_reyclerviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        productDataProviderList = new ArrayList<>();
        loadProduct();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_activity_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchProduct);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    private void loadProduct(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);

                            productDataProviderList.add(new ProductDataProvider(
                                    product.getString("category_name"),
                                    product.getString("product_description"),
                                    product.getString("store_name"),
                                    product.getString("product_image"),
                                    product.getString("product_name"),
                                    product.getDouble("product_cost")
                            ));
                        }

                    adapter = new ProductAdapter(context, productDataProviderList);
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
        Volley.newRequestQueue(context).add(stringRequest);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<ProductDataProvider> newList = new ArrayList<>();

        for(ProductDataProvider dataProvider: productDataProviderList){

            String name = dataProvider.getProductName().toLowerCase();
            if(name.contains(newText))
                newList.add(dataProvider);
        }
        adapter.setFilter(newList);
        recyclerView.setAdapter(adapter);
        return  true;

    }
}
