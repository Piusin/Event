package com.example.piusin.event.MapsPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Piusin on 3/29/2018.
 */

public class DownloadUrl {
    //Class for retriving data from url using HttpUrlConnections
    public String readUrl(String myUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;



        try {
            URL url = new URL(myUrl); //created it
            urlConnection = (HttpURLConnection) url.openConnection(); //opened it
            urlConnection.connect(); //and connected it.

            //reading data from the urlConnection
            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            //Read eachline one by one
            String line= "";
            while ((line = br.readLine()) != null){
                sb.append(line);
            }

            //convert StringBuffered into string and store it into String data Variable.
            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data; //data returned is json format and can be accessed using HTTP Connections
    }
}
