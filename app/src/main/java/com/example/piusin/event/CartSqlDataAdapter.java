package com.example.piusin.event;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piusin on 3/11/2018.
 */

public class CartSqlDataAdapter extends ArrayAdapter {
    List list = new ArrayList();
    private String prodName, prodDes;

    public CartSqlDataAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler{
       TextView ProductNAME,ProductDES;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        LayoutHandler layoutHandler;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.cart_items, parent, false);
            layoutHandler = new LayoutHandler();
            layoutHandler.ProductNAME = row.findViewById(R.id.cart_itemname);
            layoutHandler.ProductDES = row.findViewById(R.id.cart_itemdesc);
            row.setTag(layoutHandler);

        }
        else{
            layoutHandler = (LayoutHandler) row.getTag();
        }

        CartDataProvider dataProvider = (CartDataProvider) this.getItem(position);
        layoutHandler.ProductNAME.setText(dataProvider.getProductName());
        layoutHandler.ProductDES.setText(dataProvider.getProductDes());
         prodName = dataProvider.getProductName();
         prodDes = dataProvider.getProductDes();

        return row;
    }
}

