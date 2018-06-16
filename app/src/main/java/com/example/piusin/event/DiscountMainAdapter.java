package com.example.piusin.event;

import android.content.Context;
import android.graphics.Paint;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Piusin on 1/23/2018.
 */

public class DiscountMainAdapter extends RecyclerView.Adapter<DiscountMainAdapter.MyViewHolder> implements SweetAlertClass{
    private Context mCtx;

    ArrayList<DiscountMainDataProvider> data = new ArrayList<>();

    public DiscountMainAdapter(Context mCtx, ArrayList<DiscountMainDataProvider> data) {
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
        final DiscountMainDataProvider discountDataProvider = data.get(position);
        holder.discountImage.setImageResource(data.get(position).getDicountImage());
        holder.discountname.setText(data.get(position).getDiscountName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsInternetOn() != true){
                    displayNoInternetAlert();
                }
                else{
                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                Bundle bundle = new Bundle();

                bundle.putString("category", discountDataProvider.getDiscountName());
                FragmentDiscounts fragmentDiscounts = new FragmentDiscounts();
                fragmentDiscounts.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragmentDiscounts).addToBackStack(null).commit();
                activity.getSupportActionBar().setTitle("Discounts");
            }
        }
    });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView discountImage;
        TextView discountname;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            discountImage = itemView.findViewById(R.id.categories_Image);
            discountname = itemView.findViewById(R.id.categories_name);
            cardView = itemView.findViewById(R.id.cardview_category);
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
