/**
 * This is the main activity class, it serves as the "base" of the app. This activity is the base of the app itself, and displays all of the UI components
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.example.digitalpath2020.Backend.MDatabase;
import com.example.digitalpath2020.Backend.ServerConnect;
import com.example.digitalpath2020.ExternalClasses.CameraTimer;
import com.example.digitalpath2020.ExternalClasses.ImageProcessor;
import com.example.digitalpath2020.ExternalClasses.Patient;
import com.example.digitalpath2020.Views.AfterCaptureView;
import com.example.digitalpath2020.Views.BaseView;
import com.example.digitalpath2020.Views.ConfirmCameraView;
import com.example.digitalpath2020.Views.LoginView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

import io.realm.mongodb.App;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCamera2View cameraView; // View that will be accessing the camera, taking pictures and displaying them
    private List<Mat> matList; // List of processed Mat objects for uploading and displaying
    private Patient currentUser;

    private CameraTimer ImageTimer;
    private ImageProcessor ImgProc;

    private MDatabase database; // AWS database to be connected to through the MongoDB client
    private ServerConnect serverConnection; // Connection to the Python Image Processing Server using the Volley HTTP library

    private String imageCurrentTime;
    private Mat baseMat;
    MainActivity activity = this; // Instance of the main activity to pass to other classes

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
        this.matList = new ArrayList<Mat>(100);
        this.currentUser = new Patient();
        this.ImgProc = new ImageProcessor();
        this.ImageTimer = new CameraTimer(2000, 3000);
        this.database = new MDatabase();
        this.database.onCreate();
        this.serverConnection = new ServerConnect(this);
        changeView(new LoginView(this, R.layout.login_activity));
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
        ImgProc.setCentered(false);
        ImageTimer.resetTimer();
        cameraView.enableView();
        matList.clear();
    }

    /**
     * Stops the camera and switches the view to the post capture page
     */
    public void cancelCamera() {
        ImageTimer.disableTimer();
        cameraView.disableView();

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
        if (ImageTimer.getImageReady()) {
            //baseMat = ImgProc.processFrame(inputFrame);
            System.out.println("Timer activated!");
            baseMat = inputFrame.rgba();
            matList.add(baseMat);
            ImageTimer.setImageReady(false);
        }
        return (baseMat == null) ? baseMat : ImageProcessor.resizeScreen(baseMat.clone(), ImgProc.getAspectWidth(), ImgProc.getAspectHeight());
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

    public void changeView(BaseView currentView) {}

    // Getters and Setters for the fields
    public App getApp() {
        return database.getTaskApp();
    }

    public List<Mat> getMatList() {
        return matList;
    }

    public ServerConnect getServerConnection() {
        return serverConnection;
    }

    public Patient getCurrentUser() {
        return currentUser;
    }

    public String getImageCurrentTime() {
        return imageCurrentTime;
    }

    public void setImageCurrentTime(String imageCurrentTime) {
        this.imageCurrentTime = imageCurrentTime;
    }
}