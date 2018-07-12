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

import java.util.ArrayList;

/**
 * Created by Piusin on 1/17/2018.
 */
//Gets hand coded data
public class FeaturedStoreMainAdapter extends RecyclerView.Adapter<FeaturedStoreMainAdapter.MyViewHolder> implements SweetAlertClass{
    public Context mCtx;
    private String storeName,storeMore;

    private ArrayList<FeaturedStoreDataProvider> data = new ArrayList<>();

    public FeaturedStoreMainAdapter(Context mCtx, ArrayList<FeaturedStoreDataProvider> data) {
        this.data = data;
        this.mCtx = mCtx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featuredstore_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FeaturedStoreDataProvider featuredStoreDataProvider = data.get(position);

        holder.storeName.setText(data.get(position).getName());
        holder.storeOpenTime.setText(data.get(position).getOpenTime());
        holder.storeLocation.setText(data.get(position).getLocation());
        holder.storeDistance.setText(String.valueOf(data.get(position).getDistance()));
        holder.storeImage.setImageResource(data.get(position).getImageView());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsInternetOn() != true) {
                    displayNoInternetAlert();
                }else{

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                CategoriesFragment categoriesFragment = new CategoriesFragment();
                FeaturedStoreFragment featuredStoreFragment = new FeaturedStoreFragment();

                storeName = featuredStoreDataProvider.getName();
                if (storeName.equals("")) {
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, featuredStoreFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Stores");
                } else {
                    bundle.putString("storeName", storeName);
                    categoriesFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, categoriesFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Categories");

                }

            }
        }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView storeName, storeOpenTime, storeLocation, storeDistance;
        ImageView storeImage;
        CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);

           //itemView.setOnClickListener(this); //for overriden onclick functionality

            storeImage = itemView.findViewById(R.id.featuredstore_image);
            storeName = itemView.findViewById(R.id.featuredstore_name);
            storeOpenTime = itemView.findViewById(R.id.featuredstore_opentime);
            storeLocation = itemView.findViewById(R.id.featuredstore_location);
            storeDistance = itemView.findViewById(R.id.featuredstore_distance);
            cardview = itemView.findViewById(R.id.cardview_store);


            /*itemView.setOnClickListener(new View.OnClickListener() { //does not need onclick context(this)set
                @Override
                public void onClick(View v) {
                    Toast.makeText(mCtx, "jjjjj", Toast.LENGTH_SHORT).show();
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new CategoriesFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Categories");
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


