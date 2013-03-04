/**
 * 
 */
package com.aart.gallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author Steve Upton
 * 
 *         Detects when a gallery is approached/entered and downloads the art
 *         appropriately.
 * 
 */
public class GalleryFinderService extends Service {

	LocationManager locationManager;

	LocationProvider locationProvider;

	/**
	 * 
	 */
	public GalleryFinderService() {
		System.out.println("Gallery Service Constructor!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		
		System.out.println("Hi from onBind");
		
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		locationManager = (LocationManager) this
		.getSystemService(Context.LOCATION_SERVICE);

		locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			
			@Override
			public void onProviderEnabled(String provider) {
			}
			
			@Override
			public void onProviderDisabled(String provider) {				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				System.out.println("Location: " + location.toString());
				
				try {
					//String query = URLEncoder.encode("SELECT name FROM mapdat WHERE ST_Intersects( the_geom,ST_SetSRID( ST_POINT("+location.getLongitude()+","+location.getLatitude()+"),4326))&api_key=6c488ad00f45400158ff329ea170e2db0c4c40a8", "ISO-8859-1");
					//String urlString = "http://tomb.cartodb.com/api/v2/sql?q=" + query;
					//URI uri = new URI(urlString);
					//System.out.println("URI = " + uri.toString());
					String urlString = "http://tomb.cartodb.com/api/v2/sql?q=SELECT%20name%20FROM%20mapdat%20WHERE%20ST_Intersects(%20the_geom,ST_SetSRID(%20ST_POINT("+location.getLongitude()+","+location.getLatitude()+"),4326))&api_key=6c488ad00f45400158ff329ea170e2db0c4c40a8";
					HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost(urlString);
			        httppost.addHeader("Accept", "application/json"); 
			        HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
			        InputStream is = entity.getContent();
			        
			        //String result = convertStreamToString(is); 
		            //result = result.replace("\n", "");
			        //JSONObject json = new JSONObject(result);
			        
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			        StringBuilder sb = new StringBuilder();
			        String line = null;
			        while ((line = reader.readLine()) != null) {
			        	sb.append(line + "\n");
			        }
			        is.close();
			        String jsonString = sb.toString();
			        
			        JSONObject json = new JSONObject(jsonString);
			        
			        System.out.println("JSON= " + json.toString());
			        
			        //02-24 13:26:13.172: I/System.out(17599): JSON= {"error":["syntax error at or near \"c488ad00f45400158ff329ea170e2db0c4c40a8\""]}

			        
			        int totalRows = json.getInt("total_rows");
			        if (totalRows > 0) {
			        	JSONArray galleries = json.getJSONArray("rows");
			        	JSONObject gallery = galleries.getJSONObject(0);
				        String galleryName = gallery.getString("name");
				        Toast.makeText(getApplicationContext(), "In gallery: " + galleryName, Toast.LENGTH_SHORT).show();
			        } else {
			        	Toast.makeText(getApplicationContext(), "Not in gallery", Toast.LENGTH_SHORT).show();
			        }
			        
			        
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				//} catch (URISyntaxException e) {
					//e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Toast.makeText(getApplicationContext(), location.getLatitude() + ", " + location.getLongitude() , Toast.LENGTH_SHORT).show();
				
			}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
		
		Toast.makeText(getApplicationContext(), "Location provider setup done.", Toast.LENGTH_SHORT).show();
		
		return START_STICKY;
		
	}
	
	public static String convertStreamToString(InputStream is) throws Exception {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	        sb.append(line + "\n");
	    }
	    is.close();
	    return sb.toString();
	}
	
	

}
