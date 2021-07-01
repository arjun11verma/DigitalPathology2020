/**
 * This is the camera page of the app, where images are taken and displayed.
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.view.View;

import com.example.digitalpath2020.R;

import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;

public class ImageCaptureView extends BaseView {
    /**
     * Constructor for the MainView class
     * Sets the UI to the main page's UI layout (activity_main)
     * Assigns the activity's button action and stop camera methods to the start/stop camera buttons respectively
     * @param context A reference to the instance of the main activity class
     */
    public ImageCaptureView(Context context, int layout) {
        super(context, layout);
        checkLoggedIn(false);

        activity.activateCamera((JavaCamera2View) activity.findViewById(R.id.camera));

        activity.findViewById(R.id.startCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.buttonAction();
            }
        });

        activity.findViewById(R.id.stopTheCameraBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.cancelCamera();
            }
        });
    }
}
