package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import csedevelopers.freaky.developers.resources.CheckNet;
import csedevelopers.freaky.developers.resources.MyURL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static csedevelopers.freaky.developers.resources.MyURL.APIKEY;

public class OTPVeification extends AppCompatActivity {

    String phoneNo;
    EditText otp;
    TextView submit, resend, verifyNo;
    Context context;
    JSONObject finalJsonobject;
    AVLoadingIndicatorView indicatorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpveification);
        submit = (TextView) findViewById(R.id.submit);
        resend = (TextView) findViewById(R.id.resend);
        resend.setClickable(false);
        verifyNo = (TextView) findViewById(R.id.verifyno);
        phoneNo = getIntent().getStringExtra("phoneNo");

        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OTP Verification!!");

        try {
            finalJsonobject = new JSONObject(getIntent().getStringExtra("jsonObject"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //This will bipass the phone verification and proceed for register
       /* if (new CheckNet(OTPVeification.this).checkNet()) {
            new Signup().execute(jsonObject);
        }*/


        otp = (EditText) findViewById(R.id.otpentry);
        context = this;


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new CheckNet(OTPVeification.this).checkNet()) {

                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("OTP has been sent to your number again!!");
                    toast.setView(v);
                    toast.show();

                    String resendOTP = "https://control.msg91.com/api/retryotp.php?authkey=" + APIKEY + "&mobile=91" + phoneNo + "&retrytype=voice";
                    if (new CheckNet(OTPVeification.this).checkNet()) {
                        new SendOTP().execute(resendOTP, "1");
                    }
                }
                setTimer();
            }
        });
        setTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        verifyNo.setText("+91 " + phoneNo);

        Random random = new Random();

        String otp = String.format("%04d", random.nextInt(10000));

        String requestOTP = "https://control.msg91.com/api/sendotp.php?authkey=" + APIKEY + "&mobile=91"
                + phoneNo +
                "&message=Your%20otp%20is%20"
                + otp +
                "&sender=WMSafe&otp="
                + otp;

        if (new CheckNet(this).checkNet()) {
            new SendOTP().execute(requestOTP, "0");
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();


        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        otp.setFilters(FilterArray);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otpString = otp.getText().toString();
                boolean check = false;
                if (otpString.equals("")) {
                    check = false;
                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("OTP Field is Empty!!");
                    toast.setView(v);
                    toast.show();

                } else if (otpString.length() != 4) {
                    check = false;

                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("OTP Field must have 4 Characters!!");
                    toast.setView(v);
                    toast.show();

                } else {
                    check = true;
                }

                if (check && new CheckNet(OTPVeification.this).checkNet()) {

                    new VerifyOTP().execute(otpString);

                }
            }
        });


    }

    private void setTimer() {
        resend.setEnabled(false);
        resend.setTextColor(ContextCompat.getColor(OTPVeification.this, R.color.gray));
        new CountDownTimer(30000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    resend.setText("Resend via call ( " + secondsLeft + " )");
                }
            }

            @Override
            public void onFinish() {

                final float scale = OTPVeification.this.getResources().getDisplayMetrics().density;
                int pixels = (int) (120 * scale + 0.5f);

                resend.setClickable(true);
                resend.setText("Resend via call");
                resend.setWidth(pixels);
                resend.setTextColor(ContextCompat.getColor(OTPVeification.this, R.color.white));
                resend.setEnabled(true);
            }
        }.start();
    }


    class SendOTP extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
        }

        @Override
        protected String doInBackground(String... strings) {


            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "oops";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            indicatorView.hide();
            JSONObject jsonObject = null;

            Log.d("1st", s);

            try {
                jsonObject = new JSONObject(s);

                if (jsonObject.getString("type").equals("success")) {

                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("Succssfully Sent");
                    toast.setView(v);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    class VerifyOTP extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indicatorView.show();
            submit.setClickable(false);
        }

        @Override
        protected String doInBackground(String... jsonObjects) {


            try {
                OkHttpClient client = new OkHttpClient();

                String verifyOTP = "https://control.msg91.com/api/verifyRequestOTP.php?authkey="
                        + APIKEY + "&mobile=91"
                        + phoneNo + "&otp=" + jsonObjects[0];

                Request request = new Request.Builder()
                        .url(verifyOTP)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "oops";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            indicatorView.hide();
            submit.setClickable(true);

            Log.d("2nd", s);

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(s);

                if (jsonObject.getString("type").equals("success")) {
                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("Successfully Verified");
                    toast.setView(v);
                    toast.show();

                    new SignUp().execute(finalJsonobject);

                } else {
                    Toast toast = new Toast(OTPVeification.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.toast_layout, null);
                    TextView textView = (TextView) v.findViewById(R.id.toast_message);
                    textView.setText("Unable to Verify!");
                    toast.setView(v);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class SignUp extends AsyncTask<JSONObject, Void, String> {


        @Override
        protected String doInBackground(JSONObject... objects) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MyApp.JSON, objects[0].toString());

                Log.d("SIDDHARTH", objects[0].toString());

                Request request = new Request.Builder()
                        .url(MyURL.newuserURL)
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject object;
            try {
                object = new JSONObject(s);
                boolean success = object.getString("success").equals("success");
                boolean mailExist = object.getBoolean("ismailexist");
                boolean phoneExist = object.getBoolean("isphoneexist");

                if (!success && mailExist && !phoneExist) {

                    Toast.makeText(context, "Email already Exists!!", Toast.LENGTH_SHORT).show();

                } else if (!success && !mailExist && phoneExist) {

                    Toast.makeText(context, "Phone already Exist!!", Toast.LENGTH_SHORT).show();

                } else if (!success && mailExist && phoneExist) {

                    Toast.makeText(context, "Both Phone and Email  already Exists!!", Toast.LENGTH_SHORT).show();

                } else if (success && !mailExist && !phoneExist) {

                    SharedPreferences sharedPref = context.getSharedPreferences(
                            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putBoolean(context.getString(R.string.haveLoggedIn), true);
                    editor.putString(context.getString(R.string.fname), finalJsonobject.getString("fName"));
                    editor.putString(context.getString(R.string.lanme), finalJsonobject.getString("lName"));
                    editor.putString(context.getString(R.string.saved_email), finalJsonobject.getString("email"));
                    editor.apply();

                    Toast.makeText(context, "Successfully Registered!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTPVeification.this, CentralActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    OTPVeification.this.finish();
                    startActivity(intent);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

                Toast.makeText(context, "NullPointerExcetion", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
