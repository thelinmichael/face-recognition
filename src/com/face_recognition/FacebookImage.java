package com.face_recognition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Represents an image. 
 */
public class FacebookImage {

	String url   = null;
	Bitmap image = null;
	
	public FacebookImage(String url) throws MalformedURLException, IOException {
		this.url = url;
		this.image = getPicture();
	}	
	
	/* Make a bitmap out of a URL */
	public Bitmap getPicture() throws java.net.MalformedURLException, java.io.IOException {
		if (this.image == null)
		{
			HttpURLConnection connection = (HttpURLConnection) new java.net.URL(this.url).openConnection();
		    connection.connect();
		    InputStream input = connection.getInputStream();
		    this.image = BitmapFactory.decodeStream(input);
		}			
		return image;  
	}
	
	/* Get and set */
	public void setIcon(String icon) { this.url = icon; }
	public String getIcon() { return this.url; }
	public Bitmap getImage() { return this.image; }
	public void setImage(Bitmap image) { this.image = image; }
	
	public String toString() { return this.url; } 
}
	