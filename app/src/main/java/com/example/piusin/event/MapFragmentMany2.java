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
import com.example.piusin.event.ManyMapsDataProvidersPackage.MapStoreDataProvider;
import com.example.piusin.event.MapsPackage.CustomInfoWindowAdapter;
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

public class MapFragmentMany2 extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
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
    private double endLatitude, endLongitude, distanceInKms, transportCost , costPerKm = 10.0
            ,totalCost, productCost, storeDistance, finalCost=0, finalProdCost=0, fProdCount=1;
    private Double storeLat, storeLng, startLatitude, startLongitude;
    private static DecimalFormat df2;
    private int i;

    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;
    String prodName, prodDes, prodStoreName, middle, prodCount;
    String [] stores = {"Naivas Bandap", "Tesia Frontier", "Tesia Supermarket", "Khetias Centre-point", "Khetias Crossroads", "Naivas Supermarket",
                          "Nakumatt Supermarket", "Tesia Junction"};
    double tcNaivasBandap=0 , tcTesiaFrontier=0, tcTesiaSupermarket=0, tcKhetiasCPoint=0, tcKhetiasCRoads=0, tcNaivasSupermarket=0,
                             tcNakumattSupermarket=0, tcTesiaJunction=0;
    int cartSize;

    private ArrayList<CartItemDataProvider> cartItemDataProviderArrayList;
    private ArrayList<MapStoreDataProvider> optimizednaivasBandap, optimizedtesiaFrontier, optimizedtesiaSupermarket, optimizedkhetiasCentrepoint, optimizedKhetiascrossroads, optimizedNaivas, optimizedNakumatt, optimizedTesiaJunstion;
    private ArrayList<CartCursorDataProvider> naivasBandap, tesiaFrontier,tesiaSupermarket, khetiasCentrePoint, khetiasCrossroads, naivasSupermarket, nakumattSupermarket, tesiaJunction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        optimizednaivasBandap = new ArrayList<>();
        optimizedtesiaFrontier = new ArrayList<>();
        optimizedtesiaSupermarket = new ArrayList<>();
        optimizedkhetiasCentrepoint = new ArrayList<>();
        optimizedKhetiascrossroads = new ArrayList<>();
        optimizedNakumatt = new ArrayList<>();

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
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.snippet("To " + prodStoreName +  "\n" + "Products Cost: " + finalProdCost +
                "\n" + "Transport Costs: " + transportCost
                +"\n" + "Total Costs: " + finalCost);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
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
        naivasBandap = new ArrayList<>();
        tesiaFrontier = new ArrayList<>();
        tesiaSupermarket = new ArrayList<>();
        khetiasCentrePoint = new ArrayList<>();
        khetiasCrossroads = new ArrayList<>();
        naivasSupermarket = new ArrayList<>();
        nakumattSupermarket = new ArrayList<>();
        tesiaJunction = new ArrayList<>();

        cartSize = cartItemDataProviderArrayList.size();

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

                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[0])) {
                                naivasBandap.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            } //end naivas Bandap

                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[1])) {
                                tesiaFrontier.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            }//end tesia frontier

                             if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[2])) {
                                tesiaSupermarket.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            } //end tesia supermarket

                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[3])) {
                                khetiasCentrePoint.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            } // end khetias centre point


                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[4])) {
                                khetiasCrossroads.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            }//end khetias crossroads


                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[5])) {
                                naivasSupermarket.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            } //end naivas supermarket


                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[6])) {
                                nakumattSupermarket.add(new CartCursorDataProvider(
                                        product.getString("product_name"),
                                        product.getString("product_description"),
                                        product.getString("product_cost"),
                                        product.getString("quantity_at_hand"),
                                        product.getString("store_name"),
                                        prodCount,
                                        product.getDouble("store_latitude"),
                                        product.getDouble("store_longitude")
                                ));
                            } // end nakumatt supermarket


                            if (product.getString("product_name").equals(prodName) && product.getString("product_description").equals(prodDes) &&
                                    product.getString("store_name").equals(stores[7])) {
                                tesiaJunction.add(new CartCursorDataProvider(
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

                    SharedPreferences sharedPreferences = context.getSharedPreferences("piusPref",
                            Context.MODE_PRIVATE);
                    startLatitude = Double.valueOf(sharedPreferences.getString("startLat", "0.6174969"));
                    startLongitude = Double.valueOf(sharedPreferences.getString("startlngq", "34.52367779999997"));


                    //getstoreWith all products
                    if(naivasBandap.size() == cartSize){
                     //   optimizednaivasBandap = new ArrayList<>();
                        for(i=0; i<naivasBandap.size(); i++){
                            storeLat = naivasBandap.get(i).getStoreLatitude();
                            storeLng = naivasBandap.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(naivasBandap.get(i).getProductCount());
                            productCost = Double.valueOf(naivasBandap.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);
                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizednaivasBandap.add(new MapStoreDataProvider(
                                    naivasBandap.get(i).getProductName(),
                                    stores[0],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    }//end of naivas bandap


                    if(tesiaFrontier.size() == cartSize){ //start of tesia  frontier
                        //optimizedtesiaFrontier = new ArrayList<>();
                        for(i=0; i<tesiaFrontier.size(); i++){
                            storeLat = tesiaFrontier.get(i).getStoreLatitude();
                            storeLng = tesiaFrontier.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(tesiaFrontier.get(i).getProductCount());
                            productCost = Double.valueOf(tesiaFrontier.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);
                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizedtesiaFrontier.add(new MapStoreDataProvider(
                                    tesiaFrontier.get(i).getProductName(),
                                    stores[1],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    }//end of tesia Frontier


                    if(tesiaSupermarket.size() == cartSize) //start of tesia supermarket
                    {
                        //optimizedtesiaSupermarket = new ArrayList<>();
                        for(i=0; i<tesiaSupermarket.size(); i++){
                            storeLat = tesiaSupermarket.get(i).getStoreLatitude();
                            storeLng = tesiaSupermarket.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(tesiaSupermarket.get(i).getProductCount());
                            productCost = Double.valueOf(tesiaSupermarket.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);

                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizedtesiaSupermarket.add(new MapStoreDataProvider(
                                    tesiaSupermarket.get(i).getProductName(),
                                    stores[2],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    } //end of tesiaSupermarket



                    if(khetiasCentrePoint.size() == cartSize) //start of tesia supermarket
                    {
                        //optimizedkhetiasCentrepoint = new ArrayList<>();
                        for(i=0; i<khetiasCentrePoint.size(); i++){
                            storeLat = khetiasCentrePoint.get(i).getStoreLatitude();
                            storeLng = khetiasCentrePoint.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(khetiasCentrePoint.get(i).getProductCount());
                            productCost = Double.valueOf(khetiasCentrePoint.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);

                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizedkhetiasCentrepoint.add(new MapStoreDataProvider(
                                    khetiasCentrePoint.get(i).getProductName(),
                                    stores[3],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    }// end of khetiascentre point



                    if(khetiasCrossroads.size() == cartSize) //start of khetias crossroads
                    {
                        //optimizedKhetiascrossroads = new ArrayList<>();
                        for(i=0; i<khetiasCrossroads.size(); i++){
                            storeLat = khetiasCrossroads.get(i).getStoreLatitude();
                            storeLng = khetiasCrossroads.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(khetiasCrossroads.get(i).getProductCount());
                            productCost = Double.valueOf(khetiasCrossroads.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);

                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizedKhetiascrossroads.add(new MapStoreDataProvider(
                                    khetiasCrossroads.get(i).getProductName(),
                                    stores[4],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    }// end of khetiascrossroads


                    if(nakumattSupermarket.size() == cartSize) //start of nakumatt supermarket
                    {
                       // optimizedNakumatt = new ArrayList<>();
                        for(i=0; i<nakumattSupermarket.size(); i++){
                            storeLat = nakumattSupermarket.get(i).getStoreLatitude();
                            storeLng = nakumattSupermarket.get(i).getStoreLongitude();
                            fProdCount = Double.valueOf(nakumattSupermarket.get(i).getProductCount());
                            productCost = Double.valueOf(nakumattSupermarket.get(i).getProductCost());
                            if (startLatitude != null && startLongitude != null && storeLat != null && storeLng != null) {
                                float[] results = new float[10];
                                Location.distanceBetween(startLatitude, startLongitude, storeLat, storeLng, results);
                                distanceInKms = results[0] / 1000;
                                transportCost = 0.0;
                                transportCost = Math.round(distanceInKms * costPerKm);

                            } else {
                                Toast.makeText(context, "Cordinates Empty", Toast.LENGTH_SHORT).show();
                            }

                            totalCost = 0.0;
                            totalCost = + ((productCost*fProdCount) + transportCost);

                            optimizedNakumatt.add(new MapStoreDataProvider(
                                    nakumattSupermarket.get(i).getProductName(),
                                    stores[6],
                                    totalCost,
                                    transportCost,
                                    storeLat,
                                    storeLng
                            ));
                        }
                    }// end of nakumatt supermarket





                    createfinalOptimizedArrayList();
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



    private void createfinalOptimizedArrayList(){


        for(i =0; i<optimizednaivasBandap.size(); i++){
            tcNaivasBandap = tcNaivasBandap + optimizednaivasBandap.get(i).getTotalCost();
        }
        for(i =0; i<optimizedtesiaFrontier.size(); i++){
            tcTesiaFrontier = tcTesiaFrontier + optimizedtesiaFrontier.get(i).getTotalCost();
        }
        for(i =0; i<optimizedtesiaSupermarket.size(); i++){
            tcTesiaSupermarket = tcTesiaSupermarket + optimizedtesiaSupermarket.get(i).getTotalCost();
        }
        for(i =0; i<optimizedkhetiasCentrepoint.size(); i++){
            tcKhetiasCPoint = tcKhetiasCPoint + optimizedkhetiasCentrepoint.get(i).getTotalCost();
        }
        for(i =0; i<optimizedKhetiascrossroads.size(); i++){
            tcKhetiasCRoads = tcKhetiasCRoads + optimizedKhetiascrossroads.get(i).getTotalCost();
        }
        for(i =0; i<optimizedNakumatt.size(); i++){
            tcNakumattSupermarket = tcNakumattSupermarket + optimizedNakumatt.get(i).getTotalCost();
        }

        if(tcNaivasBandap > 0 && tcNaivasBandap <= tcTesiaFrontier && tcNaivasBandap<= tcTesiaSupermarket && tcNaivasBandap<= tcKhetiasCPoint && tcNaivasBandap<= tcKhetiasCRoads && tcNaivasBandap<= tcNakumattSupermarket){
            //returm Naivas Bandap
            endLatitude = optimizednaivasBandap.get(0).getLatitude();
            endLongitude = optimizednaivasBandap.get(0).getLongitude();
            prodStoreName = optimizednaivasBandap.get(0).getStoreName();
            finalCost = tcNaivasBandap;
            transportCost = (optimizednaivasBandap.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();
        }

        if(tcTesiaFrontier > 0 && tcTesiaFrontier <= tcNaivasBandap && tcTesiaFrontier<= tcTesiaSupermarket && tcTesiaFrontier<= tcKhetiasCPoint && tcTesiaFrontier<= tcKhetiasCRoads && tcTesiaFrontier<= tcNakumattSupermarket){
            //returm Tesia Frontier
            endLatitude = optimizedtesiaFrontier.get(0).getLatitude();
            endLongitude = optimizedtesiaFrontier.get(0).getLongitude();
            prodStoreName = optimizedtesiaFrontier.get(0).getStoreName();
            finalCost = tcTesiaFrontier;
            transportCost = (optimizedtesiaFrontier.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();
        }// end of tesiaFrontie


        if(tcTesiaSupermarket > 0 && tcTesiaSupermarket <= tcNaivasBandap && tcTesiaSupermarket<= tcTesiaFrontier && tcNaivasBandap<= tcKhetiasCPoint && tcNaivasBandap<= tcKhetiasCRoads && tcNaivasBandap<= tcNakumattSupermarket){
            //returm Tesia Supermartket
            endLatitude = optimizedtesiaSupermarket.get(0).getLatitude();
            endLongitude = optimizedtesiaSupermarket.get(0).getLongitude();
            prodStoreName = optimizedtesiaSupermarket.get(0).getStoreName();
            finalCost = tcTesiaSupermarket;
            transportCost = (optimizedtesiaSupermarket.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();

        }//end of tesia Supermarket

        if(tcKhetiasCPoint > 0 && tcKhetiasCPoint<= tcTesiaSupermarket && tcKhetiasCPoint <= tcNaivasBandap && tcKhetiasCPoint<= tcTesiaFrontier && tcKhetiasCPoint<= tcKhetiasCRoads && tcKhetiasCPoint<= tcNakumattSupermarket){
            //returm Khetias Cpoint
            endLatitude = optimizedkhetiasCentrepoint.get(0).getLatitude();
            endLongitude = optimizedkhetiasCentrepoint.get(0).getLongitude();
            prodStoreName = optimizedkhetiasCentrepoint.get(0).getStoreName();
            finalCost = tcKhetiasCPoint;
            transportCost = (optimizedkhetiasCentrepoint.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();

        } //end of khetias center point

        if(tcKhetiasCRoads > 0 &&  tcKhetiasCRoads <= tcKhetiasCPoint && tcKhetiasCRoads<= tcTesiaSupermarket && tcKhetiasCRoads <= tcNaivasBandap && tcKhetiasCRoads<= tcTesiaFrontier && tcKhetiasCRoads<= tcNakumattSupermarket){
            //returm Khetias Croads
            endLatitude = optimizedKhetiascrossroads.get(0).getLatitude();
            endLongitude = optimizedKhetiascrossroads.get(0).getLongitude();
            prodStoreName = optimizedKhetiascrossroads.get(0).getStoreName();
            finalCost = tcKhetiasCRoads;
            transportCost = (optimizedKhetiascrossroads.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();
        }//end of khetias crossroads


        if(tcNakumattSupermarket > 0 &&  tcNakumattSupermarket<= tcKhetiasCRoads && tcNakumattSupermarket <= tcKhetiasCPoint && tcNakumattSupermarket<= tcTesiaSupermarket && tcNakumattSupermarket <= tcNaivasBandap && tcNakumattSupermarket<= tcTesiaFrontier){
            //return nakumatt supermarket
            endLatitude = optimizedNakumatt.get(0).getLatitude();
            endLongitude = optimizedNakumatt.get(0).getLongitude();
            prodStoreName = optimizedNakumatt.get(0).getStoreName();
            finalCost = tcNakumattSupermarket;
            transportCost = (optimizedNakumatt.get(0).getTransportCost()) * 2;
            finalProdCost = finalCost - transportCost;
            calculateDistanceAndDuration();
        }//end of nakumatt supermarket



    }






}




