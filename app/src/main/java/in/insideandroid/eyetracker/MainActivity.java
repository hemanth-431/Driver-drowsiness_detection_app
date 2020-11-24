package in.insideandroid.eyetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout background;
    TextView user_message;
    private static final long START_TIME_IN_MILLIS = 5000;
    private TextView mTextViewCountDown,step;
    private  MediaPlayer mediaPlayerc;
    Button toast;
    Dialog epicdialog;
    Button yes,no;
    ImageView close;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    boolean flag = false;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
   private String con="no";
    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        epicdialog=new Dialog(this);


step=findViewById(R.id.steps123);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent!= null){
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude-MagnitudePrevious;
                    MagnitudePrevious = Magnitude;
                    System.out.println(x_acceleration+"**** "+y_acceleration+" *****"+z_acceleration+" *****"+Magnitude+" ******"+MagnitudeDelta);
                    if (MagnitudeDelta > 10){
                        stepCount++;
                        step.setText(stepCount.toString());
con="yes";
if(stepCount > 10){
    stepCount=1;
    showpopup();
    Toast.makeText(MainActivity.this,"Are You a Passenger"+MagnitudeDelta+" "+stepCount,Toast.LENGTH_LONG).show();

}
                        step.setText(stepCount.toString());
                  //        Toast.makeText(MainActivity.this," "+MagnitudeDelta+" "+stepCount,Toast.LENGTH_LONG).show();
                    }else {

                        con="no";
                    }

                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
       // sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        mediaPlayerc=MediaPlayer.create(MainActivity.this,R.raw.real);
toast=findViewById(R.id.Toast);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n Grant permission and restart app", Toast.LENGTH_SHORT).show();
        }else{
            init();
        }
        updateCountDownText();

toast.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
     //   Intent i=new Intent(MainActivity.this,LivePreviewActivity.class);
    //    startActivity(i);
        mediaPlayerc.start();
  /*   try{   showpopup();}catch (Exception e){
         Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
     }
       // Toast.makeText(MainActivity.this,"Hi",Toast.LENGTH_LONG).show();

   */
    }




});

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void showpopup() {
        epicdialog.setContentView(R.layout.popup);
        close=epicdialog.findViewById(R.id.close);
        yes=epicdialog.findViewById(R.id.yes);
        no=epicdialog.findViewById(R.id.No);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicdialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicdialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicdialog.dismiss();
            }
        });
        epicdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicdialog.show();

    }
    private void init() {
        background = findViewById(R.id.background);
        user_message = findViewById(R.id.user_text);
        flag = true;

        initCameraSource();

        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimerRunning) {
                    Toast.makeText(MainActivity.this,"Reset Timer",Toast.LENGTH_LONG).show();
                    pauseTimer();
                    resetTimer();
              //      mediaPlayerc.stop();
                } else {
                    Toast.makeText(MainActivity.this,"Start Timer",Toast.LENGTH_LONG).show();
                    startTimer();
        //     mediaPlayerc.start();
                    //Toast.makeText(MainActivity.this,"Start Timer  "+mTimerRunning,Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;

            }
        }.start();
        mTimerRunning = true;
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;

    }
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();

    }
    private void updateCountDownText() {

        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%d", seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
if(timeLeftFormatted.equals("0")){



if(con.equals("yes")){
    mediaPlayerc.start();
}else {
    showpopup();
    Toast.makeText(MainActivity.this,"Dont't Sleep while You Drive",Toast.LENGTH_LONG).show();
}

       // Toast.makeText(MainActivity.this,stepCount.toString()+" "+con,Toast.LENGTH_LONG).show();
    resetTimer();
}
    }


    //method to create camera source from faceFactoryDaemon class
    private void initCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(MainActivity.this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource!=null) {
            cameraSource.stop();
        }

        setBackgroundGrey();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource!=null) {
            cameraSource.release();
        }
    }

    //update view
    public void updateMainView(Condition condition){
        switch (condition){
            case USER_EYES_OPEN:
                setBackgroundGreen();
                user_message.setText("Open eyes detected");
                break;
            case USER_EYES_CLOSED:
                setBackgroundOrange();
                user_message.setText("Close eyes detected");
                break;
            case FACE_NOT_FOUND:
                setBackgroundRed();
                user_message.setText("User not found");
                break;
            default:
                setBackgroundGrey();
                user_message.setText("Hello World");
        }
    }



    //set background Grey
    private void setBackgroundGrey() {
        if(background != null)
            background.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    //set background Green
    private void setBackgroundGreen() {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 try{
                     if(mTimerRunning)
                     {
                    //    mediaPlayerc.stop();
                         pauseTimer();
                    resetTimer();}}catch (Exception e){

                 }
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            });
        }
    }

    //set background Orange
    private void setBackgroundOrange() {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mTimerRunning)
                    {  startTimer();}
                  //  Toast.makeText(MainActivity.this,"hi",Toast.LENGTH_LONG).show();
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            });
        }
    }

    //set background Red
    private void setBackgroundRed() {
       // Toast.makeText(MainActivity.this,"hi",Toast.LENGTH_LONG).show();
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
              try{    if(mTimerRunning){
                //  mediaPlayerc.stop();
                  pauseTimer();
                    resetTimer();}}catch (Exception e){

              }
                    background.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            });
        }
    }
}
