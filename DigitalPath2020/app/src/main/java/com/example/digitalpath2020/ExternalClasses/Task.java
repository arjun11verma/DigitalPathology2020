/**
 * This is a class for the image capture TimerTask
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.ExternalClasses;

import com.example.digitalpath2020.MainActivity;
import com.example.digitalpath2020.Views.BaseView;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.TimerTask;

public class Task extends TimerTask {
    private MainActivity activity; // Instance of the main activity
    private int divider; // Distance for cropping image
    private Mat mRGBAT, mRGBA, mGRAY, mBIN, mRET; // Mats to store temporary image data
    private boolean centered = false; // Determines whether cropping range has been determined or not
    private int stopRowTop = 0, stopRowBottom = 0, stopColLeft = 0, stopColRight = 0; // Cropping ranges
    private boolean topFlag, bottomFlag, leftFlag, rightFlag; // Booleans to determine when cropping ranges have been determined

    /**
     * Constructor for the Task class
     * @param activity Instnace of the main activity
     */
    public Task(MainActivity activity, BaseView uiCameraView) {
        this.activity = activity;
        mGRAY = new Mat();
        mBIN = new Mat();
    }

    /**
     * Run method inherited from the TimerTask class, called by the timer
     * Processes images and adds them to the image list in the main activity
     */
    @Override
    public void run() {
        activity.imageReady = true;
    }

    public void resetCentered() {
        centered = false;
    }
}
