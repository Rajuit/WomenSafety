package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import csedevelopers.freaky.developers.resources.CheckNet;
import csedevelopers.freaky.developers.resources.MyToast;
import csedevelopers.freaky.developers.resources.MyURL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationSelect extends AppCompatActivity {

    private String TAG = "SID";
    private Button save;
    private Context context;
    private String lati, longi, placeName;
    String emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        save = (Button) findViewById(R.id.save);
        context = this;

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        new MyToast(context, "Please save your current address!!").show();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                placeName = place.getName().toString();
                emailString = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE)
                        .getString(getResources().getString(R.string.saved_email), "");
                LatLng latLng = place.getLatLng();
                lati = String.valueOf(latLng.latitude);
                longi = String.valueOf(latLng.longitude);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CheckNet(context).checkNet() && !placeName.isEmpty() && !lati.isEmpty() && !longi.isEmpty()) {

                    new LocationSaver().execute();

                }

            }
        });

    }


    private class LocationSaver extends AsyncTask<Void, Void, String> {

        JsonObject jsonObject;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jsonObject = new JsonObject();
            jsonObject.addProperty("lattitude", lati);
            jsonObject.addProperty("longitude", longi);
            jsonObject.addProperty("mail", emailString);
            jsonObject.addProperty("place", placeName);

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MyApp.JSON, jsonObject.toString());


                Request request = new Request.Builder()
                        .url(MyURL.saveAddressURL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response.equals("success")) {

                Toast.makeText(context, "Successfully saved!", Toast.LENGTH_SHORT).show();
                finish();

            } else {

                Toast.makeText(context, "Unsuccessful!!", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
