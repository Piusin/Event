package com.example.piusin.event;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.piusin.event.MapsPackage.CustomInfoWindowAdapter;
import com.example.piusin.event.MapsPackage.GetDirectionsData;
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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

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
    private Double startLatitude, startLongitude, endLatitude, endLongitude, startLatitudeA , startLongitudeA;
    private double distanceInKms, pivot, transportCost = 10.0;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private String productName, productDesc, storeName;
    private ArrayList<AlgorithmProductDataProvider> algorithmProductDataProviderArrayList;
    private ArrayList<StoreDistancesDataProvider> storeDistancesDataProviderArrayList;
    private ArrayList<DCOptimazitionDataProvider> dcOptimazitionDataProviderArrayList;
    private List<DCOptimazitionDataProvider> sortedDCOptimizationArrayList;
    private List<DCOptimazitionDataProvider> pivotList;
    private List<DCOptimazitionDataProvider> less;
    private List<DCOptimazitionDataProvider> greater;
    private int i, middle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();
        /*SharedPreferences sharedPreferences = context.getSharedPreferences("storePref",
                Context.MODE_PRIVATE);
        Toast.makeText(context, "Store: " + sharedPreferences.getString("store", ""), Toast.LENGTH_SHORT).show();
*/
        //getting algorithimBundleData
        Bundle bundle = getArguments();
        if (bundle != null) {
          productName = bundle.getString("productName");
          productDesc = bundle.getString("productDesc");
          loadAlgorithmProducts();

        } else {
            Toast.makeText(context, "Bundle Empty", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

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
        mMap.setOnInfoWindowClickListener(this);
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
        getStartCoordinates(location.getLatitude(), location.getLongitude());
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.snippet("To " + sortedDCOptimizationArrayList.get(0).dcStoreName + "\n" + "Item Costs: " +
        sortedDCOptimizationArrayList.get(0).dcProductCosts + "\n" + "Transport Cost: " +
                sortedDCOptimizationArrayList.get(0).dcTransportCost + "\n" + "Total Cost: " + sortedDCOptimizationArrayList.get(0).dcTotalCost);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        //currentLocationMarker = mMap.addMarker(markerOptions);
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(context);
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

        calculateDistanceAndDuration();
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

    private void pius() { //Displays marker when user clicks a certain point in the map
        mo = new MarkerOptions();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mo.position(latLng);
                mo.title("Store of Interest");
                mMap.addMarker(mo);
                Toast.makeText(context, "Latitude: " + latLng.latitude + "Longitude: " + latLng.longitude, Toast.LENGTH_SHORT).show();
                //algorithmLocation();
                calculateDistanceAndDuration();
            }
        });
    }

    private void algorithmLocation() { //from distanceBetween()
        df2 = new DecimalFormat(".##");
        endLatitude = 0.5641064;
        endLongitude = 34.56108010000003;
        LatLng latLng = new LatLng(endLatitude, endLongitude);
        mo = new MarkerOptions();
        mo.position(latLng);
        mo.title("Algorithm Location");
        float[] results = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        distanceInKms = results[0] / 1000;
        mo.snippet("Distance: " + df2.format(distanceInKms) + "KMsff");
        mMap.addMarker(mo);
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


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(activity, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    //fetching products from server.
    private void loadAlgorithmProducts(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("piusPref",
                Context.MODE_PRIVATE);
        startLatitudeA = Double.valueOf(sharedPreferences.getString("startLat", "0.6174969"));
        startLongitudeA = Double.valueOf(sharedPreferences.getString("startlngq", "34.52367779999997"));
        algorithmProductDataProviderArrayList = new ArrayList<>();
        storeDistancesDataProviderArrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_ALGORITHMPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    JSONArray array = new JSONArray(response);
                    for (i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);

                        if(product.getString("product_name").contains(productName) && product.getString("product_description").contains(productDesc)) {
                            //adding the product to arrayList
                            algorithmProductDataProviderArrayList.add(new AlgorithmProductDataProvider(
                                    product.getString("product_name"),
                                    product.getString("store_name"),
                                    product.getDouble("product_cost"),
                                    product.getDouble("store_latitude"),
                                    product.getDouble("store_longitude"),
                                    product.getInt("quantity_at_hand")
                            ));

                                endLatitude = product.getDouble("store_latitude");
                                endLongitude = product.getDouble("store_longitude");
                                storeName = product.getString("store_name");

                            // start of calculate storeDistances and store in an arraylist
                                if (startLatitudeA != null && startLongitudeA != null && endLatitude != null && endLongitude != null) {

                                    df2 = new DecimalFormat(".##");
                                    float[] results = new float[10];
                                    Location.distanceBetween(startLatitudeA, startLongitudeA, endLatitude, endLongitude, results);
                                    distanceInKms = results[0] / 1000;
                                    storeDistancesDataProviderArrayList.add(new StoreDistancesDataProvider(
                                            storeName,
                                            distanceInKms
                                    ));
                                } else {
                                    Toast.makeText(context, "Error Fetching Algorithm Coordinates", Toast.LENGTH_SHORT).show();
                                } //end of calculating storeDistances
                        }
                    }
                    distanceCostOptimazitinLogic();

                } catch (JSONException e) {
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

    private void storeDistances() { //from distanceBetween()
        int index;
        for (index = 0; index< algorithmProductDataProviderArrayList.size(); index++) {
            storeName = algorithmProductDataProviderArrayList.get(index).storeName;
            endLatitude = algorithmProductDataProviderArrayList.get(index).storeLatitude;
            df2 = new DecimalFormat(".##");
            endLatitude = 0.5641064;
            endLongitude = 34.56108010000003;
            float[] results = new float[10];
            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
            distanceInKms = results[0] / 1000;
        }
    }

    public void getStartCoordinates(double startLat, double startLng){
        startLatitude = startLat;
        startLongitude = startLng;
    }

    //Logic for store selection
    private void distanceCostOptimazitinLogic(){
        dcOptimazitionDataProviderArrayList = new ArrayList<>();
        double productCost , storeDistance, totalCost, OstoreLatitude, OstoreLongitude; //store coordinates
        for(i= 0; i<algorithmProductDataProviderArrayList.size() && i<storeDistancesDataProviderArrayList.size(); i++) {
            if (algorithmProductDataProviderArrayList.get(i).storeName == storeDistancesDataProviderArrayList.get(i).getStoreName()){
                storeName = algorithmProductDataProviderArrayList.get(i).storeName;
                storeDistance = storeDistancesDataProviderArrayList.get(i).getStoreDistance();
                productCost = algorithmProductDataProviderArrayList.get(i).productCost;
                OstoreLatitude = algorithmProductDataProviderArrayList.get(i).storeLatitude;
                OstoreLongitude = algorithmProductDataProviderArrayList.get(i).storeLongitude;
                transportCost = Double.valueOf(df2.format(storeDistance * transportCost));
                totalCost = transportCost + productCost;

                //add the element to dco arrayList
                dcOptimazitionDataProviderArrayList.add(new DCOptimazitionDataProvider(
                        totalCost,
                        productCost,
                        transportCost,
                        storeName,
                        OstoreLatitude,
                        OstoreLongitude
                ));
            }else
            {
                Toast.makeText(context, "You Lost Son", Toast.LENGTH_SHORT).show();
            }
        }
        DCOptimizationQuickSort();
    } //the array will be sorted and the store wuth the best pickValue selected and posted vicevisi second pickvalue

    private void DCOptimizationQuickSort(){ //start of QuickSort
        middle = (int) Math.ceil((double) dcOptimazitionDataProviderArrayList.size() / 2);
        pivot = dcOptimazitionDataProviderArrayList.get(middle).dcTotalCost;
        pivotList = new ArrayList<>();
        less = new ArrayList<>();
        greater = new ArrayList<>();

        for (int i = 0; i < dcOptimazitionDataProviderArrayList.size(); i++) {
            if (dcOptimazitionDataProviderArrayList.get(i).dcTotalCost <= pivot) {
                if (i == middle) {
                    continue;
                }
                less.add(dcOptimazitionDataProviderArrayList.get(i));
            } else {
                greater.add(dcOptimazitionDataProviderArrayList.get(i));
            }
        }
        pivotList.add(dcOptimazitionDataProviderArrayList.get(middle));
        concatenate(less, pivotList, greater);
        endLatitude = concatenate(less,pivotList,greater).get(0).dcStoreLatitude;
        endLongitude = concatenate(less,pivotList,greater).get(0).dcStoreLongitude;
    } //end of QuickSort

        //start of QuickSortMerge
        private List<DCOptimazitionDataProvider> concatenate(List<DCOptimazitionDataProvider> less, List<DCOptimazitionDataProvider> pivotList, List<DCOptimazitionDataProvider> greater){

        int index;
            sortedDCOptimizationArrayList = new ArrayList<>();

        for (int i = 0; i < less.size(); i++) {
            sortedDCOptimizationArrayList.add(less.get(i));
        }

            sortedDCOptimizationArrayList.add(pivotList.get(0));

        for (int i = 0; i < greater.size(); i++) {
            sortedDCOptimizationArrayList.add(greater.get(i));
        }

        return sortedDCOptimizationArrayList;
    }// end of QuickSort merges
}



