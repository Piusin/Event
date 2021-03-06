package com.example.piusin.event;

import android.content.Context;
import android.graphics.Paint;
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

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Piusin on 1/23/2018.
 */

//for data fetched from server
public class DiscountDesAdapter extends RecyclerView.Adapter<DiscountDesAdapter.MyViewHolder>  {
    private Context mCtx;
    private double oldCost, newCost, discount;

    private ArrayList<DiscountDataProvider> data ;
    private DiscountDataProvider discountDataProvider;

    public DiscountDesAdapter(Context mCtx, ArrayList<DiscountDataProvider> data) {
        this.data = data;
        this.mCtx = mCtx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_descitems, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        discountDataProvider = data.get(position);
        Picasso.with(mCtx)
                .load(discountDataProvider.getDiscountImage())
                .into(holder.discountImage);
        holder.discountname.setText(data.get(position).getDiscountName());
        holder.discountOcost.setText("KSH " + String.valueOf(data.get(position).getOldCost()));
        holder.discountNcost.setText("KSH " + String.valueOf(data.get(position).getNewCost()));
        holder.storeName.setText(data.get(position).getStoreName());
        holder.discountDes.setText(data.get(position).getDiscountDescription());

        oldCost = data.get(position).getOldCost();
        newCost = data.get(position).getNewCost();
        discount = ((oldCost - newCost)/(oldCost)) * 100;
        DecimalFormat df = new DecimalFormat("#.##");

        holder.discountText.setText( df.format(discount) + " % Discount");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView discountImage;
        TextView discountname, discountOcost, discountNcost, discountText,storeName, discountDes;
        Button discountBuy, discountLocate;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            discountImage = itemView.findViewById(R.id.desImage);
            discountname = itemView.findViewById(R.id.desName);
            discountOcost = itemView.findViewById(R.id.desoldCost);
            discountNcost = itemView.findViewById(R.id.desnewCost);
            discountText = itemView.findViewById(R.id.desDiscount);
            storeName = itemView.findViewById(R.id.desStoreName);
            discountDes = itemView.findViewById(R.id.des);
            discountBuy = itemView.findViewById(R.id.btnDesBuy);
            discountLocate = itemView.findViewById(R.id.btnDeslocate);
            cardView = itemView.findViewById(R.id.cardview_des);
            discountBuy.setOnClickListener(this);
            discountLocate.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            double latitude, longitude, productCost;
            String storeName, productName;
            AppCompatActivity activity = (AppCompatActivity) v.getContext();

            switch (v.getId())
            {
                case R.id.btnDesBuy:
                    Toast.makeText(mCtx, "Discounted Item Clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnDeslocate:
                    longitude = Double.valueOf(discountDataProvider.getLongitude());
                    latitude = Double.valueOf(discountDataProvider.getLatitude());
                    storeName = discountDataProvider.getStoreName();
                    productCost = discountDataProvider.getNewCost();
                    productName = discountDataProvider.getDiscountName();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat",latitude);
                    bundle.putDouble("lng",longitude);
                    bundle.putString("store",storeName);
                    bundle.putString("product", productName);
                    bundle.putDouble("cost", productCost);
                    NewMapFragment newMapFragment = new NewMapFragment();
                    newMapFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, newMapFragment).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("Item Location");
                    break;
                default:
                    Toast.makeText(mCtx, "End of Options ", Toast.LENGTH_SHORT).show();
                    break;
            }


            /*AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProductsFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Products");*/
        }


    }
}
