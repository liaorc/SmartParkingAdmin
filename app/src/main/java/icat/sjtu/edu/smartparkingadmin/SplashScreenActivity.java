package icat.sjtu.edu.smartparkingadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;


public class SplashScreenActivity extends Activity {
    private static final String TAG = "PARK_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler h = new Handler();
        h.postDelayed(new SplashHandler(), 1000);
    }

    class SplashHandler implements Runnable{
        public void run() {
            Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
            //Intent i = new Intent(SplashScreenActivity.this, QueryListActivity.class);
            startActivity(i);
            SplashScreenActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

}
