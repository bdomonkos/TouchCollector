package dlabs.com.touchresearcher;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;


public class MainActivity extends AppCompatActivity{
    SimpleDrawingView drawingView;
    View view;
    TextView textView;
    TextView mesaureTextView;
    Button stopButton;
    private static final int PERMISSION_REQUEST = 1000;
    private int grantResults[];
    public static final String MES_KEY = "MES_KEY";
    int mesID;
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.simpleDrawingView1);
        view =  findViewById(R.id.relativeLayout);
        
        textView = findViewById(R.id.textView);
        mesaureTextView = findViewById(R.id.textView3);
        stopButton = findViewById(R.id.button2);
        DisplayMetrics metrics = new DisplayMetrics();   //for all android versions
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int Measuredwidth  = metrics.widthPixels;
        final int Measuredheight = metrics.heightPixels;
        textView.setText("X:"+Measuredwidth+"Y:"+Measuredheight+"Pressure:0");
        mesaureTextView.setVisibility(View.INVISIBLE);


        SharedPreferences sharedPreferencesr = getSharedPreferences("app",MODE_PRIVATE);
        mesID = sharedPreferencesr.getInt(MES_KEY,0);

        final Modell modell = new Modell(Measuredheight,Measuredwidth,mesID);
        drawingView.setModell(modell);
        modell.setRecord(false);
        drawingView.setActivity(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //if you dont have required permissions ask for it (only required for API 23+)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);


            onRequestPermissionsResult(PERMISSION_REQUEST, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults);
        }



        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modell.isRecord()) {
                    textView.setText("X:"+Measuredwidth+"Y:"+Measuredheight+"Pressure:0");
                    stopButton.setText("Start");
                    mesaureTextView.setVisibility(View.INVISIBLE);

                    modell.setRecord(false);
                    drawingView.clearCanvas();

                    CSVWriter writer = null;
                    try {


                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date currentTime = Calendar.getInstance().getTime();
                        String filename = "/capture_"+simpleDateFormat.format(currentTime)+"_"+String.valueOf(mesID)+".csv";
                        writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory() + filename), ';');
                        String[] header = {"X", "Y", "Pressure", "Actiontype", "AltitudeAngle", "AzimuthAngle", "Time"};
                        writer.writeNext(header);
                        for (Touch touch : modell.getTouches()) {
                            String[] data1 = {touch.getX(), touch.getY(), touch.getPressure(), touch.getActionType(), String.valueOf(0), String.valueOf(0), touch.getTouchTime()};
                            writer.writeNext(data1);
                        }
                        writer.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().getCanonicalName(), e.getLocalizedMessage());
                    }
                }
                else{
                    SharedPreferences.Editor editor = getSharedPreferences("app",MODE_PRIVATE).edit();
                    mesID++;
                    editor.putInt(MES_KEY,mesID);
                    editor.apply();
                    modell.clearModell();
                    modell.setRecord(true);
                    modell.setLatestTouchTime(0);
                    stopButton.setText("Stop");
                    mesaureTextView.setVisibility(View.VISIBLE);
                    mesaureTextView.setText(String.valueOf(mesID));
                }

            }
        });

        int currentApiVersion = Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.

    }

    @Override // android recommended class to handle permissions
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }

            // other 'case' line to check fosr other
            // permissions this app might request
        }


    }

}
