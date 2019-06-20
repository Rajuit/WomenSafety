package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import csedevelopers.freaky.developers.resources.MyToast;

public class AutoCall extends AppCompatActivity {

    private Switch aSwitch;
    private EditText phone;
    private String phoneNo;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String autocallPhoneNo;
    private boolean isEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_call);
        aSwitch = (Switch) findViewById(R.id.isenable);
        phone = (EditText) findViewById(R.id.phone11);
        this.context = this;
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE);
        autocallPhoneNo = sharedPreferences.getString("autocall", "");
        isEnabled = sharedPreferences.getBoolean("isEnable", false);

        aSwitch.setChecked(isEnabled);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!autocallPhoneNo.isEmpty()) {
            phone.setText(autocallPhoneNo);
        }


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                phoneNo = phone.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (phoneNo.length() > 2 && isChecked) {
                    new MyToast(context, "Auto Call Enabled!!").setGravity(Gravity.BOTTOM).setDuration(Toast.LENGTH_SHORT).show();
                    editor.putString("autocall", phoneNo);
                    editor.putBoolean("isEnable", true);
                    editor.apply();

                } else {
                    new MyToast(context, "Please Enter Emergency No!!").setGravity(Gravity.BOTTOM).setDuration(Toast.LENGTH_SHORT).show();
                    aSwitch.setChecked(false);
                    editor.putBoolean("isEnable", false);
                    editor.apply();
                }


            }
        });


        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                aSwitch.setChecked(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}
