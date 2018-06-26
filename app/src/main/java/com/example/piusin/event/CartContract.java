package com.example.piusin.event;

/**
 * Created by Piusin on 3/11/2018.
 */


public class CartContract {

    public static abstract class NewProduct{
        public static  final String PRODUCT_COUNT = "product_count";
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRODUCT_DES = "product_des";
        public static final String STORE_NAME = "store_name";
        public static final String TABLE_NAME = "carts_products";
        //public static final String TABLE_NAME = "carts_productsA";
    }

}
