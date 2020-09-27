package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import org.opencv.android.JavaCameraView;

public class MainView extends View {
    private MainActivity activity;

    public MainView(Context context) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(R.layout.activity_main);
        activity.activateCamera((JavaCameraView)activity.findViewById(R.id.camera));

        Button startCamera = (Button)activity.findViewById(R.id.startCamera);
        startCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.buttonAction();
            }
        });
    }
}
