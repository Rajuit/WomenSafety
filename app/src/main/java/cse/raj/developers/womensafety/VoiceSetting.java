package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import csedevelopers.freaky.developers.resources.MyToast;

public class VoiceSetting extends AppCompatActivity {

    private Switch aSwitch;
    private EditText time;
    private int timeduration;
    private Context context;
    private SharedPreferences sharedPreferences;
    private boolean isEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_setting);

        aSwitch = (Switch) findViewById(R.id.isenable);
        time = (EditText) findViewById(R.id.time11);
        this.context = this;
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE);
        timeduration = sharedPreferences.getInt("autovoice", 30);
        isEnable = sharedPreferences.getBoolean("isVoiceEnable", false);

        aSwitch.setChecked(isEnable);
    }

    @Override
    protected void onStart() {
        super.onStart();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                timeduration = Integer.parseInt(time.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (timeduration > 29 && isChecked) {
                    new MyToast(context, "Auto Call Enabled!!").setGravity(Gravity.BOTTOM).setDuration(Toast.LENGTH_SHORT).show();
                    editor.putInt("autovoicetime", timeduration);
                    editor.putBoolean("isVoiceEnable", true);
                    editor.apply();

                } else {
                    new MyToast(context, "Please Enter time!!").setGravity(Gravity.BOTTOM).setDuration(Toast.LENGTH_SHORT).show();
                    aSwitch.setChecked(false);
                    editor.putBoolean("isVoiceEnable", false);
                    editor.apply();
                }


            }
        });

    }
}
