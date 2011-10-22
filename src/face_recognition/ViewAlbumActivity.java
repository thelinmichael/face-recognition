package face_recognition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.mure_facebook.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Presents a table which contains FacebookImages taken 
 * from the FacebookAlbum chosen in Ecran1Activity.
 * 
 * Using asynchronous Facebook API calls to get the 
 * image from Facebook.
 * 
 * Selection of one FacebookImage will take the user to the third screen.
 * 
 * ETC:
 * Use ImageAdapter to show pictures. 
 * Use OnClickItemListener to detect which object gets clicked.
 */
public class ViewAlbumActivity extends Activity {
	
	/* Contains one album */
	FacebookAlbum facebookAlbum;
	
	/* Contains a list of images */
	LinkedList<FacebookImage> facebookImages = new LinkedList<FacebookImage>();
	
	AsyncFacebookRunner mAsyncFacebookRunner;
	
	GridView gridview;
	ImageAdapter gridAdapter;
	
	 /**
     * Runs automatically when the activity starts. (Activity enters Starting stage) 
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view2_layout); // Set layout to res/values/ecran2_layout.xml   
        
        /* Receiving the FacebookAlbum that was sent from Ecran1 */
        Bundle b = getIntent().getExtras();
        facebookAlbum = b.getParcelable("fbAlbum");
        
        gridview = (GridView) findViewById(R.id.gridview);
		gridAdapter = new ImageAdapter(this);
		gridview.setAdapter(gridAdapter);
		         
		gridview.setOnItemClickListener(new OnItemClickListener() {        
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {            
				startRecognitionAct(position);
			} 
		});	
        		
        /* Retrieve asynchronous Facebook API runner from the global variables */
        mAsyncFacebookRunner = ((Globals) getApplicationContext()).getAsyncFacebookRunner();	  
        
        /* Make asynchronous Facebook API call to get the images from the FacebookAlbum */
        mAsyncFacebookRunner.request(facebookAlbum.getId() + "/photos", new GetAlbumsRequestListener());
    }
	
	// Listens to the result of the login procedure
	public class GetAlbumsRequestListener implements RequestListener {			
		 public void onComplete(final String response, Object state){
			 
			JSONObject jsonObj;
			try {
				jsonObj = Util.parseJson(response);
				JSONArray jsonDataArray = jsonObj.getJSONArray("data");
				JSONObject jsonPictureObj;
				String fbImageURL;
				int i;
				for (i = 0; i < jsonDataArray.length(); i++) {
					jsonPictureObj = jsonDataArray.getJSONObject(i);
					fbImageURL = jsonPictureObj.getString("picture");			
					try {
						facebookImages.add(new FacebookImage(fbImageURL));
					} catch (MalformedURLException e) { /* ... */ e.printStackTrace();
					} catch (IOException e) { /* ... */ e.printStackTrace();
					}
				}
				showFbPictures();
				
			} catch (JSONException e) { /* ... */  e.printStackTrace();
			} catch (FacebookError e) { /* ... */ e.printStackTrace(); }	  
		 }
		 public void onIOException(IOException e, Object state) { };
		 public void onFileNotFoundException(FileNotFoundException e, Object state) { };
		 public void onMalformedURLException(MalformedURLException e, Object state) { };
		 public void onFacebookError(FacebookError e, Object state) { };
	 }	
	
	/* Puts the pictures into the GridView. Checks facebookImages for the URLs. */
	private void showFbPictures() {
		gridview.post(new Runnable() {
	        @Override
	        public void run() {
	        	 gridAdapter.notifyDataSetChanged();
	        	 gridview.invalidateViews();        
	        }
	    });	
	}
	
	/* Extending the base adapter for grid view. */
	private class ImageAdapter extends BaseAdapter {
	    private Context mContext; // Will be set to this activity
	    
	    /* Constructor */
	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    /* Number of pictures */
	    public int getCount() {
	        return facebookImages.size();
	    }

	    // Retrieve the object at a certain position.
	    public Object getItem(int position) {
	    	Object returnObject = null;
				returnObject = (Object) facebookImages.get(position).toString();
	        return returnObject;
	    }

	    // Retrieve a certain row number. (?)
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	ImageView imageView;
	        if (convertView == null) { 
	            imageView = new ImageView(mContext);
		        try {
					imageView.setImageBitmap(facebookImages.get(position).getPicture());
				} catch (MalformedURLException e) { /* ... */ e.printStackTrace();
				} catch (IOException e) { e.printStackTrace();
				}
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }             
            return imageView;  
	    }
	}

	private void startRecognitionAct(int position) {
		String chosenImageUrl = this.gridAdapter.getItem(position).toString();			
		Intent recognitionIntent = new Intent(this, ImageRecognitionActivity.class);
		recognitionIntent.putExtra("chosenImageUrl", chosenImageUrl);
		startActivity(recognitionIntent);
	}
}
