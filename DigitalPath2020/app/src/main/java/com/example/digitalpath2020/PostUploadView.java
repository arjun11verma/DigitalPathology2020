package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class PostUploadView extends BaseView {
    public PostUploadView(Context context, String status) {
        super(context);

        checkLoggedIn();

        activity.setContentView(R.layout.post_upload_activity);
        ((TextView)(activity.findViewById(R.id.postTitle))).setText(status);

        activity.findViewById(R.id.logoutBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                app.currentUser().logOutAsync(new App.Callback<User>() {
                    @Override
                    public void onResult(App.Result<User> result) {
                        activity.setLoggedIn(false);
                    }
                });
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
