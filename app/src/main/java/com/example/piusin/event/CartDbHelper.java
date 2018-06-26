package com.example.piusin.event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Piusin on 3/11/2018.
 */

public class CartDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CART_PRODUCT.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY =
            "CREATE TABLE "+ CartContract.NewProduct.TABLE_NAME+"("+ CartContract.NewProduct.PRODUCT_NAME+" TEXT,"+
                    CartContract.NewProduct.PRODUCT_DES+" TEXT,"+ CartContract.NewProduct.PRODUCT_COUNT+" TEXT, "+ CartContract.NewProduct.STORE_NAME+" TEXT);";
    private static final String UPDATE_QUERY =
            "ALTER TABLE CartContract.NewProduct.TABLE_NAME ADD COLUMN CartContract.NewProduct.STORE_NAME TEXT";

    public CartDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("Database Operations", "Database Created / Opened ...");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //called if table doesnt exist
        db.execSQL(CREATE_QUERY);
        //db.execSQL("DROP TABLE IF EXISTS " + CartContract.NewProduct.TABLE_NAME + ";");
        Log.e("Database Operations", "Table Created ...");
    }

    //method for data Insertion
    public void addInformations(String productName, String productDes, String productCount, String storeName, SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CartContract.NewProduct.PRODUCT_NAME, productName);
        contentValues.put(CartContract.NewProduct.PRODUCT_DES, productDes);
        contentValues.put(CartContract.NewProduct.PRODUCT_COUNT, productCount);
        contentValues.put(CartContract.NewProduct.STORE_NAME, storeName);
        db.insert(CartContract.NewProduct.TABLE_NAME, null, contentValues);
        Log.e("Database Operations", "One Row Inserted ...");
    }

  //method for data retrival
    public Cursor getInformations(SQLiteDatabase db){
        Cursor cursor;
        String [] projections = {CartContract.NewProduct.PRODUCT_NAME, CartContract.NewProduct.PRODUCT_DES, CartContract.NewProduct.PRODUCT_COUNT, CartContract.NewProduct.STORE_NAME};
        cursor = db.query(CartContract.NewProduct.TABLE_NAME, projections, null, null, null, null, null);
        return cursor;
    }

    //method to delete data
    public void deleteInformation(String productName, SQLiteDatabase sqLiteDatabase){
        String selection = CartContract.NewProduct.PRODUCT_NAME+" LIKE ?";
        String[] selection_args = {productName};
        sqLiteDatabase.delete(CartContract.NewProduct.TABLE_NAME, selection, selection_args);
    }

    //method to update data
    public int updateInformations(String product_name, String new_name, String new_des, String new_count, String new_store, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(CartContract.NewProduct.PRODUCT_NAME, new_name);
        contentValues.put(CartContract.NewProduct.PRODUCT_DES, new_des);
        contentValues.put(CartContract.NewProduct.PRODUCT_COUNT, new_count);
        contentValues.put(CartContract.NewProduct.STORE_NAME, new_store);

        String selection = CartContract.NewProduct.PRODUCT_NAME + " LIKE ?";
        String [] selection_args = {product_name};
        int count =  sqLiteDatabase.update(CartContract.NewProduct.TABLE_NAME, contentValues, selection, selection_args);
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

