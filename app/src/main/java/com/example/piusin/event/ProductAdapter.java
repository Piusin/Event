package com.example.piusin.event;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Piusin on 1/29/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsViewHolder> implements SweetAlertClass{
    private Context mCtx;
    private List<ProductDataProvider> productDataProviderList;
    String categoryName;
    ProductDataProvider productDataProvider;

    CartDbHelper cartDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String productNames, productDes, productCount, storeName;
    int position;
    static int cursorKey = 0;

    TextView mCount;

    //constructor

    public ProductAdapter(Context mCtx, List<ProductDataProvider> productDataProviderList) {
        this.mCtx = mCtx;
        this.productDataProviderList = productDataProviderList;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.products_items, null);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position){
         productDataProvider = productDataProviderList.get(position);
         //holder.productAddToCart.setTag(position);
        Picasso.with(mCtx)
                .load(productDataProvider.getProductImage())
                .into(holder.productImage);
        holder.productName.setText(productDataProvider.getProductName());
        holder.productPrice.setText("Ksh " + String.valueOf(productDataProvider.getProductPrice()));
        holder.storeName.setText(productDataProvider.getStoreName());
        categoryName = productDataProvider.getCategoryName();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsInternetOn() != true) {
                    displayNoInternetAlert();

                } else {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();

                    Bundle bundle = new Bundle();
                    bundle.putString("productDescription", productDataProvider.getProductDescription());
                    bundle.putString("storeName", productDataProvider.getStoreName());
                    bundle.putString("productImage", productDataProvider.getProductImage());
                    bundle.putDouble("productCost", productDataProvider.getProductPrice());
                    bundle.putString("productName", productDataProvider.getProductName());

                    ProductDescFragment productDescFragment = new ProductDescFragment();
                    productDescFragment.setArguments(bundle);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, productDescFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Product");

                }
            }
        });

        productNames = productDataProvider.getProductName();
        productDes = productDataProvider.getProductDescription();
        productCount = "1";
        storeName = productDataProvider.getStoreName();

}

    @Override
    public int getItemCount() {
        return productDataProviderList.size();
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView storeName;
        CardView cardView;

        Button productBuy;
        ImageButton productAddToCart;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.prouct_name);
            productPrice = itemView.findViewById(R.id.products_price);
            storeName = itemView.findViewById(R.id.store_name);
            cardView = itemView.findViewById(R.id.cardview_featuredstoreproducts);

           //mCount = itemView.findViewById(R.id.count_tv);

            productBuy = itemView.findViewById(R.id.products_btnbuy);
            productAddToCart = itemView.findViewById(R.id.products_btncart);

            productBuy.setOnClickListener(this);
            productAddToCart.setOnClickListener(this);

        }

        public void displaySweetAlert(){
            new SweetAlertDialog(mCtx, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .show();
        }

        @Override
        public void onClick(View v) {
          // int position = (int)v.getTag();
           position = getAdapterPosition();
            String dproductName, dprodDesc, dstoreName;
            dproductName = productDataProviderList.get(position).getProductName();
            dprodDesc = productDataProviderList.get(position).getProductDescription();
            dstoreName = productDataProviderList.get(position).getStoreName();

          switch (v.getId()) {
              case R.id.products_btnbuy: //should go directly to buy
                  //displaySweetAlert();
                  Toast.makeText(mCtx, "Buy " + productDataProviderList.get(position).getProductName(), Toast.LENGTH_SHORT).show();
                  break;

              case R.id.products_btncart:
                  //start of restrict
                  cartDbHelper = new CartDbHelper(mCtx);
                  sqLiteDatabase = cartDbHelper.getReadableDatabase();
                  cursor = cartDbHelper.getInformations(sqLiteDatabase);
                  int size = 0;

                  if (cursor.moveToFirst()) {
                      do {
                          size++;
                          final String prodName, prodDes, prodCount, prodStore;
                          prodName = cursor.getString(0);
                          prodDes = cursor.getString(1);
                          prodCount = cursor.getString(2);
                          prodStore = cursor.getString(3);

                          if (prodName.equals(dproductName) && prodDes.equals(dprodDesc)) {
                              //if product already exists
                              Toast.makeText(mCtx, dproductName + " Product already exists", Toast.LENGTH_SHORT).show();
                              int count = Integer.valueOf(prodCount);
                              count++;
                              cartDbHelper = new CartDbHelper(mCtx);
                              sqLiteDatabase = cartDbHelper.getWritableDatabase();
                              String name,des, counts;
                              name = dproductName;
                              des = dprodDesc;
                              counts = String.valueOf(count);
                              cartDbHelper.updateInformations(name, name, des, counts, prodStore, sqLiteDatabase);

                          }
                          else {
                              if (size != cursor.getCount()) {

                              } else {
                                  //if table not empty && product doesnt exist
                                  cartDbHelper = new CartDbHelper(mCtx);
                                  sqLiteDatabase = cartDbHelper.getWritableDatabase();
                                  cartDbHelper.addInformations(dproductName, dprodDesc, productCount, dstoreName, sqLiteDatabase);
                                  cartDbHelper.close();
                                  Toast.makeText(mCtx, "Item Added to cart", Toast.LENGTH_SHORT).show();
                              }
                          }
                      } while (cursor.moveToNext());
                  }

                  else{
                      //if table is empty
                      cartDbHelper = new CartDbHelper(mCtx);
                      sqLiteDatabase = cartDbHelper.getWritableDatabase();
                      cartDbHelper.addInformations(dproductName, dprodDesc, productCount, dstoreName, sqLiteDatabase);
                      cartDbHelper.close();
                      Toast.makeText(mCtx, "Item Added to cart", Toast.LENGTH_SHORT).show();
                  }
                  //end of restrict

                  break;

              default:
                  Toast.makeText(mCtx, "End of Options" , Toast.LENGTH_SHORT).show();
          }
        }
    }

    //method for searchView
    public void setFilter(ArrayList<ProductDataProvider> newList){
        productDataProviderList = new ArrayList<>();
        productDataProviderList.addAll(newList);
        notifyDataSetChanged();
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
}
