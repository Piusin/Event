package com.example.piusin.event;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.piusin.event.ManyMapsDataProvidersPackage.AllProductsDataProvider;
import com.example.piusin.event.ManyMapsDataProvidersPackage.CartCursorDataProvider;
import com.example.piusin.event.ManyMapsDataProvidersPackage.CartItemDataProvider;
import com.example.piusin.event.ManyMapsDataProvidersPackage.CartOptimizationDataProvider;
import com.example.piusin.event.MapsPackage.GetDirectionsData;
import com.example.piusin.event.MapsPackage.GetDirectionsData1;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MapFragmentMany extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    View view;
    AppCompatActivity activity;
    Context context;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private MarkerOptions mo;
    private Marker currentLocationMarker;
    private static final int LOCATION_REQUEST_CODE = 99;
    private double endLatitude, endLongitude, distanceInKms, transportCost = 10.0,totalCost, productCost, storeDistance;
    private Double storeLat, storeLng, startLatitude, startLongitude;
    private static DecimalFormat df2;
    private int i;

    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    String prodName, prodDes, prodStoreName, middle, prodCount;
    int index;

    private ArrayList<CartCursorDataProvider> cartCursorDataProviderArrayList;
    private ArrayList<StoreDistancesDataProvider> storeDistancesDataProviderArrayList;
    private ArrayList<CartOptimizationDataProvider> cartOptimizationDataProviderArrayList;
    private ArrayList<CartItemDataProvider> cartItemDataProviderArrayList;
    private ArrayList<AllProductsDataProvider> allProductsArrayList;
    private ArrayList<AllProductsDataProvider> allProductsArrayList2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        loadAlgorithmProducts();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //called when map is ready to be used
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() { //called in onmapReady

        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) { //used to store last location
        lastLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        startLatitude = location.getLatitude();
        startLongitude = location.getLongitude();
        SharedPreferences.Editor editor = activity.getSharedPreferences("piusPref", MODE_PRIVATE).edit();
        editor.putString("startLat", String.valueOf(startLatitude));
        editor.putString("startlngq", String.valueOf(startLongitude));
        editor.apply();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

        endLatitude = 0.5641064;
        endLongitude = 34.56108010000003;
        //calculateDistanceAndDuration();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) { //if permission is not granted check whether we should request for permisiion
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //for handling permission Requests such dialog boxes is permission granted or not
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else { // permission is denied
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private void calculateDistanceAndDuration() {
        Object dataTransfer[];
        String url;
        dataTransfer = new Object[3];
        url = getDirectionsUrl();
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(endLatitude, endLongitude);
        getDirectionsData.execute(dataTransfer);
    }

    //for direction API
    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + startLatitude + "," + startLongitude);
        googleDirectionsUrl.append("&destination=" + endLatitude + "," + endLongitude);
        googleDirectionsUrl.append("&key=" + "AIzaSyC1Bdiqrc7tTk0ISA3aTo-jg-AUaH1Xehs");//13
        return googleDirectionsUrl.toString();
    }

    private void createCartItemsArray(){ //trying to create non looping
        cartDbHelper = new CartDbHelper(context);
        sqLiteDatabase = cartDbHelper.getReadableDatabase();
        cursor = cartDbHelper.getInformations(sqLiteDatabase);

        cartItemDataProviderArrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                prodName = cursor.getString(0);
                prodDes = cursor.getString(1);
                prodCount = cursor.getString(2);
                prodStoreName = cursor.getString(3);
                cartItemDataProviderArrayList.add(new CartItemDataProvider(
                        prodName,
                        prodDes,
                        prodStoreName,
                        prodCount
                ));
            }
            while (cursor.moveToNext());

        }
        else{
            Toast.makeText(context, "Your Cart is Empty....", Toast.LENGTH_SHORT).show();
        }

    }

    //fetching products from server.
    private void loadAlgorithmProducts() {
        createCartItemsArray();

        cartCursorDataProviderArrayList = new ArrayList<>();

         StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_ALGORITHMPRODUCTS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);
                        for (i = 0; i < array.length(); i++) {

                            JSONObject product = array.getJSONObject(i);
                            for (int inde = 0; inde < cartItemDataProviderArrayList.size(); inde++) {
                                prodName = cartItemDataProviderArrayList.get(inde).getProductName();
                                prodDes = cartItemDataProviderArrayList.get(inde).getProductDes();
                                prodCount = cartItemDataProviderArrayList.get(inde).getProductCount();

                                if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes)) {
                                    cartCursorDataProviderArrayList.add(new CartCursorDataProvider(
                                            product.getString("product_name"),
                                            product.getString("product_description"),
                                            product.getString("product_cost"),
                                            product.getString("quantity_at_hand"),
                                            product.getString("store_name"),
                                            prodCount,
                                            product.getDouble("store_latitude"),
                                            product.getDouble("store_longitude")
                                    ));
                                }
                            }
                        }
                       // Log.e(cartCursorDataProviderArrayList.get(0))


                        for ( i = 0; i< cartCursorDataProviderArrayList.size(); i++){
                            Toast.makeText(context, "Prodname: " + cartCursorDataProviderArrayList.get(i).getProductName()
                                            + "\n" +"ProdDes: " + cartCursorDataProviderArrayList.get(i).getProductDes()
                                            + "\n" +"storeName: " + cartCursorDataProviderArrayList.get(i).getStoreName()
                                            + "\n"+"ProdCost: " + cartCursorDataProviderArrayList.get(i).getProductCost()
                                            + "\n"+"Quantity: " + cartCursorDataProviderArrayList.get(i).getQuantityAtHand()
                                            + "\n"+"Product Count: " + cartCursorDataProviderArrayList.get(i).getProductCount()
                                            + "\n"+"lat: " + cartCursorDataProviderArrayList.get(i).getStoreLatitude()
                                            + "\n"+"lng: " + cartCursorDataProviderArrayList.get(i).getStoreLongitude()
                                    , Toast.LENGTH_SHORT).show();
                        }

                        //createStoreDistanceArrayList();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            //adding our stringrequest to queue
            Volley.newRequestQueue(context).add(stringRequest);
    }



    public void createStoreDistanceArrayList() {
        storeDistancesDataProviderArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("piusPref",
                Context.MODE_PRIVATE);
        startLatitude = Double.valueOf(sharedPreferences.getString("startLat", "0.6174969"));
        startLongitude = Double.valueOf(sharedPreferences.getString("startlngq", "34.52367779999997"));
        for (i = 0; i < cartCursorDataProviderArrayList.size(); i++) {
            storeLat = cartCursorDataProviderArrayList.get(i).getStoreLatitude();
            storeLng = cartCursorDataProviderArrayList.get(i).getStoreLongitude();
            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                float[] results = new float[10];
                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                distanceInKms = results[0] / 1000;

                storeDistancesDataProviderArrayList.add(new StoreDistancesDataProvider(
                        cartCursorDataProviderArrayList.get(i).getStoreName(),
                        distanceInKms
                ));
            } else {
                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(context, "Distance:" + storeDistancesDataProviderArrayList.size(), Toast.LENGTH_SHORT).show();


        createCartOptimizationArrayList();
    }

    private void createCartOptimizationArrayList() {

        cartOptimizationDataProviderArrayList = new ArrayList<>();
        for (i = 0; i < cartCursorDataProviderArrayList.size() && i< storeDistancesDataProviderArrayList.size();  i++) {
            storeDistance = storeDistancesDataProviderArrayList.get(i).getStoreDistance();
            productCost = Double.valueOf(cartCursorDataProviderArrayList.get(i).getProductCost());
            prodCount = cartCursorDataProviderArrayList.get(i).getProductCount();
            transportCost = 0.0;
            totalCost = 0.0;
            transportCost = storeDistance * 10.0;
            totalCost = (productCost * Double.valueOf(prodCount));
            cartOptimizationDataProviderArrayList.add(new CartOptimizationDataProvider(
                    cartCursorDataProviderArrayList.get(i).getProductName(),
                    cartCursorDataProviderArrayList.get(i).getProductDes(),
                    cartCursorDataProviderArrayList.get(i).getStoreName(),
                    productCost,
                    transportCost,
                    totalCost,
                    cartCursorDataProviderArrayList.get(i).getStoreLatitude(),
                    cartCursorDataProviderArrayList.get(i).getStoreLongitude(),
                    storeDistance
            ));
        }

      //  finalOptimizedDataProviderArrayList();
       for ( i = 0; i< cartOptimizationDataProviderArrayList.size(); i++){
            Toast.makeText(context, "Prodname: " + cartOptimizationDataProviderArrayList.get(i).getProductName()
                     + "\n" +"ProdDes: " + cartOptimizationDataProviderArrayList.get(i).getProductDes()
                            + "\n" +"storeName: " + cartOptimizationDataProviderArrayList.get(i).getStoreName()
                            + "\n"+"ProdCost: " + cartOptimizationDataProviderArrayList.get(i).getProductCost()
                            + "\n"+"ProdTansport: " + cartOptimizationDataProviderArrayList.get(i).getTransportCost()
                            + "\n"+"total: " + cartOptimizationDataProviderArrayList.get(i).getTotalCost()
                            + "\n"+"lat: " + cartOptimizationDataProviderArrayList.get(i).getOstoreLatitude()
                            + "\n"+"lng: " + cartOptimizationDataProviderArrayList.get(i).getOstoreLongitude()
                    , Toast.LENGTH_SHORT).show();
        }
    }







    private void finalOptimizedDataProviderArrayList(){
        String productName1, storeName1;

        //Toast.makeText(context, "Pius: " + cartItemDataProviderArrayList.size(), Toast.LENGTH_SHORT).show();

        for(i=0; i<cartItemDataProviderArrayList.size(); i++){
            productName1 = cartItemDataProviderArrayList.get(i).getProductName();

           // Toast.makeText(context, "Name: " + productName1, Toast.LENGTH_SHORT).show();

            for(int j =0; j<storeDistancesDataProviderArrayList.size(); j++){
            storeName1 = storeDistancesDataProviderArrayList.get(j).getStoreName();

               // Toast.makeText(context, "StoreName: " + storeName1, Toast.LENGTH_SHORT).show();

                for(int x = 0; x< cartOptimizationDataProviderArrayList.size(); x++) {
                    prodStoreName = cartOptimizationDataProviderArrayList.get(x).getStoreName();
                    prodName = cartOptimizationDataProviderArrayList.get(x).getProductName();
                    
                    if(!productName1.equals(prodName) && storeName1.equals(prodStoreName)){
                        Toast.makeText(context, "Found: " + storeName1, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Remove Store: " + storeName1, Toast.LENGTH_SHORT).show();
                    }
                }

            
            }

        }

    }




}

/*
*  //for two products.
    private void hasTwoProducts(){
        String productName1, productName2, storeName1;
        productName1 = cartItemDataProviderArrayList.get(0).getProductName();
        productName2 = cartItemDataProviderArrayList.get(1).getProductName();
        allProductsArrayList = new ArrayList<>();
        allProductsArrayList2 = new ArrayList<>();
        Toast.makeText(context, "ProductName1: " + productName1 + "\n" + "Product name2: " + productName2, Toast.LENGTH_SHORT).show();

            for(i = 0; i< cartOptimizationDataProviderArrayList.size(); i++) {
                storeName1 = cartOptimizationDataProviderArrayList.get(i).getStoreName();
                prodName = cartOptimizationDataProviderArrayList.get(i).getProductName();

                if (productName1.equals(prodName)){ //dealing with specific store

                    for(int y = 0; y< cartOptimizationDataProviderArrayList.size(); y++) {
                        prodStoreName = cartOptimizationDataProviderArrayList.get(y).getStoreName();
                        prodName = cartOptimizationDataProviderArrayList.get(y).getProductName();
                        if(productName2.equals(prodName) && storeName1.equals(prodStoreName)){

                            for(int z = 0; z< cartOptimizationDataProviderArrayList.size(); z++){
                                String prod = cartOptimizationDataProviderArrayList.get(z).getProductName();
                                String store = cartOptimizationDataProviderArrayList.get(z).getStoreName();

                                if(productName1.equals(prod) && store.equals(prodStoreName)){
                                    allProductsArrayList2.add(new AllProductsDataProvider(
                                            cartOptimizationDataProviderArrayList.get(z).getProductName(),
                                            cartOptimizationDataProviderArrayList.get(z).getStoreName(),
                                            cartOptimizationDataProviderArrayList.get(z).getProductCost(),
                                            cartOptimizationDataProviderArrayList.get(z).getTransportCost(),
                                            cartOptimizationDataProviderArrayList.get(z).getTotalCost(),
                                            cartOptimizationDataProviderArrayList.get(z).getOstoreLatitude(),
                                            cartOptimizationDataProviderArrayList.get(z).getOstoreLongitude()

                                    ));
                                }
                                else if(productName2.equals(prodName) && storeName1.equals(prodStoreName)){
                                    allProductsArrayList.add(new AllProductsDataProvider(
                                            prodName,
                                            storeName1,
                                            cartOptimizationDataProviderArrayList.get(y).getProductCost(),
                                            cartOptimizationDataProviderArrayList.get(y).getTransportCost(),
                                            cartOptimizationDataProviderArrayList.get(y).getTotalCost(),
                                            cartOptimizationDataProviderArrayList.get(y).getOstoreLatitude(),
                                            cartOptimizationDataProviderArrayList.get(y).getOstoreLongitude()

                                    ));
                                }

                            }



                        }

                        else{
                            //Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                        }
                    }

                   // Toast.makeText(context, "True", Toast.LENGTH_SHORT).show();
                }else{
                   // Toast.makeText(context, "Lost", Toast.LENGTH_SHORT).show();
                }
        }
        Toast.makeText(context, "Fuck me" + allProductsArrayList.size(), Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Fuck me" + allProductsArrayList2.size(), Toast.LENGTH_SHORT).show();



   for( i=0; i< allProductsArrayList.size(); i++){
       totalCost = 0;
       prodStoreName = allProductsArrayList.get(i).getStoreName();
       for(int x = 0; x<allProductsArrayList.size(); x++){
           if(allProductsArrayList.get(x).getStoreName().equals(prodStoreName))
           totalCost = + allProductsArrayList.get(x).getTotalCost();
       }
       Toast.makeText(context, prodStoreName + " " + totalCost, Toast.LENGTH_SHORT).show();
   }

        for( i=0; i< allProductsArrayList2.size(); i++){
            totalCost = 0;
            prodStoreName = allProductsArrayList2.get(i).getStoreName();
            for(int x = 0; x<allProductsArrayList2.size(); x++){
                if(allProductsArrayList2.get(x).getStoreName().equals(prodStoreName))
                    totalCost = + allProductsArrayList2.get(x).getTotalCost();
            }
            Toast.makeText(context, prodStoreName + " " + totalCost, Toast.LENGTH_SHORT).show();
        }

    }




    //for three products.
    private void hasThreeProducts(){
        String productName1, productName2, productName3, storeName1;
        productName1 = cartItemDataProviderArrayList.get(0).getProductName();
        productName2 = cartItemDataProviderArrayList.get(1).getProductName();
        productName3 = cartItemDataProviderArrayList.get(2).getProductName();

        for(i = 0; i< cartOptimizationDataProviderArrayList.size(); i++) {
            storeName1 = cartOptimizationDataProviderArrayList.get(i).getStoreName();
            prodName = cartOptimizationDataProviderArrayList.get(i).getProductName();

            if (productName1.equals(prodName)){ //dealing with specific store

                for(int y = 0; y< cartOptimizationDataProviderArrayList.size(); y++) {
                    prodStoreName = cartOptimizationDataProviderArrayList.get(y).getStoreName();
                    prodName = cartOptimizationDataProviderArrayList.get(y).getProductName();
                    if(productName2.equals(prodName) && storeName1.equals(prodStoreName)){
                        //Toast.makeText(context, "Made it: " + storeName1, Toast.LENGTH_SHORT).show();

                        for(int x = 0; x< cartOptimizationDataProviderArrayList.size(); x++) {
                            prodStoreName = cartOptimizationDataProviderArrayList.get(x).getStoreName();
                            prodName = cartOptimizationDataProviderArrayList.get(x).getProductName();
                            if(productName3.equals(prodName) && storeName1.equals(prodStoreName)){
                                Toast.makeText(context, "Made it : " + storeName1, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else{
                        Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                    }
                }

                Toast.makeText(context, "True", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Lost", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //for four products.
    private void hasFourProducts(){
        String productName1, productName2, productName3, productName4, storeName1;
        productName1 = cartItemDataProviderArrayList.get(0).getProductName();
        productName2 = cartItemDataProviderArrayList.get(1).getProductName();
        productName3 = cartItemDataProviderArrayList.get(2).getProductName();
        productName4 = cartItemDataProviderArrayList.get(3).getProductName();

        for(i = 0; i< cartOptimizationDataProviderArrayList.size(); i++) {
            storeName1 = cartOptimizationDataProviderArrayList.get(i).getStoreName();
            prodName = cartOptimizationDataProviderArrayList.get(i).getProductName();

            if (productName1.equals(prodName)){ //dealing with specific store

                for(int y = 0; y< cartOptimizationDataProviderArrayList.size(); y++) {
                    prodStoreName = cartOptimizationDataProviderArrayList.get(y).getStoreName();
                    prodName = cartOptimizationDataProviderArrayList.get(y).getProductName();
                    if(productName2.equals(prodName) && storeName1.equals(prodStoreName)){
                        //Toast.makeText(context, "Made it: " + storeName1, Toast.LENGTH_SHORT).show();

                        for(int x = 0; x< cartOptimizationDataProviderArrayList.size(); x++) {
                            prodStoreName = cartOptimizationDataProviderArrayList.get(x).getStoreName();
                            prodName = cartOptimizationDataProviderArrayList.get(x).getProductName();
                            if(productName3.equals(prodName) && storeName1.equals(prodStoreName)){
                                //Toast.makeText(context, "Made it : " + storeName1, Toast.LENGTH_SHORT).show();
                                for(int z = 0; z< cartOptimizationDataProviderArrayList.size(); z++) {
                                    prodStoreName = cartOptimizationDataProviderArrayList.get(z).getStoreName();
                                    prodName = cartOptimizationDataProviderArrayList.get(z).getProductName();
                                    if(productName4.equals(prodName) && storeName1.equals(prodStoreName)){
                                        Toast.makeText(context, "Made it : " + storeName1, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else{
                        Toast.makeText(context, "Lost it", Toast.LENGTH_SHORT).show();
                    }
                }

                Toast.makeText(context, "True", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Lost", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

