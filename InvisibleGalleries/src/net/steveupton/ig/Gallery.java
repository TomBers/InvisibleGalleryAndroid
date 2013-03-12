/**
 * 
 */
package net.steveupton.ig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * @author Steve Upton
 * 
 *         Describes a gallery.
 * 
 *         <p>
 *         Stores images, videos, audio and urls for the gallery.
 * 
 */
public class Gallery {

	/**
	 * The JSON object that describes the gallery.
	 */
	private JSONObject galleryJSON;
	
	/**
	 * The name of this gallery.
	 */
	private String name;
	
	/**
	 * A description of this gallery.
	 */
	private String description;

	public Gallery(JSONObject galleryJSON) {
		this.galleryJSON = galleryJSON;
		
		// Set basic properties
		
		
		
	}
	
	/**
	 * 
	 * @param id
	 * @param galleryURL
	 */
	@Deprecated
	public Gallery(String galleryURL) {
		
		// Download the gallery JSON
		new DownloadBasicGalleryDetails().execute(galleryURL + "gallery.json");

	}

	@Deprecated
	private class DownloadBasicGalleryDetails extends
			AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String galleryJSONURL = params[0];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(galleryJSONURL);
			httppost.addHeader("Accept", "application/json");
			HttpResponse response;
			try {
				response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				String jsonString = sb.toString();

				galleryJSON = new JSONObject(jsonString);
				
				// TODO
				
				// Extract basic data from json
				
				// Generate placeholder art objects
				
				// Mark gallery as ready

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

}
