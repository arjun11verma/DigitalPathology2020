package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class PostUploadView extends BaseView {
    public PostUploadView(Context context, String status) {
        super(context);
        activity.setContentView(R.layout.post_upload_activity);
        ((TextView)(activity.findViewById(R.id.postTitle))).setText(status);

        activity.findViewById(R.id.logoutBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new LoginView(activity));
            }
        });

        activity.findViewById(R.id.moreImagesBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.getServerConnection().setDone();
                activity.changeView(new ConfirmCameraView(activity));
            }
        });
    }
}
