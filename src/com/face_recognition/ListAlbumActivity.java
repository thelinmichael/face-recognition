package com.face_recognition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Presents a list view of the user's Facebook 
 * albums. 
 * 
 * A simple login button leading to Facebook's 
 * login interface. Asynchronous call to Facebook 
 * SDK is made to retrieve authoriation to the 
 * user's Facebook album.
 * 
 * Clicking a Facebook album takes the user to 
 * another activity, which shows the contents of the 
 * album.
 */
public class ListAlbumActivity extends ListActivity {
	
	/* Facebook albums */
	private LinkedList<FacebookAlbum> facebookAlbums = new LinkedList<FacebookAlbum>(); 

	/* Login button */
	private Button fbAuthButton;
	
	/* Facebook API */
	private Facebook mFacebook;
	
	/* Asynchronous requests to Facebook API */
	private AsyncFacebookRunner mAsyncFacebookRunner;
	
	/* Reference to this class */
	private ListAlbumActivity that = this;
	
	/* The view of this activity */
	private ListView lv;
	
	/* Facebook permissions */
	private String fbPermissions = "user_photos";
	
	/* List */
	ArrayAdapter listAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        /* Set up the view */
        setContentView(R.layout.view1_layout); 	// Set layout to res/values/ecran1_layout.xml
        lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.view1_layout_header, (ViewGroup) findViewById(R.id.header_layout_root));
        lv.addHeaderView(header, null, false);
           
        /* Retrieve Facebook from the global variables */
        Globals global = (Globals) getApplicationContext();
        mFacebook = global.getFacebook();
       	
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, facebookAlbums);
	    lv.setAdapter(listAdapter); 

	    fbAuthButton = (Button)findViewById(R.id.authButton);  // Button for login. Find it in R.java.
		
	    SessionStore.restore(mFacebook, this);   // Restore session if there is one   
	      
        restart();  
    }
	
	/* Determine which layout should be used depending on if user is logged in or not. */
	private void restart() {		
		facebookAlbums.clear();
		
		if (!mFacebook.isSessionValid())		 // Check if Facebook session is valid
			setLoginButton();					 // If not, show login button
		else {
			setLogoutButton(); 					 // If valid, show logout button
			importFacebookAlbums();				 //   and import Facebook albums
		}	
	}
	
	// Change the button to work as a login button
	private void setLoginButton() {		
		runOnUiThread(new Runnable() {
		     public void run() {
		    	 fbAuthButton.setText(that.getString(R.string.loginButton));
		    }
		});
		fbAuthButton.setOnClickListener(new ClickLoginListener());  // Button now listens to clicks, handled by this class (this).
	}	
	// Change the button to work as a logout button 
	private void setLogoutButton() {
		runOnUiThread(new Runnable() {
		     public void run() {
		    	 fbAuthButton.setText(that.getString(R.string.logoutButton));		    	 
		    }
		});
		fbAuthButton.setOnClickListener(new ClickLogoutListener());  // Button now listens to clicks, handled by this class (this).			
	}
	
	/* Listens to click on Login button -- Begins authorization. */
	private class ClickLoginListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			mFacebook.authorize(that,
									new String[] { fbPermissions }, // Permissions
									new LoginDialogListener() { 	// Listen for login events
									});			 
		}		
	}
	/* Run after login procedure */
	private class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) { 
			SessionStore.save(that.mFacebook, that);
			that.restart();
		}
		public void onFacebookError(FacebookError error) { /* ... */ }
		public void onError(DialogError error) { /* ... */ }
		public void onCancel() { /* ... */ }
	}
	
	/* Listens to click on Logout button -- Logs out and presents "first screen". */
	private class ClickLogoutListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			that.mAsyncFacebookRunner.logout(that, new LogoutRequestListener());				
		}	
	}
	/* Runs when user logs out. */
	private class LogoutRequestListener implements RequestListener {
		@Override
		public void onComplete(String response, Object state) {
			SessionStore.save(that.mFacebook, that); 
			that.restart();
		}
		@Override public void onIOException(IOException e, Object state) { /* ... */	}
		@Override public void onFileNotFoundException(FileNotFoundException e, Object state) { /* ... */ }
		@Override public void onMalformedURLException(MalformedURLException e, Object state) { /* ... */ }
		@Override public void onFacebookError(FacebookError e, Object state) { /* ... */ }
	}
	
	/* Listens to the result of the Facebook Dialog (Logging in, Canceling, etc..) */
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {	
		 super.onActivityResult(requestCode, resultCode, data);
	     mFacebook.authorizeCallback(requestCode, resultCode, data);    
	 }
	 	 
	 private void importFacebookAlbums() {	
		 /* Retrieve Facebook from the global variables */
		 mAsyncFacebookRunner = ((Globals) getApplicationContext()).getAsyncFacebookRunner();	
		 mAsyncFacebookRunner.request("me/albums", new GetAlbumsRequestListener());
	 }
	 
	 // Listens to the result of the login procedure
	 public class GetAlbumsRequestListener implements RequestListener {			
		 public void onComplete(final String response, Object state){
			 /* Handle response from AsyncFacebookRunner
			  * Create Facebook albums. */			 			 
					 try {			
					  	JSONObject jsonObj  = Util.parseJson(response);
					  	JSONArray jsonArray = jsonObj.getJSONArray("data");
						int i;
						for (i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = jsonArray.getJSONObject(i);
							String fbAlbumName = obj.getString("name");
							String fbAlbumId   = obj.getString("id");
							facebookAlbums.add(new FacebookAlbum(fbAlbumName, fbAlbumId));		
						}
						runOnUiThread(new Runnable() {
						     public void run() {
						    	 listAdapter.notifyDataSetChanged();								
						    }
						});
					 } catch (JSONException e) { Log.d("JSONException", e.toString()); 
					 } catch (FacebookError e) { Log.d("FacebookError", e.toString()); } 
				 }

		 public void onIOException(IOException e, Object state) { };
		 public void onFileNotFoundException(FileNotFoundException e, Object state) { };
		 public void onMalformedURLException(MalformedURLException e, Object state) { };
		 public void onFacebookError(FacebookError e, Object state) { };
	 }
	 
	 /* What happens when someone clicks something in the list. */
	 @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		FacebookAlbum clickedAlbum = (FacebookAlbum) lv.getAdapter().getItem(position);
	    String keyword = clickedAlbum.toString();
	    // Show a little message about which album was clicked
		Toast.makeText(this, "Viewing " + keyword, Toast.LENGTH_LONG).show();		
		Intent viewAlbumIntent = new Intent(this, ViewAlbumActivity.class);
		viewAlbumIntent.putExtra("fbAlbum", clickedAlbum);
		startActivity(viewAlbumIntent);		
	}
}