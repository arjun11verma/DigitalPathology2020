package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class FinalUploadView extends BaseView {
    /**
     * Constructor for the base view class
     *
     * @param context Instance of the main activity
     */
    public FinalUploadView(Context context, String message) {
        super(context);

        activity.setContentView(R.layout.final_upload_activity);
        ((TextView)(activity.findViewById(R.id.finalMessage))).setText(message);

        activity.findViewById(R.id.moreImagesBtnFinal).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.getServerConnection().setDone();
                activity.changeView(new ConfirmCameraView(activity));
            }
        });

        activity.findViewById(R.id.finalLogout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.logout();
                activity.changeView(new LoginView(activity));
            }
        });
    }
}
