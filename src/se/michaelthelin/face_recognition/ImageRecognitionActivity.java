package se.michaelthelin.face_recognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_objdetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ImageRecognitionActivity extends Activity {

	private Mat imageMat;

	private FacebookImage facebookImage = null;

	private Context context=this;

	private ImageView imageView; 

	private Bitmap fbImage;

	private CascadeClassifier mCascade;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view3_layout);
		imageView = (ImageView) findViewById(R.id.imageview);

		Intent intent = getIntent();   
		Bundle b = intent.getExtras();  
		String chosenImageUrl = (String) b.get("chosenImageUrl");   

		try {                           
			facebookImage = new FacebookImage(chosenImageUrl);    
			fbImage = facebookImage.getPicture(); 
			imageMat = org.opencv.android.Utils.bitmapToMat(fbImage.copy(Bitmap.Config.ARGB_8888,true)); 
		} catch (MalformedURLException e) { /* ... */ e.printStackTrace();
		} catch (IOException e)     { /* ... */ e.printStackTrace();
		}

		Loader.load(opencv_objdetect.class);  

		try {

			InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
			File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
			FileOutputStream os = new FileOutputStream(cascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
			if (mCascade.empty()) {
					Log.e("TAG", "Failed to load cascade classifier");
					mCascade = null;
			} else
				Log.i("TAG", "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

			cascadeFile.delete();
			cascadeDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("TAG", "Failed to load cascade. Exception thrown: " + e);
		}

		LinkedList<Rect> facesdetection = new LinkedList<Rect>();
		
		mCascade.detectMultiScale(imageMat, facesdetection);

		Bitmap imageBitmap = null;
		for (Rect r : facesdetection){  
			Core.rectangle(imageMat, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 2); 
		}

		imageBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(imageMat, imageBitmap);
		imageView.setImageBitmap(imageBitmap); 
	}
}