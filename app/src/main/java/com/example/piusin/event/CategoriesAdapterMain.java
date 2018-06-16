package com.example.piusin.event;

/**
 * Created by Piusin on 2/22/2018.
 */

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


//loads categories from server
public class CategoriesAdapterMain extends RecyclerView.Adapter<CategoriesAdapterMain.MyViewHolder> implements SweetAlertClass {
    private Context mCtx;
    private String categoryname, storeName;

    ArrayList<CategoriesDataProviders> data = new ArrayList<>();

    public CategoriesAdapterMain(Context mCtx, ArrayList<CategoriesDataProviders> data) {
        this.data = data;
        this.mCtx = mCtx;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category1_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CategoriesDataProviders categoriesDataProvider = data.get(position);
        holder.categoryName.setText(data.get(position).getCategoryName());
        //holder.categoryImage.setImageResource(data.get(position).getCategoryImage());
        Picasso.with(mCtx)
                .load(categoriesDataProvider.getCategoryImage())
                .into(holder.categoryImage);
        storeName = categoriesDataProvider.getStoreName();

       // Toast.makeText(mCtx, "Category " + storeName, Toast.LENGTH_SHORT).show();

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, "Am fucking with you", Toast.LENGTH_SHORT).show();
                if(IsInternetOn() != true){
                    displayNoInternetAlert();
                }
                else {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    categoryname = categoriesDataProvider.getCategoryName();
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryName", categoryname);
                    bundle.putString("storeName", storeName);
                    ProductsFragment productsFragment = new ProductsFragment();
                    productsFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, productsFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Products");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView categoryImage;
        TextView categoryName;
        CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this);
            categoryImage = itemView.findViewById(R.id.categories_Image);
            categoryName = itemView.findViewById(R.id.categories_name);
            cardview = itemView.findViewById(R.id.cardview_categories);

        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(mCtx, getAdapterPosition(), Toast.LENGTH_SHORT).show();
            /*Toast.makeText(mCtx, "You Clicked " + categoriesDataProvider.getCategoryName(),Toast.LENGTH_LONG).show();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProductsFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Products");*/
        }

    }

    //method for searchView
    public void setFilter(ArrayList<CategoriesDataProviders> newList){
        data = new ArrayList<>();
        data.addAll(newList);
        notifyDataSetChanged();
        // Toast.makeText(Countries.class, "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayNoInternetAlert() {
        new SweetAlertDialog(mCtx, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Network Problem")
                .setContentText("Looks like you are offline!! " + "\n" + "Check Internet Connection..for better application use")
                .setConfirmText("Ok!")
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
            displayNoInternetAlert();


            return false;
        }
        displayNoInternetAlert();
        return false;
    }
}

