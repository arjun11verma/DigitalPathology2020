package com.example.digitalpath2020.ExternalClasses;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
    private int divider; // Distance for cropping image
    private boolean centered = false; // Determines whether cropping range has been determined or not
    private int aspectWidth = 0, aspectHeight = 0;
    private int stopRowTop = 0, stopRowBottom = 0, stopColLeft = 0, stopColRight = 0; // Cropping ranges

    public static Mat resizeScreen(Mat baseScreen, int aspectWidth, int aspectHeight) {
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

    public Mat processFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mRGBA = inputFrame.rgba();
        if (aspectWidth == 0 && aspectHeight == 0) {
            aspectWidth = mRGBA.width();
            aspectHeight = mRGBA.height();
        }

        Mat mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        if (!centered) defineBoundaries(mRGBAT);
        Imgproc.cvtColor(mRGBAT, mRGBAT, Imgproc.COLOR_BGR2RGB);
        return removeBlackSpace(mRGBAT);
    }

    private void defineBoundaries(Mat inputMat) {
        boolean topFlag = true;
        boolean leftFlag = true;
        boolean bottomFlag = false;
        boolean rightFlag = false;
        stopRowTop = 0;
        stopRowBottom = inputMat.rows();
        stopColLeft = 0;
        stopColRight = inputMat.cols();

        Mat mGRAY = new Mat();
        Imgproc.cvtColor(inputMat, mGRAY, Imgproc.COLOR_RGB2GRAY);

        Mat mBIN = new Mat();
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

    private Mat removeBlackSpace(Mat inputMat) {
        try {
            return inputMat.submat(new Rect(stopColLeft + divider, stopRowTop + divider, stopColRight - stopColLeft - 2*divider, stopRowBottom - stopRowTop - 2*divider));
        } catch (CvException e) {
            return inputMat;
        }
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    public int getAspectWidth() { return aspectWidth; }

    public int getAspectHeight() { return aspectHeight; }
}
