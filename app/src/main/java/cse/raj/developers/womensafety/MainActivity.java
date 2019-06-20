package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import csedevelopers.freaky.developers.resources.CheckNet;
import csedevelopers.freaky.developers.resources.MyURL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    TextView textView;
    ImageView signIn;
    Context context;

    @NotEmpty
    EditText user, password;

    private Validator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.signup);
        signIn = (ImageView) findViewById(R.id.signin);
        user = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);
        context = this;

        validator = new Validator(this);
        validator.setValidationListener(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validate();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, Signup.class));

            }
        });
    }

    @Override
    public void onValidationSucceeded() {

        if (new CheckNet(this).checkNet()) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("user", user.getText().toString());
            jsonObject.addProperty("pass", password.getText().toString());


            new SignIn().execute(jsonObject);
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }


    private class SignIn extends AsyncTask<JsonObject, Void, String> {


        @Override
        protected String doInBackground(JsonObject... objects) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MyApp.JSON, objects[0].toString());

                Log.d("SIDDHARTH", objects[0].toString());

                Request request = new Request.Builder()
                        .url(MyURL.signInURL)
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

            Log.d("VALUE", "response " + s);

            JSONObject object;
            try {
                object = new JSONObject(s);
                boolean success = object.getBoolean("success");

                if (success) {

                    String fName = object.getString("fname");
                    String lName = object.getString("lname");
                    String email = object.getString("email");
                    String authCode = object.getString("authcode");

                    Log.d("DATA", fName + lName + email + authCode);

                    SharedPreferences sharedPref = context.getSharedPreferences(
                            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    JSONObject jsonObject = new JSONObject(s);
                    editor.putBoolean(context.getString(R.string.haveLoggedIn), true);
                    editor.putString(context.getString(R.string.fname), jsonObject.getString("fname"));
                    editor.putString(context.getString(R.string.lanme), jsonObject.getString("lname"));
                    editor.putString(context.getString(R.string.saved_email), jsonObject.getString("email"));
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, CentralActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);


                } else {

                    boolean isPhoneExist = object.getBoolean("phoneExist");
                    boolean isEmailExist = object.getBoolean("emailExist");

                    if (isEmailExist || isPhoneExist) {
                        Toast.makeText(context, "Password is wrong!!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "User does not exist!!", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (NullPointerException e) {
                e.printStackTrace();

                Toast.makeText(context, "NullPointerExcetion", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
