package com.example.piusin.event;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.piusin.event.HomeFragment.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment  implements SearchView.OnQueryTextListener{

    String categoryName, storeName;
    private int loadValue;

    RecyclerView recyclerView;
    Context context = null;
    List<ProductDataProvider> productDataProviderList;
    ProductAdapter adapter;
    View view;

    MenuItem mCartIconMenuItem;
    TextView mCountTv;
    ImageButton mImageBtn;
    FragmentTransaction fragmentTransaction;
    AppCompatActivity activity;

    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    int count, totalCount = 0;
    private ProgressDialog progressDialog;


    public ProductsFragment() {
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
        activity = (AppCompatActivity) view.getContext();
        progressDialog = new ProgressDialog(context);
        fetchCount();

        //getting bundle data
        Bundle bundle = getArguments();
        if(bundle != null){
            categoryName = bundle.getString("categoryName");
            storeName = bundle.getString("storeName");
            //Toast.makeText(context, "Store Name: " + storeName + "Category Name: " + categoryName, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }

        productDataProviderList = new ArrayList<>();

        /*
        productDataProviderList.add(
                new ProductDataProvider(
                        R.drawable.macbook,
                        "Jogoo (1kg)",
                        90.00
                ));
        productDataProviderList.add(
                new ProductDataProvider(
                        R.drawable.macbook,
                        "Jogoo (1kg)",
                        90.00
                ));
        productDataProviderList.add(
                new ProductDataProvider(
                        R.drawable.macbook,
                        "Jogoo (1kg)",
                        90.00
                ));
        productDataProviderList.add(
                new ProductDataProvider(
                        R.drawable.macbook,
                        "Jogoo (1kg)",
                        90.00
                ));
        */
      //  ProductAdapter productAdapter = new ProductAdapter(context,productDataProviderList);
       // recyclerView.setAdapter(productAdapter);

        if(storeName.equals("all")){ //loads categories for more categories(handcoded)
            loadValue = 0;
            loadProducts();
        }
        else if(storeName.equals("cat")) //loads products for specific categories(handcoded)
        {
            loadValue = 1;
            loadProducts();
        }
        else {
            loadValue = 2;
            loadProductss();
        }

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_activity_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchProduct);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        mCartIconMenuItem = menu.findItem(R.id.icon);
        View actionView = mCartIconMenuItem.getActionView();
        if (actionView != null) {
            mCountTv = actionView.findViewById(R.id.count_tv);
            mImageBtn = actionView.findViewById(R.id.cart_image);
        }


        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mCountTv.setText(String.valueOf(totalCount));
                fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new CartFragment());
                fragmentTransaction.commit();
                activity.getSupportActionBar().setTitle("Cart");
            }
        });
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("Products");
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

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("productDescription", product.getString("product_description"));
                        bundle.putString("storeName", product.getString("store_name"));
                        bundle.putString("productImage", product.getString("product_image"));
                        bundle.putDouble("productCost", product.getDouble("product_cost"));
                        bundle.putString("productName", product.getString("product_name"));

                        if(product.getString("category_name").equals(categoryName))
                            //adding the product to product list
                            productDataProviderList.add(new ProductDataProvider(
                                    product.getString("category_name"),
                                    product.getString("product_description"),
                                    product.getString("store_name"),
                                    product.getString("product_image"),
                                    product.getString("product_name"),
                                    product.getDouble("product_cost")
                            ));

                    }

                    //creating adapter object and setting it to recyclerview
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
                        progressDialog.hide();
                        serverDownAlert();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(context).add(stringRequest);
    }


    //fetching products from server. for specific stores and categories(featured store, handcoded)
    private void loadProductss(){
        progressDialog.setMessage("Fetching Data From Server...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        Bundle bundle = new Bundle();
                        bundle.putString("productDescription", product.getString("product_description"));
                        bundle.putString("storeName", product.getString("store_name"));
                        bundle.putString("productImage", product.getString("product_image"));
                        bundle.putDouble("productCost", product.getDouble("product_cost"));
                        bundle.putString("productName", product.getString("product_name"));

                        if(product.getString("category_name").equals(categoryName) && product.getString("store_name").equals(storeName))
                            //adding the product to product list
                            productDataProviderList.add(new ProductDataProvider(
                                    product.getString("category_name"),
                                    product.getString("product_description"),
                                    product.getString("store_name"),
                                    product.getString("product_image"),
                                    product.getString("product_name"),
                                    product.getDouble("product_cost")
                            ));

                    }

                    //creating adapter object and setting it to recyclerview
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
                        progressDialog.hide();
                        serverDownAlert();
                    }
                });

        //adding our stringrequest to queue
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

    public void fetchCount() {
        cartDbHelper = new CartDbHelper(context);
        sqLiteDatabase = cartDbHelper.getReadableDatabase();
        cursor = cartDbHelper.getInformations(sqLiteDatabase);

        if (cursor.moveToFirst()) {
            do {
                final String prodCount;
                prodCount = cursor.getString(2);
                count = Integer.valueOf(prodCount);
                totalCount = totalCount + count;
            } while (cursor.moveToNext());
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
                        switch (loadValue)
                        {
                            case 0:
                                loadProducts();
                                break;
                            case 1:
                                loadProducts();
                                break;
                            case 2:
                                loadProductss();
                                break;
                            default:
                                Toast.makeText(context, "End of Options", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                })
                .show();
    }


}
