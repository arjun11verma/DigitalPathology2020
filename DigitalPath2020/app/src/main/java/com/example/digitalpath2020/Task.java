/**
 * This is a class for the image capture TimerTask
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

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
        activity.focus();
        mRGBA = activity.getBaseFrame();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        removeBlackSpace();
        if(centered) activity.addMat(mRET);
    }

    /**
     * Method that removes the black space that naturally occurs in a microscope image and crops the image into a square
     */
    public void removeBlackSpace() {
        if(!centered) {
            topFlag = true;
            leftFlag = true;
            bottomFlag = false;
            rightFlag = false;
            stopRowTop = 0;
            stopRowBottom = mRGBAT.rows();
            stopColLeft = 0;
            stopColRight = mRGBAT.cols();

            Imgproc.cvtColor(mRGBAT, mGRAY, Imgproc.COLOR_BGR2GRAY);

            Imgproc.threshold(mGRAY, mBIN, 70, 5, Imgproc.THRESH_BINARY);

            for(int i = 0; i < mRGBAT.rows(); i++) {
                if(topFlag && Core.sumElems(mBIN.row(i)).val[0] > 50) {
                    stopRowTop = i;
                    topFlag = false;
                    bottomFlag = true;
                } else if (bottomFlag && Core.sumElems(mBIN.row(i)).val[0] < 50) {
                    stopRowBottom = i;
                    bottomFlag = false;
                }
            }

            for(int i = 0; i < mRGBAT.cols(); i++) {
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

        System.out.println("Top/Bottom " + stopRowTop + " " + stopRowBottom);
        System.out.println("Left/Right " + stopColLeft + " " + stopColRight);
        System.out.println("Divider: " + divider);

        try {
            mRET = mRGBAT.submat(new Rect(stopColLeft + divider, stopRowTop + divider, stopColRight - stopColLeft - 2*divider, stopRowBottom - stopRowTop - 2*divider));
            System.out.println("Correct centering");
        } catch (CvException e) {
            mRET = mRGBAT;
            System.out.println(e);
        }

        //Imgproc.resize(mRET, mRET, mRGBA.size());
    }

    public void resetCentered() {
        centered = false;
    }
}
