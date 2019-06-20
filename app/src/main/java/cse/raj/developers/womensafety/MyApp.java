package csedevelopers.freaky.developers.womensafety;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import okhttp3.MediaType;

/**
 * Created by PURUSHOTAM on 4/5/2017.
 */

public class MyApp extends MultiDexApplication {
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
