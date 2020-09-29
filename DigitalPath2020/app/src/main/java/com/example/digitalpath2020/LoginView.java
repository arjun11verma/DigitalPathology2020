package com.example.digitalpath2020;

import android.content.Context;
import android.net.wifi.hotspot2.pps.Credential;
import android.view.View;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class LoginView extends View {
    private MainActivity activity;

    public LoginView(Context context) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(R.layout.login_activity);

        Credentials any = Credentials.anonymous();

        activity.getApp().loginAsync(any, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> check) {
                if(check.isSuccess()) {

                }
            }
        });

        activity.findViewById(R.id.goToMain).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new MainView(activity));
            }
        });
    }
}
