package com.example.piusin.event;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piusin on 3/30/2018.
 */

//for data from server using whatsnewmaindataprovider
public class WhatsNewMainAdapter  extends RecyclerView.Adapter<WhatsNewMainAdapter.WhatsNewViewHolder> {
    private Context mCtx;
    private List<WhatsNewMainDataProvider> productDataProviderList;
    String categoryName;
    WhatsNewMainDataProvider productDataProvider;
    String productNames, productDes;

    public WhatsNewMainAdapter(Context mCtx, List<WhatsNewMainDataProvider> productDataProviderList) {
        this.mCtx = mCtx;
        this.productDataProviderList = productDataProviderList;
    }

    @Override
    public WhatsNewMainAdapter.WhatsNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.whatsnew_items, null);
        return new WhatsNewMainAdapter.WhatsNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WhatsNewMainAdapter.WhatsNewViewHolder holder, int position){
        productDataProvider = productDataProviderList.get(position);

        Picasso.with(mCtx)
                .load(productDataProvider.getProductImage())
                .into(holder.productImage);
        holder.productName.setText(productDataProvider.getProductName());
        holder.productPrice.setText("Ksh " + String.valueOf(productDataProvider.getProductPrice()));
        holder.productStore.setText(productDataProvider.getStoreName());
    }



    @Override
    public int getItemCount() {
        return productDataProviderList.size();
    }

    class WhatsNewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView productImage;
        TextView productName, productPrice, productStore, productDes;
        CardView cardView;

        Button productBuy;

        public WhatsNewViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.whatsnew_Image);
            productName = itemView.findViewById(R.id.whatsnew_Name);
            productPrice = itemView.findViewById(R.id.whatsnew_Cost);
            productStore = itemView.findViewById(R.id.whatsnew_store);
            cardView = itemView.findViewById(R.id.cardview_whatsnew);

            productBuy = itemView.findViewById(R.id.whatsnew_btnbuy);
            productBuy.setOnClickListener(this);
            cardView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            switch (v.getId()) {
                case R.id.whatsnew_btnbuy:
                    //This is from a specific store meaning purchase is customerized
                    Toast.makeText(mCtx, "Buy " + productDataProvider.getProductName(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.cardview_whatsnew:
                   // Toast.makeText(mCtx, "CardView Clicked", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("productName", productDataProvider.getProductName());
                    bundle.putString("productStore", productDataProvider.getStoreName());
                    WhatsNewDes whatsNewDes = new WhatsNewDes();
                    whatsNewDes.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, whatsNewDes).addToBackStack(null).commit();
                    activity.getSupportActionBar().setTitle("New Item Des");
                    break;

                default:
                    Toast.makeText(mCtx, "End of Options" , Toast.LENGTH_SHORT).show();
            }
        }
    }

}
