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
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    static Context context;
    private static ArrayList<Object> objects = new ArrayList<>();
    //private static final String URL_DISCOUNTS = "http://192.168.101.1/SuperMart/discounts.php";


    static ArrayList<CategoriesDataProvider>  singleVerticals = new ArrayList<>();
    View view;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_View);
        MainAdapter adapter = new MainAdapter(context, getObject());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle("All Mart");
        objects.clear();
        getObject();
    }


    private ArrayList<Object> getObject() {
        objects.add(getVerticalData().get(0));
        objects.add(getHorizontalData().get(0));
        objects.add(getDiscountData().get(0));
        objects.add(getWhatsNewData().get(0));
        return objects;
    }



    public static ArrayList<CategoriesDataProvider> getVerticalData() {
        singleVerticals = new ArrayList<>();

        singleVerticals.add(
                new CategoriesDataProvider(
                        "Computing",
                        R.drawable.computing_category
                ));
        singleVerticals.add(
                new CategoriesDataProvider(
                        "Fashion",
                        R.drawable.fashion_category
                ));
        singleVerticals.add(
                new CategoriesDataProvider(
                        "Flour",
                        R.drawable.flour_category
                ));
        singleVerticals.add(
                new CategoriesDataProvider(
                        "More",
                        R.drawable.arrow
                ));


       return singleVerticals;
    }

    public static ArrayList<FeaturedStoreDataProvider> getHorizontalData() {
        ArrayList<FeaturedStoreDataProvider> singleHorizontals = new ArrayList<>();

        singleHorizontals.add(
                new FeaturedStoreDataProvider(
                        R.drawable.khetias,
                        "Khetias Crossroads",
                        "Open Hours: 7.50Am - 9.00Pm",
                        "Location: Bungoma",
                        60000));

        singleHorizontals.add(
                new FeaturedStoreDataProvider(
                        R.drawable.tesia,
                        "Tesia Frontier",
                        "Open Hours:8.00Am - 8.00Pm",
                        "Location: Kisumu",
                        60000));

        singleHorizontals.add(
                new FeaturedStoreDataProvider(
                        R.drawable.naivas,
                        "Naivas Bandap",
                        "Open Hours:9.00Am - 11.00Pm",
                        "Location: Eldoret",
                        60000));
        singleHorizontals.add(
                new FeaturedStoreDataProvider(
                        R.drawable.arrow,
                        "",
                        "",
                        "More",
                        0));
       // Toast.makeText(context, "Supermart Has: " + singleHorizontals, Toast.LENGTH_SHORT).show();

        return singleHorizontals;
    }

/*
    public static ArrayList<DiscountDataProvider> getDiscountData(){
        singleDiscounts = new ArrayList<>();
       // getDiscountData().clear();
        //Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DISCOUNTS, new Response.Listener<String>() {
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
                        singleDiscounts.add(new DiscountDataProvider(
                                product.getString("product_name"),
                                product.getString("product_image"),
                                product.getDouble("product_cost"),
                                product.getDouble("new_cost")
                        ));
                        //Toast.makeText(context, "ggg"+ getVerticalData(), Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(context, "Discount: " + getDiscountData().size(), Toast.LENGTH_SHORT).show();
                    //objects.add(getDiscountData().get(0));

                    //Toast.makeText(context, "Outside Onresposne" + singleVerticals, Toast.LENGTH_SHORT).show();
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
        return singleDiscounts;
    }*/
/*
    public static ArrayList<DiscountDataProvider> getDiscountData() {
        ArrayList<DiscountDataProvider> singleDiscounts = new ArrayList<>();

        singleDiscounts.add(
                new DiscountDataProvider(
                        "Macbook",
                        R.drawable.macbook,
                        40000,
                        35000
                ));
        singleDiscounts.add(
                new DiscountDataProvider(
                        "Dellinspiron",
                        R.drawable.dellinspiron,
                        50000,
                        40000
                ));
        singleDiscounts.add(
                new DiscountDataProvider(
                        "Surface",
                        R.drawable.surface,
                        35000,
                        33000
                ));

        return singleDiscounts;
    }*/


/*
    public static void loadCat(){
        Toast.makeText(context, "LoadCat: " + getDiscountData().size(), Toast.LENGTH_SHORT).show();
        singleDiscounts = new ArrayList<>();
        //Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DISCOUNTS, new Response.Listener<String>() {
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
                        singleDiscounts.add(new DiscountDataProvider(
                                product.getString("product_name"),
                                product.getString("product_image"),
                                product.getDouble("product_cost"),
                                product.getDouble("new_cost")
                        ));
                        //Toast.makeText(context, "ggg"+ getVerticalData(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, "Discount: " + getDiscountData().size(), Toast.LENGTH_SHORT).show();
                   objects.add(getDiscountData().get(0));

                    //Toast.makeText(context, "Outside Onresposne" + singleVerticals, Toast.LENGTH_SHORT).show();
                }

                catch (JSONException e) {
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


    public static ArrayList<DiscountDataProvider> getDiscountData(){
          Toast.makeText(context, "Wachira has: " + singleDiscounts + "Size: "+ singleDiscounts.size(), Toast.LENGTH_SHORT).show();
          return  singleDiscounts;
    }*/


    public static ArrayList<DiscountMainDataProvider> getDiscountData() {
         ArrayList<DiscountMainDataProvider> singleDiscounts = new ArrayList<>();

        singleDiscounts.add(
                new DiscountMainDataProvider(
                        "Computing",
                        R.drawable.hp
                ));
        singleDiscounts.add(
                new DiscountMainDataProvider(
                        "Fashion",
                        R.drawable.fashion_category
                ));
        singleDiscounts.add(
                new DiscountMainDataProvider(
                        "Flour",
                        R.drawable.flour_category
                ));
        singleDiscounts.add(
                new DiscountMainDataProvider(
                        "More",
                        R.drawable.arrow
                ));


        return singleDiscounts;
    }


    public static ArrayList<WhatsNewDataProvider> getWhatsNewData() {
        ArrayList<WhatsNewDataProvider> singleWhatsNew = new ArrayList<>();
        singleWhatsNew.add(
                new WhatsNewDataProvider(
                        R.drawable.cameras,
                        "Cameras"
                ));
        singleWhatsNew.add(
                new WhatsNewDataProvider(
                        R.drawable.phone,
                        "Phones"
                ));
        singleWhatsNew.add(
                new WhatsNewDataProvider(
                        R.drawable.utensils,
                        "Utensils"
                ));
        singleWhatsNew.add(
                new WhatsNewDataProvider(
                        R.drawable.arrow,
                        "More"
                ));
        return singleWhatsNew;
    }

}