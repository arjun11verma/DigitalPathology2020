package com.example.digitalpathology2020;

import android.content.Context;
import android.view.View;

public class CameraView extends BaseView {
    public CameraView(final Context context) {
        super(context);
        activity.setContentView(R.layout.camera_layout);

        activity.findViewById(R.id.startCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setCamera();
            }
        });

        activity.findViewById(R.id.seePictureBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new PictureLayoutView(context));
            }
        });

    }
}
