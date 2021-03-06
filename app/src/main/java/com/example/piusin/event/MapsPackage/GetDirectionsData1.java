package com.example.piusin.event.MapsPackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData1 extends AsyncTask<Object, String, String> { //for drawing path
    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration,distance;
    LatLng latLng;

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
        HashMap<String, String> directionList;
        DataParser1 parser = new DataParser1();
        directionList = parser.parseDirections(s);
        duration = directionList.get("duration");
        distance = directionList.get("distance");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Suggested Store");
        markerOptions.snippet("Distances = " +distance + "\n" + "Durations = " +duration);
        mMap.addMarker(markerOptions);

    }


}
