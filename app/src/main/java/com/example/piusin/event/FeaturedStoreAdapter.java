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
 * Created by Piusin on 3/1/2018.
 */


//Gets data from server
public class FeaturedStoreAdapter extends RecyclerView.Adapter<FeaturedStoreAdapter.FeaturedStoreViewHolder> implements SweetAlertClass{

    private Context mCtx;
    private ArrayList<FeaturedStoreMainDataProvider> featuredStoreDataProviderList;
    private String storeName;

    //constructor
    public FeaturedStoreAdapter(Context mCtx, ArrayList<FeaturedStoreMainDataProvider> featuredStoreDataProviderList) {
        this.mCtx = mCtx;
        this.featuredStoreDataProviderList = featuredStoreDataProviderList;
    }

    @Override
    public FeaturedStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.store_item, null);
        return new FeaturedStoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeaturedStoreViewHolder holder, final int position) {
        final FeaturedStoreMainDataProvider featuredStoreDataProvider = featuredStoreDataProviderList.get(position);
        Picasso.with(mCtx)
                .load(featuredStoreDataProvider.getStoreImage())
                .into(holder.storeImage);

        holder.storeName.setText("Name: " + featuredStoreDataProvider.getStoreName());
        holder.openTime.setText("Open: " + featuredStoreDataProvider.getStoreOpenTime() + "AM");
        holder.closeTime.setText("Close: " + featuredStoreDataProvider.getStoreCloseTime() + "PM");
        holder.location.setText("Located: " + featuredStoreDataProvider.getStoreLocation());
        holder.distance.setText("Distance: "+ String.valueOf(featuredStoreDataProvider.getStoreDistance()) + "KMs");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(IsInternetOn() != true){ //screen error and remain at same page
                    displayNoInternetAlert();
                }
                else { //open required page
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Bundle bundle = new Bundle();
                    storeName = featuredStoreDataProvider.getStoreName();

                    bundle.putString("storeName", storeName);
                    categoriesFragment.setArguments(bundle);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, categoriesFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Categories");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return featuredStoreDataProviderList.size();
    }

    class FeaturedStoreViewHolder extends RecyclerView.ViewHolder {
        ImageView storeImage;
        TextView storeName, openTime, location, distance,closeTime;
        CardView cardView;

        public FeaturedStoreViewHolder(View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.store_name);
            openTime = itemView.findViewById(R.id.store_opentime);
            location = itemView.findViewById(R.id.store_location);
            distance = itemView.findViewById(R.id.store_distance);
            storeImage = itemView.findViewById(R.id.store_image);
            closeTime = itemView.findViewById(R.id.store_closetime);
            cardView =itemView.findViewById(R.id.cardview_store);
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
