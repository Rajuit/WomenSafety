package csedevelopers.freaky.developers.notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import csedevelopers.freaky.developers.womensafety.R;

/**
 * Created by PURUSHOTAM on 11/30/2016.
 */

public class MyFireBaseInstanceId extends FirebaseInstanceIdService {

    public static String fcmId;
    public static String oldfcmId;
    private SharedPreferences sharedPreferences;

    @Override
    public void onTokenRefresh() {
        fcmId = FirebaseInstanceId.getInstance().getToken();

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcmId", fcmId);
        editor.apply();
        oldfcmId = sharedPreferences.getString("fcmId", "");

        Log.d("Value", fcmId + " Old One" + oldfcmId);

        if (!fcmId.equals(oldfcmId) && !oldfcmId.equals("")) {

            Log.d("Value", fcmId + " Old One" + oldfcmId);


        }

    }

}
