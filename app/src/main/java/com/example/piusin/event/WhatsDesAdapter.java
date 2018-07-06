package com.example.piusin.event;

import android.content.Context;
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

import java.util.List;

/**
 * Created by Piusin on 4/2/2018.
 */
public class WhatsDesAdapter extends RecyclerView.Adapter<WhatsDesAdapter.WhatsNewViewHolder> {
    private Context mCtx;
    private List<WhatsNewMainDataProvider> whatsNewDataProviderList;
    String categoryName;
    WhatsNewMainDataProvider whatDataProvider;

    public WhatsDesAdapter(Context mCtx, List<WhatsNewMainDataProvider> whatsNewDataProviderList) {
        this.mCtx = mCtx;
        this.whatsNewDataProviderList = whatsNewDataProviderList;
    }

    @Override
    public WhatsDesAdapter.WhatsNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.whatsnew_descitems, null);
        return new WhatsDesAdapter.WhatsNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WhatsDesAdapter.WhatsNewViewHolder holder, int position){
        whatDataProvider = whatsNewDataProviderList.get(position);
        Picasso.with(mCtx)
                .load(whatDataProvider.getProductImage())
                .into(holder.productImage);
        holder.productName.setText(whatDataProvider.getProductName() + " going at ");
        holder.productPrice.setText("Ksh " + String.valueOf(whatDataProvider.getProductPrice()) + " in ");
        holder.productStore.setText(whatDataProvider.getStoreName() + " " + whatDataProvider.getStoreLocation());
        holder.productDes.setText(whatDataProvider.getProductDescription());
    }

    @Override
    public int getItemCount() {
        return whatsNewDataProviderList.size();
    }

    class WhatsNewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView productImage;
        TextView productName, productPrice, productStore, productDes;
        CardView cardView;
        Button productBuy, productLocate;

        public WhatsNewViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.desImage);
            productName = itemView.findViewById(R.id.desName);
            productPrice = itemView.findViewById(R.id.desCost);
            productStore = itemView.findViewById(R.id.desStore);
            productDes = itemView.findViewById(R.id.whatsnew_des);
            cardView = itemView.findViewById(R.id.cardview_dess);
            productBuy = itemView.findViewById(R.id.btnNewB);
            productLocate = itemView.findViewById(R.id.btnNewLocate);
            productBuy.setOnClickListener(this);
            productLocate.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            double latitude, longitude, productCost;
            String storeName, productName;
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            switch (v.getId()) {
                case R.id.btnNewB:
                    Toast.makeText(mCtx, "Buy " + whatDataProvider.getProductName(), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btnNewLocate:
                    longitude = Double.valueOf(whatDataProvider.getLongitude());
                    latitude = Double.valueOf(whatDataProvider.getLatitude());
                    storeName = whatDataProvider.getStoreName();
                    productCost = whatDataProvider.getProductPrice();
                    productName = whatDataProvider.getProductName();
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
                    Toast.makeText(mCtx, "End of Options", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
