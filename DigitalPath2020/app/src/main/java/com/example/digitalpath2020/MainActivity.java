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
    private List<Mat> matList = new ArrayList<Mat>(); // List of processed Mat objects for uploading and displaying
    private CameraBridgeViewBase.CvCameraViewFrame baseFrame;

    private int maxNumImages = 50; // The maximum number of pictures that will be taken
    private int delay = 2000; // Delay until camera starts in milliseconds
    private int period = 2000; // Period of time between each picture being taken
    private Timer timer; // Timer that will control when each picture is being taken
    private Task timerTask; // Task to be executed that will take in and do rudimentary processing on images
    private boolean clicked = false; // Prevents a crash by stopping the button after it has been clicked once

    private MDatabase database; // AWS database to be connected to through the MongoDB client
    private String username; // User data
    private String slide, cancer, name; // User data
    private ServerConnect serverConnection; // Connection to the Python Image Processing Server using the Volley HTTP library

    MainActivity activity = this; // Instance of the main activity to pass to other classes
    private BaseView currentView; // Current page of the app
    private boolean loggedIn = false;

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
        changeView(new LoginView(this));
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
        loggedIn = false;
    }

    /**
     * Determines whether a user is logged in or not
     * @return Boolean representing whether the user is logged in or not
     */
    public boolean isLoggedIn() {
        return (database.getTaskApp().currentUser() != null && database.getTaskApp().currentUser().getProfile().getEmail() != null);
    }

    /**
     * Activates and initializes the camera
     * Sets the JavaCameraView class to take input from the phone's camera
     * @param camera JavaCameraView to be activated
     */
    public void activateCamera(JavaCameraView camera) {
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
            timerTask = new Task(this);
            timerTask.resetCentered();
            timer.schedule(timerTask, delay, period);
            cameraView.enableView();
            clicked = true;
            focus();
        }
    }

    /**
     * Refocuses the Android camera
     */
    public void focus() {
        Camera.Parameters parameters = cameraView.mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        cameraView.mCamera.setParameters(parameters);

        cameraView.mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                System.out.println(success);
            }
        });
    }

    /**
     * Stops the camera and switches the view to the post capture page
     */
    public void stopCamera() {
        timer.cancel(); // stops the timer
        timer.purge(); // makes the timertask stop occuring
        cameraView.disconnectCamera();
        cameraView.disableView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeView(new AfterCaptureView(activity)); // goes to the after capture page after the set number of images has been captured
            }
        });
    }

    /**
     * Resets the button so it is clickable
     */
    public void resetClick() {
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

    /**
     * Processes the inputs of the camera. The raw data from the camera is constantly being streamed into this method in the form of an OpenCV CvCameraViewFrame
     * This method constantly updates a Mat (Image matrix) in the activity class with the current view of the camera
     * @param inputFrame The raw input of the native Android camera
     * @return
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        baseFrame = inputFrame; // updates the system with each frame the android camera captures
        if (matList.size() == (maxNumImages + 1)) // disables the camera after numImages pictures have been taken
        {
            timer.cancel(); // stops the timer
            timer.purge(); // makes the timertask stop occuring
            cameraView.disconnectCamera();
            cameraView.disableView();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeView(new AfterCaptureView(activity)); // goes to the after capture page after the set number of images has been captured
                }
            });
        }
        if (matList.size() > 0) {
            return matList.get(matList.size() - 1);
        } else {
            return baseScreen;
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

    public String getUsername() {
        return username;
    }

    public Realm getRealm() {
        return database.getRealm();
    }

    public String getSlide() {
        return slide;
    }

    public String getName() {
        return name;
    }

    public String getCancer() {
        return cancer;
    }

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

    public ServerConnect getServerConnection() {
        return serverConnection;
    }

    public void setLoggedIn(boolean set) {
        loggedIn = set;
    }
}