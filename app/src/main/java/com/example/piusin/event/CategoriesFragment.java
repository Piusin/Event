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
import android.support.v7.widget.GridLayoutManager;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.HashMap;
import java.util.Map;

public class CategoriesFragment extends Fragment implements SearchView.OnQueryTextListener{

    RecyclerView recyclerView;
    Context context = null;
    ArrayList<CategoriesDataProviders> categoriesDataProviderList;
    CategoriesAdapterMain adapter;
    View view;

    Bundle bundle;
    private String storeName;
    private String categoryName;
    private ProgressDialog progressDialog;

    MenuItem mCartIconMenuItem;
    TextView mCountTv;
    ImageButton mImageBtn;
    FragmentTransaction fragmentTransaction;
    AppCompatActivity activity;

    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    int count, totalCount = 0;

    public CategoriesFragment() {
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
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
       // recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        progressDialog = new ProgressDialog(context);
        categoriesDataProviderList = new ArrayList<>();
        activity = (AppCompatActivity) view.getContext();
        fetchCount();

        bundle = getArguments();
        if(bundle != null){
            storeName = bundle.getString("storeName");
            sendStore();
        }
        else{
            loadCategories();
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
               // mCountTv.setText(String.valueOf(totalCount));
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
        activity.getSupportActionBar().setTitle("Categories");
        super.onResume();
    }

    //fetching products from server.
    private void loadProducts(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    //converting the string to json array object
                    JSONArray array = new JSONArray(response);

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                       // Toast.makeText(context, "Store Name 2: " + storeName, Toast.LENGTH_SHORT).show();

                        if (product.getString("store_name").equals(storeName)) {
                            //adding the product to product list
                            categoriesDataProviderList.add(new CategoriesDataProviders(
                                    product.getString("category_name"),
                                    product.getString("category_icon"),
                                    product.getString("store_name")
                            ));

                        }
                    }

                    //creating adapter object and setting it to recyclerview
                    adapter = new CategoriesAdapterMain(context, categoriesDataProviderList);
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

  public void sendStore() {

        progressDialog.setMessage("Fetching Data From Server...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_STORES,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    progressDialog.dismiss();
                        try {
                           JSONObject jsonObject = new JSONObject(response);
                           categoryName = jsonObject.getString("message");

                       if(categoryName != null){
                           loadProducts();
                           //Toast.makeText(context, "Category Name: " + categoryName, Toast.LENGTH_SHORT).show();
                       }
                       else{
                           Toast.makeText(context, "CategoryName is null", Toast.LENGTH_SHORT).show();
                       }

                        } catch (JSONException e) {
                            e.printStackTrace();
                           // Toast.makeText(context, "Pius", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     progressDialog.hide();
                     serverDownAlert();
                        //Toast.makeText(context, "Piusin" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("store", storeName);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
  }


  //Load Categories.
  //fetching products from server.
  private void loadCategories(){

      StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_STORECAT, new Response.Listener<String>() {
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
                          categoriesDataProviderList.add(new CategoriesDataProviders(
                                  product.getString("category_name"),
                                  product.getString("category_icon"),
                                  product.getString("store_name")
                          ));


                  }

                  //creating adapter object and setting it to recyclerview
                  CategoriesAdapterMain adapter = new CategoriesAdapterMain(context, categoriesDataProviderList);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<CategoriesDataProviders> newList = new ArrayList<>();

        for(CategoriesDataProviders dataProvider: categoriesDataProviderList){

            String name = dataProvider.getCategoryName().toLowerCase();
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
                        sendStore();
                    }
                })
                .show();
    }


}
