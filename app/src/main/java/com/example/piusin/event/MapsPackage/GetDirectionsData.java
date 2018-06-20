package com.example.piusin.event.MapsPackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Piusin on 3/29/2018.
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> { //for calculationg distance and duration

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    LatLng latLng;
    String duration,distance;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        distanceDuration(s);
        String [] directionList;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        displayDiretion(directionList);
    }

    public void displayDiretion(String[] directionList){
        int count = directionList.length;
        for(int i = 0; i<count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));
            mMap.addPolyline(options);
        }

    }

    private void distanceDuration(String se){
        HashMap<String, String> directionList;
        DataParser1 parser = new DataParser1();
        directionList = parser.parseDirections(se);
        duration = directionList.get("duration");
        distance = directionList.get("distance");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Suggested Stores");
        markerOptions.snippet("Distances = " +distance + "\n" + "Durations = " +duration);
        mMap.addMarker(markerOptions);
    }
}
