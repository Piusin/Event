package com.example.piusin.event;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Piusin on 2/19/2018.
 */

public class ProductDescAdapter extends RecyclerView.Adapter<ProductDescAdapter.ProductsViewHolder> implements SweetAlertClass{

    private Context mCtx;
    AppCompatActivity activity;
    private List<ProductDescDataProvider> productDescDataProviderList;
    private ProductDescDataProvider productDataProvider;
    SharedPreferences sharedPreferences;
    private String productName, productDesc;
    private ArrayList<AlgorithmProductDataProvider> algorithmProductDataProviderArrayList;
    private List<AlgorithmProductDataProvider> sortedAlgorithmDataProviderList;
    private int i;

    public ProductDescAdapter(Context mCtx, List<ProductDescDataProvider> productDescDataProviderList) {
        this.mCtx = mCtx;
        this.productDescDataProviderList = productDescDataProviderList;
    }


    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_descitems, null);
        return new ProductDescAdapter.ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        productDataProvider = productDescDataProviderList.get(position);
        Picasso.with(mCtx)
                .load(productDataProvider.getProductDesImage())
                .into(holder.productDesImage);
        holder.productDesName.setText(productDataProvider.getProductDesName());
        holder.productDesCost.setText("Ksh " + String.valueOf(productDataProvider.getProductDesCost()));
        holder.productDes.setText(productDataProvider.getProductDescription());
        holder.storeName.setText(productDataProvider.getProductDesStoreName());
    }

    @Override
    public int getItemCount() {
        return productDescDataProviderList.size();
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView productDesImage;
        TextView productDesName;
        TextView productDesCost;
        TextView productDes;
        TextView storeName;
        Button buy, nearestMall, addToCart;


        public ProductsViewHolder(View itemView) {
            super(itemView);

            productDesImage = itemView.findViewById(R.id.prodDescImage);
            productDesName = itemView.findViewById(R.id.productdesName);
            productDesCost = itemView.findViewById(R.id.productdesCost);
            productDes = itemView.findViewById(R.id.productDescription);
            storeName = itemView.findViewById(R.id.productdesStore);
            buy = itemView.findViewById(R.id.btnBuyProduct);
            nearestMall = itemView.findViewById(R.id.btnNearestmall);
            addToCart = itemView.findViewById(R.id.btnaddToBasket);

            buy.setOnClickListener(this);
            nearestMall.setOnClickListener(this);
            addToCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(IsInternetOn() != true){
                displayNoInternetAlert();
            }
            else {
                AppCompatActivity activity;
                switch (v.getId()) {
                    case R.id.btnBuyProduct:
                       // activity = (AppCompatActivity) v.getContext();
                        Toast.makeText(mCtx, "Buy " + productDataProvider.getProductDesName(), Toast.LENGTH_SHORT).show();
                        //activity.startActivity(new Intent(mCtx, MapsActivity2.class));

                        break;

                    case R.id.btnNearestmall:
                       // loadAlgorithmProducts();
                        MapFragment mapFragment = new MapFragment();
                        Toast.makeText(mCtx, "Get Nearest Mall with " + productDataProvider.getProductDesName(), Toast.LENGTH_SHORT).show();
                        activity = (AppCompatActivity) v.getContext();
                        Bundle bundle = new Bundle();
                        bundle.putString("productName", productDataProvider.getProductDesName());
                        bundle.putString("productDesc", productDataProvider.getProductDescription());
                       // bundle.putDouble("store_longitude", sortedAlgorithmDataProviderList.get(0).storeLongitude);
                        mapFragment.setArguments(bundle);
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_container, mapFragment).addToBackStack(null).commit();
                        break;

                    case R.id.btnaddToBasket:
                        //loadAlgorithmProducts();

                       /* Toast.makeText(mCtx, "StoreName: " +sortedAlgorithmDataProviderList.get(0).storeName +  " Latitude: " + sortedAlgorithmDataProviderList.get(0).storeLatitude +
                                " Longitude: " + sortedAlgorithmDataProviderList.get(0).storeLongitude + " ProductName: " + sortedAlgorithmDataProviderList.get(0).productName + " ProductCost: "+
                                sortedAlgorithmDataProviderList.get(0).productCost, Toast.LENGTH_SHORT).show();
                        */Toast.makeText(mCtx, "Add " + productDataProvider.getProductDesName() + " to the basket", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(mCtx, "End of Options", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    public void displayNoInternetAlert() {
        new SweetAlertDialog(mCtx, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Network Problem")
                .setContentText("Looks like you are offline!! " + "\n" + "Check Internet Connection..for better application use")
                .setConfirmText("Ok!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public boolean IsInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) mCtx.getSystemService(mCtx.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            //Toast.makeText(this, "There is internet", Toast.LENGTH_SHORT).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }


    //fetching products from server.
    private void loadAlgorithmProducts(){
        algorithmProductDataProviderArrayList = new ArrayList<>();
        productName = productDataProvider.getProductDesName();
        productDesc = productDataProvider.getProductDescription();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_ALGORITHMPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    JSONArray array = new JSONArray(response);
                    for (i = 0; i < array.length(); i++) {

                         JSONObject product = array.getJSONObject(i);

                         if(product.getString("product_name").contains(productName))
                            //adding the product to arrayList
                            algorithmProductDataProviderArrayList.add(new AlgorithmProductDataProvider(
                                    product.getString("product_name"),
                                    product.getString("store_name"),
                                    product.getDouble("product_cost"),
                                    product.getDouble("store_latitude"),
                                    product.getDouble("store_longitude"),
                                    product.getInt("quantity_at_hand")
                            ));
                    }
/*
                    for(int index = 0; index<algorithmProductDataProviderArrayList.size(); index++)
                    Toast.makeText(mCtx, "Index: " + index + " " + algorithmProductDataProviderArrayList.get(index).getStoreLatitude(), Toast.LENGTH_SHORT).show();
                       */ //Toast.makeText(mCtx, "ArrayList: " + algorithmProductDataProviderArrayList.size(), Toast.LENGTH_SHORT).show();
                        int middle = (int) Math.ceil((double) algorithmProductDataProviderArrayList.size() / 2);
                        double pivot = algorithmProductDataProviderArrayList.get(middle).getProductCost();
                        //Toast.makeText(mCtx, "Middle: " + middle + "\n" + "Pivot: " + pivot, Toast.LENGTH_SHORT).show();

                        List<AlgorithmProductDataProvider> pivotList = new ArrayList<>();
                        List<AlgorithmProductDataProvider> less = new ArrayList<AlgorithmProductDataProvider>();
                        List<AlgorithmProductDataProvider> greater = new ArrayList<AlgorithmProductDataProvider>();

                        for (int i = 0; i < algorithmProductDataProviderArrayList.size(); i++) {
                            if (algorithmProductDataProviderArrayList.get(i).getProductCost() <= pivot) {
                                if (i == middle) {
                                    continue;
                                }
                                less.add(algorithmProductDataProviderArrayList.get(i));
                            } else {
                                greater.add(algorithmProductDataProviderArrayList.get(i));
                            }
                        }
                        pivotList.add(algorithmProductDataProviderArrayList.get(middle));
                        concatenate(less, pivotList, greater);
                    //Toast.makeText(mCtx, "ListMen: " + concatenate(less,pivotList,greater).toString(), Toast.LENGTH_SHORT).show();

                    if(sortedAlgorithmDataProviderList.size()!= 0){//send data to mapFragment

                        MapFragment mapFragment = new MapFragment();
                        Toast.makeText(mCtx, "Get Nearest Mall with " + productDataProvider.getProductDesName(), Toast.LENGTH_SHORT).show();
                        activity = (AppCompatActivity) mCtx;
                        Bundle bundle = new Bundle();
                        bundle.putString("storeName", sortedAlgorithmDataProviderList.get(0).storeName);
                        bundle.putDouble("storeLatitude", sortedAlgorithmDataProviderList.get(0).storeLatitude);
                        bundle.putDouble("storeLongitude", sortedAlgorithmDataProviderList.get(0).storeLongitude);
                        mapFragment.setArguments(bundle);

                        /*Toast.makeText(mCtx, "StoreName: " +sortedAlgorithmDataProviderList.get(0).storeName +  " Latitude: " + sortedAlgorithmDataProviderList.get(0).storeLatitude +
                         " Longitude: " + sortedAlgorithmDataProviderList.get(0).storeLongitude + " ProductName: " + sortedAlgorithmDataProviderList.get(0).productName + " ProductCost: "+
                                sortedAlgorithmDataProviderList.get(0).productCost, Toast.LENGTH_SHORT).show();*/
                       /* SharedPreferences.Editor editor = activity.getSharedPreferences("storePref", MODE_PRIVATE).edit();
                        editor.putString("store", "Khetias Crossroads");
                        editor.apply();*/



                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_container, mapFragment).addToBackStack(null).commit();

                    }
                    else{
                        Toast.makeText(mCtx, "Empty", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(mCtx, "StoreName: " +sortedAlgorithmDataProviderList.get(0).storeName +  " Latitude: " + sortedAlgorithmDataProviderList.get(0).storeLatitude +
                            " Longitude: " + sortedAlgorithmDataProviderList.get(0).storeLongitude + " ProductName: " + sortedAlgorithmDataProviderList.get(0).productName + " ProductCost: "+
                            sortedAlgorithmDataProviderList.get(0).productCost, Toast.LENGTH_SHORT).show();


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
        Volley.newRequestQueue(mCtx).add(stringRequest);

    }


    /*private List<AlgorithmProductDataProvider> quicksort(List<AlgorithmProductDataProvider> algorithmProductDataProviderArrayList)
    {
        Toast.makeText(mCtx, "Size2: " + algorithmProductDataProviderArrayList.size(), Toast.LENGTH_SHORT).show();

        int middle = (int) Math.ceil((double) algorithmProductDataProviderArrayList.size() / 2);
        double pivot = 2;
//        double pivot = algorithmProductDataProviderArrayList.get(middle).getProductCost();
        Toast.makeText(mCtx, "Middle: "+ middle + "pivot: " + pivot, Toast.LENGTH_SHORT).show();

        List<AlgorithmProductDataProvider> less = new ArrayList<AlgorithmProductDataProvider>();
        List<AlgorithmProductDataProvider> greater = new ArrayList<AlgorithmProductDataProvider>();

        for (int i = 0; i < algorithmProductDataProviderArrayList.size(); i++) {
            if (algorithmProductDataProviderArrayList.get(i).getProductCost() <= pivot) {
                if (i == middle) {
                    continue;
                }
                less.add(algorithmProductDataProviderArrayList.get(i));
            } else {
                greater.add(algorithmProductDataProviderArrayList.get(i));
            }
        }
        Toast.makeText(mCtx, "Less: " + less.size() + "Greater: " + greater.size(), Toast.LENGTH_SHORT).show();
        return concatenate(quicksort(less), pivotList, quicksort(greater));
    }*/

    private List<AlgorithmProductDataProvider> concatenate(List<AlgorithmProductDataProvider> less, List<AlgorithmProductDataProvider> pivotList, List<AlgorithmProductDataProvider> greater){

        int index;

        sortedAlgorithmDataProviderList = new ArrayList<AlgorithmProductDataProvider>();

        for (int i = 0; i < less.size(); i++) {
            sortedAlgorithmDataProviderList.add(less.get(i));
        }

        sortedAlgorithmDataProviderList.add(pivotList.get(0));

        for (int i = 0; i < greater.size(); i++) {
            sortedAlgorithmDataProviderList.add(greater.get(i));
        }

       /* for(index =0; index< sortedAlgorithmDataProviderList.size() ; index++){
            Toast.makeText(mCtx, "Index: " + index + "ProductCost: " + sortedAlgorithmDataProviderList.get(index).getProductCost() + "StoreName: " + sortedAlgorithmDataProviderList.get(index).getStoreName(), Toast.LENGTH_SHORT).show();
        }*/
        return sortedAlgorithmDataProviderList;
    }



    public void additemToCart(){

    }



}

