package com.example.piusin.event;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Piusin on 2/19/2018.
 */

public class ProductDescAdapter extends RecyclerView.Adapter<ProductDescAdapter.ProductsViewHolder> implements SweetAlertClass{

    private Context mCtx;
    private List<ProductDescDataProvider> productDescDataProviderList;
    private ProductDescDataProvider productDataProvider;
    SharedPreferences sharedPreferences;

    public ProductDescAdapter(Context mCtx, List<ProductDescDataProvider> productDescDataProviderList) {
        this.mCtx = mCtx;
        this.productDescDataProviderList = productDescDataProviderList;
    }


    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_descitems, null);
        return new ProductDescAdapter.ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        productDataProvider = productDescDataProviderList.get(position);
        Picasso.with(mCtx)
                .load(productDataProvider.getProductDesImage())
                .into(holder.productDesImage);
        holder.productDesName.setText(productDataProvider.getProductDesName());
        holder.productDesCost.setText("Ksh " + String.valueOf(productDataProvider.getProductDesCost()));
        holder.productDes.setText(productDataProvider.getProductDescription());
    }

    @Override
    public int getItemCount() {
        return productDescDataProviderList.size();
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView productDesImage;
        TextView productDesName;
        TextView productDesCost;
        TextView productDes;
        Button buy, nearestMall, addToCart;


        public ProductsViewHolder(View itemView) {
            super(itemView);

            productDesImage = itemView.findViewById(R.id.prodDescImage);
            productDesName = itemView.findViewById(R.id.productdesName);
            productDesCost = itemView.findViewById(R.id.productdesCost);
            productDes = itemView.findViewById(R.id.productDescription);
            buy = itemView.findViewById(R.id.btnBuyProduct);
            nearestMall = itemView.findViewById(R.id.btnNearestmall);
            addToCart = itemView.findViewById(R.id.btnaddToBasket);

            buy.setOnClickListener(this);
            nearestMall.setOnClickListener(this);
            addToCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(IsInternetOn() != true){
                displayNoInternetAlert();
            }
            else {
                AppCompatActivity activity;
                switch (v.getId()) {
                    case R.id.btnBuyProduct:
                        activity = (AppCompatActivity) v.getContext();
                        Toast.makeText(mCtx, "Buy " + productDataProvider.getProductDesName(), Toast.LENGTH_SHORT).show();
                        activity.startActivity(new Intent(mCtx, MapsActivity2.class));

                        break;

                    case R.id.btnNearestmall:
                        Toast.makeText(mCtx, "Get Nearest Mall with " + productDataProvider.getProductDesName(), Toast.LENGTH_SHORT).show();
                        activity = (AppCompatActivity) v.getContext();
                        SharedPreferences.Editor editor = activity.getSharedPreferences("storePref", MODE_PRIVATE).edit();
                        editor.putString("store", "Khetias Crossroads");
                        editor.apply();


                        MapFragment mapFragment = new MapFragment();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_container, mapFragment).addToBackStack(null).commit();
                        break;

                    case R.id.btnaddToBasket:
                        Toast.makeText(mCtx, "Add " + productDataProvider.getProductDesName() + " to the basket", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(mCtx, "End of Options", Toast.LENGTH_SHORT).show();

                }
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

