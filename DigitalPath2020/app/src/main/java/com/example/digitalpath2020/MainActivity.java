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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView cameraView;
    private Mat mRGBA, mRGBAT, mRGBATemp;
    private List<Mat> matList = new ArrayList<Mat>();
    private int pTimer = -1;
    private int TIME_CALIBRATION = 100;
    private int numImages = 5;
    private int TIME_LIMIT = (TIME_CALIBRATION * numImages) + 1;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("MainActivity", "OpenCV loaded successfully");
                    cameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (JavaCameraView) findViewById(R.id.camera);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();
        pTimer += 1; // by adjusting the rate at which I increase pTimer, I can adjust the rate at which I take pictures
        if (pTimer % TIME_CALIBRATION == 0) // takes an image after TIME_CALIBRATION units of time have passed
        {
            mRGBAT = mRGBA.t();
            Core.flip(mRGBA.t(), mRGBAT, 1);
            Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());
            matList.add(mRGBAT);
            mRGBATemp = mRGBAT;
        }
        else if (pTimer > TIME_LIMIT) // disables the camera after numImages pictures have been taken.
        {
            mRGBA.release();
            cameraView.disableView();
            //cameraView.setVisibility(SurfaceView.INVISIBLE);
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        return mRGBATemp;
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
}