package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;

public class PostUploadView extends BaseView {
    public PostUploadView(Context context) {
        super(context);
        activity.setContentView(R.layout.post_upload_activity);

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
                activity.changeView(new MainView(activity));
            }
        });
    }
}
