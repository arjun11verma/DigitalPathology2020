package com.example.digitalpath2020;

import android.hardware.Camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.TimerTask;

public class Task extends TimerTask {
    private MainActivity activity;
    private int divider;
    private Mat mRGBAT, mRGBA, mGRAY, mBIN, mRET;
    private boolean centered = false;
    private int stopRowTop = 0, stopRowBottom = 0, stopColLeft = 0, stopColRight = 0;
    private boolean topFlag, bottomFlag, leftFlag, rightFlag;

    public Task(MainActivity activity) {
        this.activity = activity;
        mGRAY = new Mat();
        mBIN = new Mat();
    }

    public void removeBlackSpace() {
        if(!centered) {
            topFlag = true;
            leftFlag = true;

            Imgproc.cvtColor(mRGBAT, mGRAY, Imgproc.COLOR_BGR2GRAY);

            Imgproc.threshold(mGRAY, mBIN, 30, 1, Imgproc.THRESH_BINARY);

            for(int i = 0; i < 720; i++) {
                if(topFlag && Core.sumElems(mBIN.row(i)).val[0] > 50) {
                    stopRowTop = i;
                    topFlag = false;
                    bottomFlag = true;
                } else if (bottomFlag && Core.sumElems(mBIN.row(i)).val[0]  < 10) {
                    stopRowBottom = i;
                    bottomFlag = false;
                }

                if(leftFlag && Core.sumElems(mBIN.col(i)).val[0] > 50) {
                    stopColLeft = i;
                    leftFlag = false;
                    rightFlag = true;
                } else if (rightFlag && Core.sumElems(mBIN.col(i)).val[0]  < 10) {
                    stopColRight = i;
                    rightFlag = false;
                }
            }

            divider = (int)(((stopColRight - stopColLeft)*(2 - 1.412))/4);
            centered = true;
        }

        mRET = new Mat(mRGBAT, (new Rect(stopColLeft + divider, stopRowTop + divider, stopColRight - stopColLeft - 2*divider, stopRowBottom - stopRowTop - 2*divider)));
        Imgproc.resize(mRET, mRET, mRGBA.size());
    }

    @Override
    public void run() {
        activity.focus();

        mRGBA = activity.getBaseFrame().rgba();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());

        //removeBlackSpace();

        //activity.addMat(mRET);
        activity.addMat(mRGBAT);
    }
}
