package com.example.piusin.event;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Piusin on 1/23/2018.
 */

//loads category section of home Fragment(handcoded)
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> implements SweetAlertClass {
    private Context mCtx;
    private  String categoryname;
    AppCompatActivity activity;

    ArrayList<CategoriesDataProvider> data = new ArrayList<>();

    public CategoriesAdapter(Context mCtx, ArrayList<CategoriesDataProvider> data) {
        this.data = data;
        this.mCtx = mCtx;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CategoriesDataProvider categoriesDataProvider = data.get(position);
        holder.categoryName.setText(data.get(position).getCategoryName());
        holder.categoryImage.setImageResource(data.get(position).getCategoryImage());

        /*Picasso.with(mCtx)
                .load(categoriesDataProvider.getCategoryImage())
                .into(holder.categoryImage);*/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsInternetOn() != true) {
                    displayNoInternetAlert();
                }else{

                Bundle bundle = new Bundle();
                categoryname = categoriesDataProvider.getCategoryName();
                if (categoryname.equals("More")) {

                    activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new CategoriesFragment()).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Categories");
                } else {
                    // Toast.makeText(mCtx, "Open Specific", Toast.LENGTH_SHORT).show();
                    activity = (AppCompatActivity) v.getContext();
                    categoryname = categoriesDataProvider.getCategoryName();
                    bundle.putString("categoryName", categoryname);
                    bundle.putString("storeName", "cat");
                    ProductsFragment productsFragment = new ProductsFragment();
                    productsFragment.setArguments(bundle);
                    //activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProductsFragment()).addToBackStack(null).commit();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, productsFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Productss");
                }
            }

            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView categoryImage;
        TextView categoryName;
        CardView cardView;

        private MyViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this);
            categoryImage = itemView.findViewById(R.id.categories_Image);
            categoryName = itemView.findViewById(R.id.categories_name);
            cardView = itemView.findViewById(R.id.cardview_category);
        }

        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProductsFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Products");
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
    }


