package csedevelopers.freaky.developers.womensafety;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import csedevelopers.freaky.developers.resources.CheckNet;
import csedevelopers.freaky.developers.resources.MyToast;

public class EmergnecyContacts extends AppCompatActivity {

    private EditText phone1, phone2, phone3, mail1, mail2, mail3;
    private Button save;
    private CheckNet checkNet;
    private SharedPreferences sharedPreferences;

    private String p11, p12, p13, m11, m12, m13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergnecy_contacts);
        phone1 = (EditText) findViewById(R.id.emaill1);
        phone2 = (EditText) findViewById(R.id.emaill2);
        phone3 = (EditText) findViewById(R.id.emaill3);
        mail1 = (EditText) findViewById(R.id.phonee1);
        mail2 = (EditText) findViewById(R.id.phonee2);
        mail3 = (EditText) findViewById(R.id.phonee3);
        save = (Button) findViewById(R.id.save);

        checkNet = new CheckNet(this);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.savior), MODE_PRIVATE);
        p11 = sharedPreferences.getString("p1", "");
        p12 = sharedPreferences.getString("p2", "");
        p13 = sharedPreferences.getString("p3", "");
        m11 = sharedPreferences.getString("m1", "");
        m12 = sharedPreferences.getString("m2", "");
        m13 = sharedPreferences.getString("m3", "");

    }

    @Override
    protected void onStart() {
        super.onStart();

        phone1.setText(p11);
        phone2.setText(p12);
        phone3.setText(p13);
        mail1.setText(m11);
        mail2.setText(m12);
        mail3.setText(m13);
        mail3.clearFocus();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String p1 = phone1.getText().toString();
                String p2 = phone2.getText().toString();
                String p3 = phone3.getText().toString();
                String m1 = mail1.getText().toString();
                String m2 = mail2.getText().toString();
                String m3 = mail3.getText().toString();

                if (phone1.getText().toString().isEmpty() || mail1.getText().toString().isEmpty()) {

                    new MyToast(EmergnecyContacts.this, "One Savior's detail is required!").show();

                } else {

                    sharedPreferences = getSharedPreferences(getResources().getString(R.string.savior), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("p1", p1);
                    editor.putString("p2", p2);
                    editor.putString("p3", p3);
                    editor.putString("m1", m1);
                    editor.putString("m2", m2);
                    editor.putString("m3", m3);
                    editor.apply();

                    new MyToast(EmergnecyContacts.this, "Successfully Saved!").show();

                }
            }
        });

    }
}
