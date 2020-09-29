package com.example.digitalpath2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.mongodb.App;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView cameraView;
    private Mat mRGBA;
    private List<Mat> matList = new ArrayList<Mat>();
    private int pTimer = 0; // measures the amount of pictures that have been taken
    private int maxNumImages = 5; // the number of pictures that will be taken
    private int delay = 1000; // delay until camera starts in milliseconds
    private int period = 5000; // period of time between each picture being taken
    private boolean clicked = false;
    private Timer timer = new Timer();
    private Task timerTask = new Task(this);
    private MDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB(); // ONLY CALL THIS ONCE!! NEVER DO IT AGAIN!!!
        changeView(new LoginView(this));
    }

    private void initDB() {
        database = new MDatabase(); // creates an instance of the MongoDB Application class
        database.onCreate();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("MainActivity", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public void activateCamera(JavaCameraView camera) {
        cameraView = camera;
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this); // sets the cameraview to take input from the android camera
        matList.add(mRGBA);
    }

    public void buttonAction() {
        if(!clicked) {
            timer.schedule(timerTask, delay, period); // activates the timer and schedules the task
            cameraView.enableView(); // activates the camera
            clicked = true; // prevents the button from being clicked multiple times and crashing the system
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba(); // updates the system with each frame the android camera captures
        if (pTimer > (maxNumImages)) // disables the camera after numImages pictures have been taken.
        {
            timer.cancel(); // stops the timer
            timer.purge(); // makes the timertask stop occuring
            cameraView.disableView(); // disables the camera
        }
        return matList.get(pTimer);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView(); // stops the camera if it is paused
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("MainActivity", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView(); // stops the camera if the view is removed
        }
    }

    public void addMat(Mat mat) {
        matList.add(mat);
    }

    public Mat getmRGBA() {
        return mRGBA;
    }

    public void setpTimer(int x) {
        pTimer = x;
    }

    public void changeView(View v) {}

    public App getApp() {return database.getTaskApp(); }
}