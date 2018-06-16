package com.example.piusin.event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.piusin.event.MapsPackage.GetDirectionsData;
import com.example.piusin.event.MapsPackage.GetNearbyPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import java.io.IOException;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private static final int LOCATION_REQUEST_CODE = 99;
    int PROXIMITY_RADIUS = 1000;
    double latitude, longitude, clatitude, clongitude, endLatitude, endLongitude;  //using distanceBetween() of loaction class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        //check if googlePlay services are available or not
        if(!CheckGooglePlayServices()){
            Log.d("onCreate", "Finishing testCase since google Play Services arent available");
            finish();
        }
        else{
            Log.d("onCreate", "Google play Services Available");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //check whether google playServices are available
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //called whenever the map is ready to be used
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    //for search purpose
    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData;

        switch (v.getId())
        {
            case R.id.btnSearch:
            {
                EditText search = findViewById(R.id.edtSearch);
                String searchedLocation = search.getText().toString();
                List<Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

                if(! searchedLocation.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(searchedLocation,5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(this, searchedLocation + " " + addressList.size(), Toast.LENGTH_SHORT).show();
                    //putting a marker on all found addresses
                    for(int i = 0; i<addressList.size(); i++){
                        Address myAddress = addressList.get(i);
                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                        mo.position(latLng);
                        mo.title("Results");
                        mMap.addMarker(mo);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    }

                }
                else{
                    Toast.makeText(this, "Enter Location to Search", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.btnHospitals:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(this, "Showing Nearby hospitals", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnRestraunts:
                mMap.clear();
                String restaurant = "restaurant";
                url = getUrl(latitude, longitude, restaurant);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(this, "Showing Nearby restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnSchools:
                mMap.clear();
                String school = "school";
                url = getUrl(latitude, longitude, school);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnTo:
                mMap.clear();
                /*//add a marker to the same position with the information window
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(endLatitude, endLongitude));
                markerOptions.title("Destination");
                markerOptions.draggable(true);

                //calc distance
                float results[] = new float[10];
                Location.distanceBetween(latitude, longitude, endLatitude, endLongitude, results);
                markerOptions.snippet("Distance=" + results[0]);
                mMap.addMarker(markerOptions);//using distanceBetween() of location class.*/

                dataTransfer = new Object[3];
                url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(endLatitude,endLongitude);
                getDirectionsData.execute(dataTransfer);
                break;

        }

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace){
        latitude = clatitude;
        longitude = clongitude;
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
       // StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyC9CPss-d4HA09j29kRA50YEv3d_gdiag4"); //T8,5   //AIzaSyArWEN_QMxgs1Eijw64sZWDtEtFZF7J7JA
        // googlePlaceUrl.append("&key="+"AIzaSyBSkvf9YoJyRMObvAgYDYon3n8g8NC1xdg");//google places API key  //https://developers.google.com/places/web-service/get-api-key
        Toast.makeText(this, "Pius: " + latitude + " " + longitude , Toast.LENGTH_SHORT).show();

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=0.0,0.0&radius=100000&type=school&sensor=true &key=AIzaSyBSkvf9YoJyRMObvAgYDYon3n8g8NC1xdg
        return googlePlaceUrl.toString();
    }

    //for direction API
    private String getDirectionsUrl(){
        latitude = clatitude;
        longitude = clongitude;
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+endLatitude+","+endLongitude);
        googleDirectionsUrl.append("&key="+"AIzaSyC1Bdiqrc7tTk0ISA3aTo-jg-AUaH1Xehs");//13

        return googleDirectionsUrl.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //for handling permission Requests such dialog boxes is permission granted or not
        switch(requestCode)
        {
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission is granted
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{ // permission is denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    protected synchronized void buildGoogleApiClient(){ //called in onMapReady

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) { //called whenevere the device is connected
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }

    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){ //if permission is not granted check whether we should request for permisiion
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            return false;
        }
        else
            return  true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { //used whenever connection is failed

    }

    @Override
    public void onLocationChanged(Location location) {//used whenever there is change in location
        lastLocation = location;

        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        clatitude = location.getLatitude();
        clongitude = location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
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
        endLatitude = marker.getPosition().latitude;
        endLongitude = marker.getPosition().longitude;
    }
}