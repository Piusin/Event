package com.example.piusin.event;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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


public class CartFragment extends Fragment implements View.OnClickListener{

//uses productdataprovider && cartAdapter
    //private static final String URL_PRODUCTS  = "http://192.168.101.1/SuperMart/products.php";

    RecyclerView recyclerView;
    Context context = null;
    List<ProductDataProvider> productDataProviderList;
    View view;


    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    String prodCount;
    double subTotal, productCost;
    TextView cartTotal;
    private Button btnBackToShop, btnCheckOut;

    public CartFragment(){
        //constructor
    }

    @Override
    public void onAttach(Context contexts) {
        super.onAttach(contexts);
        context = contexts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_reyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cartTotal = view.findViewById(R.id.cart_total);
        btnCheckOut = view.findViewById(R.id.btn_check_out);
        btnBackToShop = view.findViewById(R.id.btn_back_toShop);

        btnBackToShop.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);

        productDataProviderList = new ArrayList<>();
        loadProducts();

        return view;
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("Cart");
        super.onResume();
    }


    //fetching products from server.
    private void loadProducts(){

        cartDbHelper = new CartDbHelper(context);
        sqLiteDatabase = cartDbHelper.getReadableDatabase();
        cursor = cartDbHelper.getInformations(sqLiteDatabase);

        if(cursor.moveToFirst())
        {
            do{
                final String prodName, prodDes, prodStoreName;
                prodName = cursor.getString(0);
                prodDes = cursor.getString(1);
                prodCount = cursor.getString(2);
                prodStoreName = cursor.getString(3);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_PRODUCTS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes)
                                        && product.getString("store_name").equals(prodStoreName)){
                                productDataProviderList.add(new ProductDataProvider(
                                        product.getString("category_name"),
                                        product.getString("product_description"),
                                        product.getString("store_name"),
                                        product.getString("product_image"),
                                        product.getString("product_name"),
                                        product.getDouble("product_cost")
                                ));
                                productCost = ((Double.valueOf(product.getString("product_cost"))) * Integer.valueOf(prodCount));
                                subTotal = subTotal + productCost;
                                cartTotal.setText(String.valueOf(subTotal));
                                }

                            }
                            //creating adapter object and setting it to recyclerview
                            CartAdapter adapter = new CartAdapter(context, productDataProviderList);
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

            }while(cursor.moveToNext());
        }

   cartTotal.setText(String.valueOf(subTotal));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back_toShop:
                Toast.makeText(context, "BT clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_check_out:
                Toast.makeText(context, "CO clicked", Toast.LENGTH_SHORT).show();
                break;

                default:
                    Toast.makeText(context, "End of Options", Toast.LENGTH_SHORT).show();
                    break;
        }
    }
}
