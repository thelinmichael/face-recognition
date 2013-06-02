package se.michaelthelin.face_recognition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

public class MainActivity extends Activity {

  private String TAG = "MainActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Create session with album permissions

    // Start Facebook login
    Session.openActiveSession(this, true, new Session.StatusCallback() {

      // Callback when session changes state
      @Override
      public void call(Session session, SessionState state, Exception exception) {

        // Check if token has been updated
        if (state == SessionState.OPENED_TOKEN_UPDATED) {
          Log.i(TAG, "Updated token");
        }
        
        String[] PERMISSION_ARRAY_READ = {"email"};
        List<String> permissionList = Arrays.asList(PERMISSION_ARRAY_READ);

        // Request permissions.
        session.requestNewReadPermissions(new Session.NewPermissionsRequest(MainActivity.this, permissionList));
        
        if (session.isOpened()) {
          
          // If all required permissions are available...
          if (session.getPermissions().containsAll(permissionList)) {
            Log.i(TAG, "Email permission avaiable.");
            Request.executeGraphPathRequestAsync(session, "me/albums", new Request.Callback() {

              @Override
              public void onCompleted(Response response) {
                GraphObject graphObject = response.getGraphObject();
                Map graphMap = graphObject.asMap();
                JSONArray data = (JSONArray) graphMap.get("data");
                Log.i(TAG, data.toString());
              }
            });
          } else {
            Log.i(TAG, "Email permission was not available");
          }

        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (Session.getActiveSession() != null) {
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
  }
}
