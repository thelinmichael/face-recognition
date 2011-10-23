package com.face_recognition;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import android.app.Application;

/**
 * Global variables. 
 * 
 * Holds Facebook objects.
 */
public class Globals extends Application {
	
	/* Application ID */
	final private String APP_ID = "142471809184972";
	
	/* Facebook API */
	private Facebook mFacebook;
	
	/* Runs Facebook API requests asynchronically */
	private AsyncFacebookRunner mAsyncFacebookRunner;
	
	/* Application should be used as a Singleton. */
	private static Globals singleton;
	
	@Override
	public void onCreate() {	
		super.onCreate();
		singleton = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncFacebookRunner = new AsyncFacebookRunner(mFacebook);
	}
	
	/* Get an instance of this class */
	public Globals getInstance() {
		return singleton;
	}
		
	/* Get and set methods. */
	public void setFacebook(Facebook facebook) {
		this.mFacebook = facebook;
	}
	public Facebook getFacebook() {
		return this.mFacebook;
	}
	public void setAsyncFacebookRunner(AsyncFacebookRunner asyncFacebookRunner) {
		this.mAsyncFacebookRunner = asyncFacebookRunner;
	}
	public AsyncFacebookRunner getAsyncFacebookRunner() {
		return this.mAsyncFacebookRunner;
	}
}
