package com.example.digitalpath2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import io.realm.Realm;
import io.realm.mongodb.App;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView cameraView; // View that will be accessing the camera, taking pictures and displaying them
    private Mat baseScreen; // Mat objects that will act as temporary storage for camera data
    private List<Mat> matList = new ArrayList<Mat>(); // List of processed Mat objects for uploading
    private CameraBridgeViewBase.CvCameraViewFrame baseFrame;

    private int pTimer = 0; // measures the amount of pictures that have been taken
    private int maxNumImages = 50; // the maximum number of pictures that will be taken
    private int delay = 2000; // delay until camera starts in milliseconds
    private int period = 5000; // period of time between each picture being taken
    private Timer timer;
    private Task timerTask; // task to be executed that will take in and do rudimentary processing on images
    private boolean clicked = false; // prevents a crash by stopping the button after it has been clicked once

    private MDatabase database; // AWS database to be connected to through the MongoDB client
    private String username; // user data
    private String slide, cancer, name; // slide image/user data

    MainActivity activity = this;
    private BaseView currentView; // current page of the app
    private boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB();
        if(check) {
            changeView(new LoginView(this));
        }
    }

    private void initDB() {
        database = new MDatabase(); // creates an instance of the MongoDB Application class
        database.onCreate(); // connects to the AWS database cluster through our app's MongoDB client
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) { // loads the OpenCV library
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

    public void activateCamera(JavaCameraView camera) { // activates and initializes the camera
        cameraView = camera;
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this); // sets the cameraview to take input from the android camera
    }

    public void buttonAction() { // begins to take pictures
        if(!clicked) {
            matList.clear();
            timer = new Timer();
            timerTask = new Task(this);
            timer.schedule(timerTask, delay, period); // activates the timer and schedules the task
            cameraView.enableView(); // activates the camera
            clicked = true; // prevents the button from being clicked multiple times and crashing the system
        }
    }

    public void stopCamera() {
        timer.cancel(); // stops the timer
        timer.purge(); // makes the timertask stop occuring
        cameraView.disconnectCamera();
        cameraView.disableView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                check = false;
                changeView(new AfterCaptureView(activity)); // goes to the after capture page after the set number of images has been captured
            }
        });
    }

    public void resetClick()
    {
        clicked = false;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        baseFrame = inputFrame; // updates the system with each frame the android camera captures
        if (matList.size() == (maxNumImages + 1)) // disables the camera after numImages pictures have been taken.
        {
            timer.cancel(); // stops the timer
            timer.purge(); // makes the timertask stop occuring
            cameraView.disconnectCamera();
            cameraView.disableView();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    check = false;
                    changeView(new AfterCaptureView(activity)); // goes to the after capture page after the set number of images has been captured
                }
            });
        }
        if(matList.size() > 0) {
            return matList.get(pTimer);
        }
        else {
            return baseScreen;
        }
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

    // getters and setters for the fields

    public void addMat(Mat mat) {
        matList.add(mat);
    }

    public CameraBridgeViewBase.CvCameraViewFrame getBaseFrame() {
        return baseFrame;
    }

    public void setpTimer(int x) {
        pTimer = x;
    }

    public void changeView(BaseView v) { currentView = v; }

    public App getApp() {return database.getTaskApp(); }

    public List<Mat> getMatList() { return matList; }

    public String getUsername() { return username; }

    public Realm getRealm() {
        return database.getRealm();
    }

    public String getSlide() { return slide; }

    public String getName() { return name; }

    public String getCancer() { return cancer; }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCancer(String cancer) {
        this.cancer = cancer;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}