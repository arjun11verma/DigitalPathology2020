package com.example.digitalpathology2020;

import android.content.Context;
import android.view.View;

public class StartingView extends BaseView {
    public StartingView(Context context) {
        super(context);
        activity.setContentView(R.layout.starting_layout);

        activity.findViewById(R.id.setCameraView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new CameraView(activity));
            }
        });
    }
}
