package com.example.piusin.event;

/**
 * Created by Piusin on 4/9/2018.
 */

public class Config {

    //JSON URL
    public static final String DATA_URL = "http://10.42.0.1/Scripts/SuperMart/getCounties.php";

    //Tags used in the JSON String
    public static final String TAG_COUNTY = "county_name";

    //JSON array name
    public static final String JSON_ARRAY = "county";


    //supermatt url 10.42.0.1
    public static final String URL_PRODUCTS  = "http://10.42.0.1/Scripts/SuperMart/products.php"; //cart fragment
    public static final String URL_STORES  = "http://10.42.0.1/Scripts/SuperMart/featuredCategories1.php";
    public static final String URL_CATEGORIES  = "http://10.42.0.1/Scripts/SuperMart/storeCategories.php";
    public static final String URL_STORECAT  = "http://10.42.0.1/Scripts/SuperMart/improvedCategories.php"; //categoriesFragment
    public static final String URL_DISCOUNT  = "http://10.42.0.1/Scripts/SuperMart/discounts.php"; //discountFragment
    public static final String URL_PRODUCTSFSTORE  = "http://10.42.0.1/Scripts/SuperMart/getStores.php"; // featuredStoreFragment
    public static final String URL_NEWPRODUCTS  = "http://10.42.0.1/Scripts/SuperMart/newProducts.php"; //whatsNewDes, whatsNewFragment
    public static final String URL_GETPARSEDPRODUCTSDATA = "http://10.42.0.1/Scripts/SuperMart/sure.php";
    public static final String URL_ALGORITHMPRODUCTS = "http://10.42.0.1/Scripts/SuperMart/getAlgorithmProducts.php"; //used where smartSearchandfiltering interface is implemented
    public static final String URL_ASSINGCUSTOMER = "http://10.42.0.1/Scripts/SuperMart/assingCustomerId.php";
    public static final String URL_RESTRICT = "http://10.42.0.1/Scripts/SuperMart/restrictDoubleInsertion.php";

}
