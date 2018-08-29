package cc.siara.indexresearch;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {

    // Context lives only as long as the application, so leak warning ignored
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static SharedPreferences prefs;
    public static final String TAG = "IDX_RSRCH";

    static int DATA_SEL = 0;
    static int IDX1_SEL = 0;
    static int IDX2_SEL = 0;
    static int IDX3_SEL = 1;
    static int IDX_LEN = 70;
    static long NUM_ENTRIES = 1000000;
    static int CHAR_SET = 2;
    static int KEY_LEN = 8;
    static int VALUE_LEN = 4;
    static int IS_ART = 1;

    static StringBuilder sbOutBuf = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "App created");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "On Terminate called");
    }

    public static Context getAppContext() {
        return context;
    }

}
