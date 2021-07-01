package com.example.digitalpath2020.ExternalClasses;

import java.util.Timer;
import java.util.TimerTask;

public class CameraTimer {
    private int delay = 3000; // Delay until camera starts in milliseconds
    private int period = 3000; // Period of time between each picture being taken
    private Timer timer; // Timer that will control when each picture is being taken
    private TimerTask timerTask; // Task to be executed that will take in and do rudimentary processing on images
    private boolean imageReady = false;

    private class InternalTask extends TimerTask {
        private CameraTimer timerInstance;

        public InternalTask(CameraTimer timerInstance) {
            this.timerInstance = timerInstance;
        }

        @Override
        public void run() {
            this.timerInstance.imageReady = true;
        }
    }

    public CameraTimer(int delay, int period) {
        this.delay = delay;
        this.period = period;
        this.timerTask = new InternalTask(this);

        this.timer = new Timer();
        this.timer.schedule(this.timerTask, this.delay, this.period);
    }

    public void disableTimer() {
        this.timer.cancel();
        this.timer.purge();
    }

    public void resetTimer() {
        this.timer = new Timer();
        this.timerTask = new InternalTask(this);
        this.timer.schedule(this.timerTask, this.delay, this.period);
        this.imageReady = false;
    }

    public boolean getImageReady() {
        return imageReady;
    }

    public void setImageReady(boolean imageReady) {
        this.imageReady = imageReady;
    }
}
