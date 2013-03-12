/**
 * 
 */
package net.steveupton.ig;

import org.json.JSONObject;

/**
 * @author Steve Upton
 * 
 *         Represents a piece of art on display.
 * 
 *         <p>
 *         Can be ...
 * 
 */
public class ArtObject {

	/**
	 * The JSON object that describes the art object
	 */
	JSONObject artObjectJSON;

	/**
	 * 
	 */
	public ArtObject(JSONObject artObjectJSON) {
		this.artObjectJSON = artObjectJSON;
	}
	
	

}
