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

import java.text.DecimalFormat;

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
    private double startLatitude, startLongitude, endLatitude, endLongitude, distanceInKms;
    private static DecimalFormat df2;
    private String storeName;
    private Double storeLatitude, storeLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();

        //getting algorithimBundleData
        Bundle bundle = getArguments();
        if(bundle != null){
            storeName = bundle.getString("storeName");
            endLatitude = bundle.getDouble("storeLatitude");
            endLongitude = bundle.getDouble("storeLongitude");
        }
        else
        {
            Toast.makeText(context, "Bundle Empty", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(context, "Store NameMap: " + storeName, Toast.LENGTH_SHORT).show();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
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

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnInfoWindowClickListener(this);

    }

    protected synchronized void buildGoogleApiClient(){ //called in onmapReady

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

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        startLatitude = location.getLatitude();
        startLongitude = location.getLongitude();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.snippet("To " + storeName + "\n" + "Pius");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        //currentLocationMarker = mMap.addMarker(markerOptions);
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(context);
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
        //Toast.makeText(context, "Latitude: " + location.getLatitude() + "Longitde: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        //endLatitude = 0.5641064;
       // endLongitude = 34.56108010000003;
        calculateDistanceAndDuration();
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){ //if permission is not granted check whether we should request for permisiion
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            return false;
        }
        else
            return  true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //for handling permission Requests such dialog boxes is permission granted or not
        switch(requestCode)
        {
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission is granted
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{ // permission is denied
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

  private void pius(){ //Displays marker when user clicks a certain point in the map
        mo = new MarkerOptions();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mo.position(latLng);
                mo.title("Store of Interest");
                mMap.addMarker(mo);
                Toast.makeText(context, "Latitude: "+ latLng.latitude + "Longitude: " + latLng.longitude, Toast.LENGTH_SHORT).show();
                //algorithmLocation();
                calculateDistanceAndDuration();
            }
        });
  }

  private void algorithmLocation(){ //from distanceBetween()
        df2 = new DecimalFormat(".##");
        endLatitude = 0.5641064;
        endLongitude = 34.56108010000003;
        LatLng latLng = new LatLng(endLatitude,endLongitude);
        mo = new MarkerOptions();
        mo.position(latLng);
        mo.title("Algorithm Location");
        float [] results = new float[10];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        distanceInKms = results[0]/1000;
        mo.snippet("Distance: " + df2.format(distanceInKms) + "KMs");
        mMap.addMarker(mo);
  }

  private void calculateDistanceAndDuration(){
      Object dataTransfer[];
      String url;
      dataTransfer = new Object[3];
      url = getDirectionsUrl();
      GetDirectionsData getDirectionsData = new GetDirectionsData();
      dataTransfer[0] = mMap;
      dataTransfer[1] = url;
      dataTransfer[2] = new LatLng(endLatitude,endLongitude);
      getDirectionsData.execute(dataTransfer);

    }

    //for direction API
    private String getDirectionsUrl(){
        Toast.makeText(context, "Start: " + startLatitude + "lng: " + startLongitude + "End: "+ endLatitude + "lng: "+ endLongitude, Toast.LENGTH_SHORT).show();
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+startLatitude+","+startLongitude);
        googleDirectionsUrl.append("&destination="+endLatitude+","+endLongitude);
        googleDirectionsUrl.append("&key="+"AIzaSyC1Bdiqrc7tTk0ISA3aTo-jg-AUaH1Xehs");//13

        return googleDirectionsUrl.toString();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(activity, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }
}

