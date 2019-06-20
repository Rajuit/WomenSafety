package csedevelopers.freaky.developers.resources;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import csedevelopers.freaky.developers.womensafety.R;

/**
 * Created by PURUSHOTAM on 7/8/2017.
 */

public class MyToast {

    private final Context context;
    private Toast toast;

    public MyToast(Context context, String message) {
        this.context = context;
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = inflater.inflate(R.layout.toast_layout, null);
        TextView textView = (TextView) v1.findViewById(R.id.toast_message);
        textView.setText(message);
        toast.setView(v1);

    }

    public MyToast setGravity(int i) {
        toast.setGravity(i, 0, 0);
        return this;
    }

    public MyToast setDuration(int duration) {
        toast.setDuration(duration);
        return this;
    }

    public void show() {
        toast.show();
    }


}
