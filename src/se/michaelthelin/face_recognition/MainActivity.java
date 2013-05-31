package se.michaelthelin.face_recognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

  private String TAG = "MainActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Start Facebook login
    Log.i(TAG, "Opening session..");
    Session.openActiveSession(this, true, new Session.StatusCallback() {

      // Callback when session changes state
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        Log.i(TAG, "Session opened. Checking state. (Open -> " + session.isOpened() + ")");
        
        if (session.isOpened()) {
          Log.i(TAG, "Session is open. Quering OpenGraph..");
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            @Override
            public void onCompleted(GraphUser user, Response response) {
              Log.i(TAG, "Completed. Got user " + user);
              if (user != null) {
                TextView welcome = (TextView) findViewById(R.id.welcome);
                welcome.setText("Hello " + user.getName() + "!");
              }
            }

          });
        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    Log.i(TAG, "Got Activity result.");
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }

}
