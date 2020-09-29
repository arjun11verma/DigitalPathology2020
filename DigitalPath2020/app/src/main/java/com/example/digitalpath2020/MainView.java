package com.example.digitalpath2020;

import android.content.Context;
import android.util.Log;
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

        activity.findViewById(R.id.startCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.buttonAction();
            }
        });

        activity.findViewById(R.id.goToLogin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new LoginView(activity));
            }
        });
    }
}
