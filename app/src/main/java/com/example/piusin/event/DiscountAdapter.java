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
import android.widget.Button;
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

//for data fetched from server
public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.MyViewHolder> implements SweetAlertClass {
    private Context mCtx;
    private double oldCost, newCost, discount;

    ArrayList<DiscountDataProvider> data = new ArrayList<>();

    public DiscountAdapter(Context mCtx, ArrayList<DiscountDataProvider> data) {
        this.data = data;
        this.mCtx = mCtx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DiscountDataProvider discountDataProvider = data.get(position);
        Picasso.with(mCtx)
                .load(discountDataProvider.getDiscountImage())
                .into(holder.discountImage);
        holder.discountname.setText(data.get(position).getDiscountName());
        holder.discountOcost.setText("KSH " + String.valueOf(data.get(position).getOldCost()));
        holder.discountNcost.setText("KSH " + String.valueOf(data.get(position).getNewCost()));
        holder.storeName.setText(data.get(position).getStoreName());

        oldCost = data.get(position).getOldCost();
        newCost = data.get(position).getNewCost();
        discount = ((oldCost - newCost)/(oldCost)) * 100;
        DecimalFormat df = new DecimalFormat("#.##");

        holder.discountText.setText( df.format(discount) + " % off");


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsInternetOn() != true){
                    displayNoInternetAlert();
                }
                else {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putString("discountName", discountDataProvider.getDiscountName());
                    bundle.putString("storeName", discountDataProvider.getStoreName());
                    bundle.putString("category", discountDataProvider.getCategoryName());
                    DiscountDesFragment discountDesFragment = new DiscountDesFragment();
                    discountDesFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, discountDesFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Discount Des");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView discountImage;
        TextView discountname, discountOcost, discountNcost, discountText,storeName, discountDes;
        Button discountBuy;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            discountImage = itemView.findViewById(R.id.discount_itemImage);
            discountname = itemView.findViewById(R.id.discount_itemName);
            discountOcost = itemView.findViewById(R.id.discount_oldCost);
            discountNcost = itemView.findViewById(R.id.discount_newCost);
            discountText = itemView.findViewById(R.id.discount);
            storeName = itemView.findViewById(R.id.store_name);
            discountBuy
                    = itemView.findViewById(R.id.discount_btnbuy);
            cardView = itemView.findViewById(R.id.cardview_discounts);

            discountBuy.setOnClickListener(this);
            //cardView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            IsInternetOn();

            switch (v.getId())
            {
                case R.id.discount_btnbuy:
                    Toast.makeText(mCtx, "Discounted Item Clicked", Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        Toast.makeText(mCtx, "End of Options ", Toast.LENGTH_SHORT).show();
                        break;
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
}
