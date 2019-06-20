package csedevelopers.freaky.developers.womensafety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import csedevelopers.freaky.developers.resources.Mail;
import csedevelopers.freaky.developers.resources.MyURL;
import csedevelopers.freaky.developers.resources.SendSMS;
import csedevelopers.freaky.developers.services.GPSTracker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CentralActivity extends AppCompatActivity implements ShakeDetector.Listener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    TextView user_name, user_email;
    ImageView profile;
    String name, email;
    SharedPreferences sharedPref, savedlocationpref;
    private Button guardianList, autoCall, voiceRecording, help, saveLocation;

    private static int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        guardianList = (Button) findViewById(R.id.guardian);
        autoCall = (Button) findViewById(R.id.autocall);
        voiceRecording = (Button) findViewById(R.id.voice);
        help = (Button) findViewById(R.id.help);
        saveLocation = (Button) findViewById(R.id.savelocation);

        View hView = navigationView.inflateHeaderView(R.layout.header);

        user_name = (TextView) hView.findViewById(R.id.user_name);
        user_email = (TextView) hView.findViewById(R.id.user_email);
        profile = (ImageView) hView.findViewById(R.id.profile_image);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        savedlocationpref = getSharedPreferences(
                getString(R.string.saved_location_details), MODE_PRIVATE);

        name = sharedPref.getString(getString(R.string.fname), "New") + " " + sharedPref.getString(getString(R.string.lanme), "User");
        email = sharedPref.getString(getString(R.string.saved_email), "newuser@email.com");

        Log.d("Name", name);
        Log.d("mail", email);


        user_name.setText(name);
        user_email.setText(email);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        updateFCMId();
        getLocation();
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    // For rest of the options we just show a toast on click

                    case R.id.account_details:
                        return true;

                    case R.id.emergency_contacts:

                        return true;

                    case R.id.aboutus:
                        return true;

                    case R.id.logout:

                        getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().clear().apply();

                        Toast toast = new Toast(CentralActivity.this);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.toast_layout, null);
                        TextView textView = (TextView) v.findViewById(R.id.toast_message);
                        textView.setText("You have been logged out!!");
                        toast.setView(v);
                        toast.show();
                        Intent intent = new Intent(CentralActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    private void updateFCMId() {

        boolean isfcmIdsaved = sharedPref.getBoolean("isfcmIdsaved", false);

        if (!isfcmIdsaved) {

            new updateFCM().execute();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        guardianList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CentralActivity.this, EmergnecyContacts.class));
            }
        });

        autoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CentralActivity.this, AutoCall.class));

            }
        });

        voiceRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CentralActivity.this, VoiceSetting.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CentralActivity.this, HelpActivity.class));
            }
        });

        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CentralActivity.this, LocationSelect.class));
            }
        });

    }

    @Override
    public void hearShake() {

        i++;

        if (i > 3) {
            Toast.makeText(this, "Shaked....", Toast.LENGTH_SHORT).show();
            Log.d("bg", "shaked");
            String lati = savedlocationpref.getString("lati", "23.257281799999998");
            String longi = savedlocationpref.getString("longi", "87.8468376");


            Log.d("Value", "Lati " + lati + " Longi" + longi);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lattitude", lati);
                jsonObject.put("longitude", longi);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            new NotifyUser().execute(jsonObject);
//        new SendMail().execute();
            new SendNowMail().execute();
        }
    }


    private class updateFCM extends AsyncTask<Void, Void, String> {


        JSONObject object = new JSONObject();
        String fcmId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            fcmId = sharedPref.getString("fcmId", "");


        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                object.put("email", email);
                object.put("fcmId", fcmId);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MyApp.JSON, object.toString());

                Log.d("Value", "FCMREQ " + object.toString());

                Request request = new Request.Builder()
                        .url(MyURL.savefcmURL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String respo = response.body().string();

                Log.d("Value", "FCMRESP " + respo);


                return respo;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "unsuccess";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isfcmIdsaved", true);
                editor.apply();

            }


        }
    }


    class NotifyUser extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {

            try {

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MyApp.JSON, params[0].toString());

                Log.d("Value", "FCMREQ " + params[0].toString());

                Request request = new Request.Builder()
                        .url(MyURL.findPersonURL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String respo = response.body().string();

                Log.d("Value", "FINDRESP " + respo);


                return respo;


            } catch (Exception e) {
                e.printStackTrace();
            }

            return "unsuccess";

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {

                Toast.makeText(CentralActivity.this, "Successfully Requested!!", Toast.LENGTH_SHORT).show();

                i = 0;


            } else {


            }

        }
    }


    public String[] getLocation() {

        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled()) {

            try {
                String stringLatitude = String.valueOf(gpsTracker.latitude);
//                Log.d(TAG, stringLatitude);

                String stringLongitude = String.valueOf(gpsTracker.longitude);
//                Log.d(TAG, stringLongitude);

                String country = gpsTracker.getCountryName(this);
//                Log.d(TAG, country);

                String city = gpsTracker.getLocality(this);
//                Log.d(TAG, city);

                String postalCode = gpsTracker.getPostalCode(this);
//                Log.d(TAG, postalCode);

                String addressLine = gpsTracker.getAddressLine(this);
//                Log.d(TAG, addressLine);

                if (!stringLatitude.equals("0.0")) {

                    SharedPreferences.Editor editor = savedlocationpref.edit();
                    editor.putString("lati", stringLatitude);
                    editor.putString("longi", stringLongitude);
                    editor.apply();
                } else if (stringLatitude.equals("0.0")) {

                    SharedPreferences.Editor editor = savedlocationpref.edit();
                    editor.putString("lati", "23.257281799999998");
                    editor.putString("longi", "87.8468376");
                    editor.apply();

                }

                return new String[]{stringLatitude, stringLongitude};

            } catch (NullPointerException e) {
                e.printStackTrace();
                return new String[]{"", ""};
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
        return new String[]{"", ""};
    }

    class SendNowMail extends AsyncTask<String, String, String> {

        String phone;
        String[] mail;

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.savior), MODE_PRIVATE);


            String mail1 = sharedPreferences.getString("m1", "");
            String mail2 = sharedPreferences.getString("m1", "");
            String mail3 = sharedPreferences.getString("m1", "");

            mail = new String[]{mail1};


            phone = sharedPreferences.getString("p1", "");

            Log.d("Value", "Mail " + mail1);
            Log.d("Value", "Phone " + phone);


            String lati = savedlocationpref.getString("lati", "0.0");
            String longi = savedlocationpref.getString("longi", "0.0");


            String messages = "Your ward " + name + " is in danger.The last located location is http://maps.google.com/maps?q=" + lati + "," + longi + " .";


            Mail m = new Mail();
            String toArr[] = {mail1};
            m.set_to(toArr);
            m.set_from(email);
            m.set_subject("Sent By:" + email);
            m.set_body(messages);
            try {
                m.send();
            } catch (Exception e) {
                e.printStackTrace();
            }


            new SendSMS(phone, messages);


            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            i = 0;

            if (s.equals("success") && checkPermission()) {

                if (sharedPref.getBoolean("isEnable", false)) {

                    String emergencyNo = sharedPref.getString("autocall", "0");

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + emergencyNo));
                    startActivity(callIntent);

                }


            }

        }
    }

    public boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;

    }


}
