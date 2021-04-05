/**
 * This is the main activity class, it serves as the "base" of the app. This activity is the base of the app itself, and displays all of the UI components
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.example.digitalpath2020.Backend.MDatabase;
import com.example.digitalpath2020.Backend.ServerConnect;
import com.example.digitalpath2020.ExternalClasses.Patient;
import com.example.digitalpath2020.ExternalClasses.Task;
import com.example.digitalpath2020.Views.AfterCaptureView;
import com.example.digitalpath2020.Views.BaseView;
import com.example.digitalpath2020.Views.ConfirmCameraView;
import com.example.digitalpath2020.Views.LoginView;
import com.example.digitalpath2020.Views.MainView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import io.realm.Realm;
import io.realm.mongodb.App;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCamera2View cameraView; // View that will be accessing the camera, taking pictures and displaying them
    private Mat baseScreen; // Mat objects that will act as temporary storage for camera data
    private List<Mat> matList = new ArrayList<Mat>(100); // List of processed Mat objects for uploading and displaying
    private CameraBridgeViewBase.CvCameraViewFrame baseFrame;
    private int aspectWidth, aspectHeight, prev = 0;

    private int delay = 3000; // Delay until camera starts in milliseconds
    private int period = 3000; // Period of time between each picture being taken
    private Timer timer; // Timer that will control when each picture is being taken
    private Task timerTask; // Task to be executed that will take in and do rudimentary processing on images
    private boolean clicked = false; // Prevents a crash by stopping the button after it has been clicked once

    private MDatabase database; // AWS database to be connected to through the MongoDB client
    private Patient currentUser = new Patient();
    private ServerConnect serverConnection; // Connection to the Python Image Processing Server using the Volley HTTP library

    private int divider; // Distance for cropping image
    private Mat mRGBAT, mRGBA, mGRAY, mBIN, mRET; // Mats to store temporary image data
    private boolean centered = false; // Determines whether cropping range has been determined or not
    private int stopRowTop = 0, stopRowBottom = 0, stopColLeft = 0, stopColRight = 0; // Cropping ranges
    private boolean topFlag, bottomFlag, leftFlag, rightFlag; // Booleans to determine when cropping ranges have been determined

    public boolean imageReady = false;

    MainActivity activity = this; // Instance of the main activity to pass to other classes
    private BaseView currentView; // Current page of the app

    private String devServerUrl; // for development purposes only

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) { // Connects to and loads the OpenCV Library
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

    /**
     * This method is called when the app is opened and it builds the main activity
     * This method instantiates the database connection as well as the server connection, and it then checks if the user is currently logged in
     * It then redirects a user to either the login page or the main app page
     *
     * @param savedInstanceState Saved state of the app such that the state of the app remains the same if closed then opened
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB();
        serverConnection = new ServerConnect(this);
        changeView(new MainView(this, R.layout.activity_main));
    }

    /**
     * Creates an instance of the MongoDB Application class
     * Connects to the DigitalPath2020 database through the Android Studio MongoDB API
     */
    private void initDB() {
        database = new MDatabase();
        database.onCreate();
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        database.logout();
    }

    /**
     * Activates and initializes the camera
     * Sets the JavaCameraView class to take input from the phone's camera
     * @param camera JavaCameraView to be activated
     */
    public void activateCamera(JavaCamera2View camera) {
        cameraView = camera;
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);
    }

    /**
     * Activates the timer and schedules the task to take pictures
     */
    public void buttonAction() {
        if (!clicked) {
            matList.clear();
            timer = new Timer();
            timerTask = new Task(this, currentView);
            timerTask.resetCentered();
            timer.schedule(timerTask, delay, period);
            cameraView.enableView();

            clicked = true;
            baseScreen = null;
            prev = 0;
        }
    }

    /**
     * Stops the camera and switches the view to the post capture page
     */
    public void cancelCamera() {
        try {
            timer.cancel(); // stops the timer
            timer.purge(); // makes the timertask stop occuring
            onDestroy();
        } catch (NullPointerException nullException) {
            System.out.println(nullException);
        }

        if (matList.size() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeView(new ConfirmCameraView(activity, R.layout.confirm_camera_activity)); // goes to the after capture page after the set number of images has been captured
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeView(new AfterCaptureView(activity, R.layout.after_capture_activity)); // goes to the after capture page after the set number of images has been captured
                }
            });
        }
    }

    /**
     * Resizes Mat to fit phone screen
     *
     * @param baseScreen Input Mat
     * @return Resized Mat
     */
    private Mat resizeScreen(Mat baseScreen, int aspectWidth, int aspectHeight) {
        double scaleFactor = Math.min(aspectWidth/baseScreen.size().width, aspectHeight/baseScreen.size().height);
        Imgproc.resize(baseScreen, baseScreen, new Size(baseScreen.size().width * scaleFactor, baseScreen.size().height * scaleFactor));

        int top = 0, bottom = 0, left = 0, right = 0;
        if(baseScreen.size().width < aspectWidth) {
            left = (int)((aspectWidth - baseScreen.size().width)/2); right = left;
            right += aspectWidth - (right + left + baseScreen.size().width);
        } else {
            top = (int)((aspectHeight - baseScreen.size().height)/2); bottom = top;
            top += aspectHeight - (top + bottom + baseScreen.size().height);
        }

        Core.copyMakeBorder(baseScreen, baseScreen, top, bottom, left, right, Core.BORDER_CONSTANT);

        return baseScreen;
    }

    /**
     * Resets the button so it is clickable
     */
    public void resetClick() {
        matList.clear();
        clicked = false;
    }

    /**
     * Called when the camera is started
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    /**
     * Called when the camera is stopped
     */
    @Override
    public void onCameraViewStopped() {

    }

    private void processFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (prev == 0) {
            aspectWidth = inputFrame.rgba().width();
            aspectHeight = inputFrame.rgba().height();
        }

        if(imageReady && inputFrame != null) {
            mRGBA = inputFrame.rgba();
            mRGBAT = mRGBA.t();
            Core.flip(mRGBA.t(), mRGBAT, 1);
            Imgproc.cvtColor(mRGBAT, mRGBAT, Imgproc.COLOR_BGR2RGB);
            removeBlackSpace();
            addMat(mRET);
            imageReady = false;
        }
    }

    /**
     * Processes the inputs of the camera. The raw data from the camera is constantly being streamed into this method in the form of an OpenCV CvCameraViewFrame
     * This method constantly updates a Mat (Image matrix) in the activity class with the current view of the camera
     * @param inputFrame The raw input of the native Android camera
     * @return
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        processFrame(inputFrame);

        if (matList.size() > prev) {
            prev++;
            baseScreen = resizeScreen(matList.get(matList.size() - 1).clone(), aspectWidth, aspectHeight);
        }

        return baseScreen;
    }

    /**
     * Defines the black space boundaries of the image
     */
    private void defineBoundaries() {
        topFlag = true;
        leftFlag = true;
        bottomFlag = false;
        rightFlag = false;
        stopRowTop = 0;
        stopRowBottom = mRGBAT.rows();
        stopColLeft = 0;
        stopColRight = mRGBAT.cols();

        mGRAY = new Mat();
        Imgproc.cvtColor(mRGBAT, mGRAY, Imgproc.COLOR_RGB2GRAY);

        mBIN = new Mat();
        Imgproc.threshold(mGRAY, mBIN, 50, 5, Imgproc.THRESH_BINARY);

        for(int i = 0; i < mBIN.rows(); i++) {
            if(topFlag && Core.sumElems(mBIN.row(i)).val[0] > 50) {
                stopRowTop = i;
                topFlag = false;
                bottomFlag = true;
            } else if (bottomFlag && Core.sumElems(mBIN.row(i)).val[0] < 50) {
                stopRowBottom = i;
                bottomFlag = false;
            }
        }

        for(int i = 0; i < mBIN.cols(); i++) {
            if(leftFlag && Core.sumElems(mBIN.col(i)).val[0] > 50) {
                stopColLeft = i;
                leftFlag = false;
                rightFlag = true;
            } else if (rightFlag && Core.sumElems(mBIN.col(i)).val[0] < 50) {
                stopColRight = i;
                rightFlag = false;
            }
        }

        divider = (int)(((stopColRight - stopColLeft)*(2 - 1.412))/4);
        centered = true;
    }

    /**
     * Method that removes the black space that naturally occurs in a microscope image and crops the image into a square
     */
    public void removeBlackSpace() {
        if(!centered) defineBoundaries();

        try {
            mRET = mRGBAT.submat(new Rect(stopColLeft + divider, stopRowTop + divider, stopColRight - stopColLeft - 2*divider, stopRowBottom - stopRowTop - 2*divider));
            System.out.println("Correct centering");
        } catch (CvException e) {
            mRET = mRGBAT;
            System.out.println(e);
        }
    }

    /**
     * Inherited method
     * @param hasCapture
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Called when camera is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView(); // stops the camera if it is paused
        }
    }

    /**
     * Called when camera is resumed
     */
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

    /**
     * Called when camera is destroyed/view layout changes
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView(); // stops the camera if the view is removed
        }
    }

    // Getters and Setters for the fields

    public void addMat(Mat mat) {
        matList.add(mat);
    }

    public CameraBridgeViewBase.CvCameraViewFrame getBaseFrame() {
        return baseFrame;
    }

    public void changeView(BaseView v) {
        currentView = v;
    }

    public App getApp() {
        return database.getTaskApp();
    }

    public List<Mat> getMatList() {
        return matList;
    }

    public Realm getRealm() {
        return database.getRealm();
    }

    public ServerConnect getServerConnection() {
        return serverConnection;
    }

    public Patient getCurrentUser() {
        return currentUser;
    }

    public void setDevServerUrl(String devServerUrl) {
        this.devServerUrl = devServerUrl;
    }

    public String getDevServerUrl() {
        return devServerUrl;
    }
}