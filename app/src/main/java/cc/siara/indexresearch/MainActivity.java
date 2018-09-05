package cc.siara.indexresearch;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("native-lib");
    }

    AssetManager mAssets = null;

    private void appendMessageWithEOL(final String msg) {
        appendMessage(msg + "\n");
    }

    private void appendMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText et = (EditText) findViewById(R.id.etOut);
                et.append(msg);
                App.sbOutBuf.append(msg);
                if (msg.contains("Get Time")) {
                    TextView tv = (TextView) findViewById(msg.startsWith("ART") ?
                            R.id.tvARTGet : msg.startsWith("Ix1") ? R.id.tvIDX1Get : R.id.tvIDX2Get);
                    tv.setText(msg.substring(msg.indexOf("Get Time") + 9));
                } else
                if (msg.contains("Insert Time")) {
                    TextView tv = (TextView) findViewById(msg.startsWith("ART") ?
                            R.id.tvARTPut : msg.startsWith("Ix1") ? R.id.tvIDX1Put : R.id.tvIDX2Put);
                    tv.setText(msg.substring(msg.indexOf("Insert Time") + 12));
                }
            }
        });
    }

    BgRunner bgRunner = null;
    PowerManager pm;
    PowerManager.WakeLock wl;
    void initControls() {

        View v;

        mAssets = getAssets();

        v = findViewById(R.id.spDataSel);
        ((Spinner) v).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                App.DATA_SEL = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ((Spinner) v).setSelection(App.DATA_SEL);

        v = findViewById(R.id.spIdx2);
        ((Spinner) v).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                App.IDX2_SEL = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ((Spinner) v).setSelection(App.IDX2_SEL);

        v = findViewById(R.id.spIdx3);
        ((Spinner) v).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                App.IDX3_SEL = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ((Spinner) v).setSelection(App.IDX3_SEL);

        v = findViewById(R.id.etIdxLen);
        ((EditText)v).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    App.IDX_LEN = Integer.parseInt(charSequence.toString());
                } catch (Exception e) { }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        ((EditText) v).setText(String.valueOf(App.IDX_LEN));

        v = findViewById(R.id.etCount);
        ((EditText)v).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    App.NUM_ENTRIES = Integer.parseInt(charSequence.toString());
                } catch (Exception e) { }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        ((EditText) v).setText(String.valueOf(App.NUM_ENTRIES));

        v = findViewById(R.id.spCharset);
        ((Spinner) v).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                App.CHAR_SET = i + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ((Spinner) v).setSelection(App.CHAR_SET - 1);

        v = findViewById(R.id.etKeyLen);
        ((EditText)v).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    App.KEY_LEN = Integer.parseInt(charSequence.toString());
                } catch (Exception e) { }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        ((EditText) v).setText(String.valueOf(App.KEY_LEN));

        v = findViewById(R.id.etValueLen);
        ((EditText)v).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    App.VALUE_LEN = Integer.parseInt(charSequence.toString());
                } catch (Exception e) { }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        ((EditText) v).setText(String.valueOf(App.VALUE_LEN));

        v = findViewById(R.id.ckART);
        ((CheckBox) v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.IS_ART = ((CheckBox) view).isChecked() ? 1 : 0;
            }
        });

        v = findViewById(R.id.btnStart);
        ((Button)v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                //getWindow().setSustainedPerformanceMode(true);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                bgRunner = new BgRunner();
                bgRunner.start();
            }
        });
        if (bgRunner == null) {
            App.sbOutBuf.delete(0, App.sbOutBuf.length());
        } else {
            v.setEnabled(false);
        }

        Thread mainThread = getMainLooper().getThread();
        mainThread.setPriority(Thread.MIN_PRIORITY);
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
        initNative(mAssets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initControls();
    }

    private class BgRunner extends Thread {
        @Override
        public void run() {
            final Button b = (Button) findViewById(R.id.btnStart);
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
            wl.acquire();
            Log.i(App.TAG, "Max Priority:"+getThreadGroup().getMaxPriority());
            setPriority(getThreadGroup().getMaxPriority());
            android.os.Process.setThreadPriority(-20);
            runNative(App.IS_ART, App.DATA_SEL, App.IDX2_SEL,
                    App.IDX3_SEL, App.IDX_LEN, App.NUM_ENTRIES,
                    App.CHAR_SET, App.KEY_LEN, App.VALUE_LEN);
            wl.release();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b.setEnabled(true);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    //getWindow().setSustainedPerformanceMode(false);
                }
            });
            bgRunner = null;
        }
    }

    public native void initNative(AssetManager assets);
    public native void runNative(int isART, int DATA_SEL, int IDX2_SEL, int IDX3_SEL,
                                 int IDX_LEN, long NUM_ENTRIES, int CHAR_SET, int KEY_LEN, int VALUE_LEN);

}
