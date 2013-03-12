/**
 * 
 */
package net.steveupton.ig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;

/**
 * @author Steve Upton
 * 
 *         Stores and controls access to Galleries.
 * 
 */
public class GalleryManager {

	private Map<Integer, Gallery> galleries;

	/**
	 * 
	 */
	public GalleryManager() {
		galleries = new HashMap<Integer, Gallery>();
	}

	/**
	 * 
	 * @param id
	 * @param gallery
	 */
	public void addGallery(int id, Gallery gallery) {
		galleries.put(id, gallery);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Gallery getGallery(int id) {
		return galleries.get(id);
	}

	public class CheckLocationAgainstDatabaseTask extends
			AsyncTask<Location, Void, Void> {

		@Override
		protected Void doInBackground(Location... params) {

			Location location = params[0];

			try {

				// Generate database query
				String urlString = "http://tomb.cartodb.com/api/v2/sql?q=SELECT%20gallery_id,gallery_url%20FROM%20mapdat%20WHERE%20ST_Intersects(%20the_geom,ST_SetSRID(%20ST_POINT("
						+ location.getLongitude()
						+ ","
						+ location.getLatitude()
						+ "),4326))&api_key=6c488ad00f45400158ff329ea170e2db0c4c40a8";

				// Execute the database query
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlString);
				httppost.addHeader("Accept", "application/json");
				HttpResponse response = httpclient.execute(httppost);
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

				JSONObject result = new JSONObject(jsonString);

				// Examine the result
				int totalRows = result.getInt("total_rows");

				if (totalRows > 0) {
					JSONArray galleries = result.getJSONArray("rows");
					JSONObject galleryJSON = galleries.getJSONObject(0);

					String galleryURL = galleryJSON.getString("gallery_url");
					int galleryId = Integer.valueOf(galleryJSON.getString("gallery_id"));

					Gallery gallery = new Gallery(galleryURL);

					addGallery(galleryId, gallery);

				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

}
