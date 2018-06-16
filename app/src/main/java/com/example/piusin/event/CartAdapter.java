package com.example.piusin.event;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
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

import java.util.List;



/**
 * Created by Piusin on 3/3/2018.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartsViewHolder> implements SweetAlertClass{

    private Context mCtx;
    private List<ProductDataProvider> productDataProviderList;
    ProductDataProvider productDataProvider;
    int count;

    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    String productName;

    public CartAdapter(Context mCtx, List<ProductDataProvider> productDataProviderList) {
        this.mCtx = mCtx;
        this.productDataProviderList = productDataProviderList;
    }

    @Override
    public CartsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_items, null);
        return new CartsViewHolder(view);
    }


    public void fetchCount() {
        cartDbHelper = new CartDbHelper(mCtx);
        sqLiteDatabase = cartDbHelper.getReadableDatabase();
        cursor = cartDbHelper.getInformations(sqLiteDatabase);

        if (cursor.moveToFirst()) {
            do {
                final String prodCount, prodName,prodDes;
                prodName = cursor.getString(0);
                prodDes = cursor.getString(1);
                prodCount = cursor.getString(2);
                if(prodName.equals(productDataProvider.getProductName()) && prodDes.equals(productDataProvider.getProductDescription())) {
                    count = Integer.valueOf(prodCount);
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onBindViewHolder(CartsViewHolder holder, int position) {

        productDataProvider = productDataProviderList.get(position);

        Picasso.with(mCtx)
                .load(productDataProvider.getProductImage())
                .into(holder.cartItemImage);
        holder.cartItemName.setText(productDataProvider.getProductName());
        holder.cartItemPrice.setText("Ksh " + String.valueOf(productDataProvider.getProductPrice()));
        holder.cartItemDesc.setText(productDataProvider.getProductDescription());
        fetchCount();
        holder.cartItemCount.setText(String.valueOf(count));
        productName = productDataProvider.getProductName();

    }


    @Override
    public int getItemCount() {
        return productDataProviderList.size();
    }

    class CartsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView cartItemImage;
        TextView cartItemName, cartItemPrice, cartItemDesc, cartItemCount;
        Button btnCartAdd, btnCartSubtract;
        ImageView itemDelete;

        public CartsViewHolder(View itemView) {
            super(itemView);

            cartItemImage = itemView.findViewById(R.id.cart_itemimage);
            cartItemName = itemView.findViewById(R.id.cart_itemname);
            cartItemPrice = itemView.findViewById(R.id.cart_itemcost);
            cartItemDesc = itemView.findViewById(R.id.cart_itemdesc);
            cartItemCount = itemView.findViewById(R.id.cart_itemcount);
            btnCartAdd = itemView.findViewById(R.id.btncart_add);
            btnCartSubtract = itemView.findViewById(R.id.btncart_subtract);
            itemDelete = itemView.findViewById(R.id.cart_itemdelete);

            btnCartSubtract.setOnClickListener(this);
            btnCartAdd.setOnClickListener(this);
            itemDelete.setOnClickListener(this);

        }

        public void restrict(){ //makes sure value is >0
            if(count<0){
                Toast.makeText(mCtx, "Item cant be negative", Toast.LENGTH_SHORT).show();
                count = 0;
            }
            else
                cartItemCount.setText(String.valueOf(count));
        }

        public void displayAlert(){
            new SweetAlertDialog(mCtx, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Cart Item")
                    .setContentText("Are You Sure? ")
                    .setConfirmText("Yes,delete it!")
                    .setCancelText("No,cancel plx!")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                           sDialog.dismissWithAnimation();
                            callInDelete();
                        }
                    })
                    .show();
        }

        public void successAlert(){
            new SweetAlertDialog(mCtx, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Cart Item")
                    .setContentText("Are You Sure? ")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog
                                    .setTitleText("Deleted!")
                                    .setContentText("Cart Item Has Been Deleted")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();

        }

        public void callInDelete(){
            cartDbHelper = new CartDbHelper(mCtx);
            sqLiteDatabase = cartDbHelper.getReadableDatabase();
            cartDbHelper.deleteInformation(productName, sqLiteDatabase);
            openCart();
        }

        public void updateCount() {
            cartDbHelper = new CartDbHelper(mCtx);
            sqLiteDatabase = cartDbHelper.getReadableDatabase();
            cursor = cartDbHelper.getInformations(sqLiteDatabase);

            if (cursor.moveToFirst()) {
                do {
                    final String prodName, prodDes;
                    prodName = cursor.getString(0);
                    prodDes = cursor.getString(1);

                    if (prodName.equals(productDataProvider.getProductName()) && prodDes.equals(productDataProvider.getProductDescription())) {
                        cartDbHelper = new CartDbHelper(mCtx);
                        sqLiteDatabase = cartDbHelper.getWritableDatabase();

                        String name, des, counts;
                        name = productDataProvider.getProductName();
                        des = productDataProvider.getProductDescription();
                        counts = String.valueOf(count);
                        int countd = cartDbHelper.updateInformations(name, name, des, counts, sqLiteDatabase);
                        cartItemCount.setText(counts);
                    }

                } while (cursor.moveToNext());

            }
        }

        public void openCart(){
            AppCompatActivity activity = (AppCompatActivity) mCtx;
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new CartFragment()).addToBackStack(null).commit();
            activity.getSupportActionBar().setTitle("Cart");
        }

        @Override
        public void onClick(View v) {
            if (IsInternetOn() != true) {
                displayNoInternetAlert();
            } else {
                switch (v.getId()) {
                    case R.id.btncart_add:
                        count++;
                        updateCount();
                        openCart();
                        break;

                    case R.id.btncart_subtract:
                        count--;
                        restrict();
                        updateCount();
                        openCart();
                        break;

                    case R.id.cart_itemdelete:
                        displayAlert();
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
