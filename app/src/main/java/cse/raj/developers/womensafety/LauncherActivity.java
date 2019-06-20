package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class LauncherActivity extends AppCompatActivity {

    boolean haveLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        haveLoggedIn = sharedPreferences.getBoolean(getString(R.string.haveLoggedIn), false);
    }

    @Override
    protected void onStart() {
        super.onStart();


        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {

                if (haveLoggedIn) {
                    startActivity(new Intent(LauncherActivity.this, CentralActivity.class));
                } else {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                }
                finish();

            }
        }, 2000);

    }
}
