package com.example.digitalpath2020;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.TimerTask;

public class Task extends TimerTask {
    private int timer = 0; // measures how many times "run" has been ran
    private MainActivity activity;
    private Mat mRGBAT, mRGBA;

    public Task(MainActivity activity) {
        this.activity = activity;
    }

    public int getTimer() {
        return timer;
    }

    public void setmRGBA(Mat m) {
        mRGBA = m;
    }

    public Mat getmRGBA() {
        return mRGBA;
    }

    @Override
    public void run() {
        timer += 1;
        mRGBA = activity.getmRGBA();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());
        activity.setpTimer(timer);
        activity.addMat(mRGBAT);
    }
}
