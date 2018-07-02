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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
                        createStoreDistanceArrayList();
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
            totalCost = transportCost + (productCost * Double.valueOf(prodCount));
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
        finalOptimizedDataProviderArrayList();
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
        String productName, storeName, storeDStore;


    /*    for(i=0; i<storeDistancesDataProviderArrayList.size(); i++){
            storeDStore = storeDistancesDataProviderArrayList.get(i).getStoreName();

            for(i = 0; i< cartOptimizationDataProviderArrayList.size(); i++) {
                prodStoreName = cartOptimizationDataProviderArrayList.get(i).getStoreName();
                prodName = cartOptimizationDataProviderArrayList.get(i).getProductName();

                if (storeDStore.equals(prodStoreName)) {

                    for (int j = 0; j < cartOptimizationDataProviderArrayList.size(); j++) {
                        productName = cartOptimizationDataProviderArrayList.get(j).getProductName();
                        storeName = cartOptimizationDataProviderArrayList.get(j).getStoreName();
                    }

                }
                else{
                    Toast.makeText(context, "Store Misses a product:", Toast.LENGTH_SHORT).show();
            }

            }
        }*/


        for(i = 0; i< cartOptimizationDataProviderArrayList.size(); i++) {
            prodStoreName = cartOptimizationDataProviderArrayList.get(i).getStoreName();
            prodName = cartOptimizationDataProviderArrayList.get(i).getProductName();
           // Toast.makeText(context, "StoreName: " + prodStoreName + "\n" + " ProductName: " + prodName, Toast.LENGTH_SHORT).show();


            for (int j = 0; j < cartOptimizationDataProviderArrayList.size(); j++) {
                productName = cartOptimizationDataProviderArrayList.get(j).getProductName();
                storeName = cartOptimizationDataProviderArrayList.get(j).getStoreName();
                 //Toast.makeText(context, "StoreName: " + prodStoreName + " " + storeName + "\n" + " ProductName: " + prodName + " " + productName, Toast.LENGTH_SHORT).show();


                if ( !productName.equals(prodName) && storeName.equals(prodStoreName)) {
                    Toast.makeText(context, "Store Found: " + storeName , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}


/*for(i=0; i<storeDistancesDataProviderArrayList.size(); i++){
        Toast.makeText(context, "Store Name: " + storeDistancesDataProviderArrayList.get(i).getStoreName()
        +"\n"  + "Distance: " + storeDistancesDataProviderArrayList.get(i).getStoreDistance(), Toast.LENGTH_SHORT).show();
        }*/




