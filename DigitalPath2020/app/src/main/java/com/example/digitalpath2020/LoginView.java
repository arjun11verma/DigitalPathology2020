package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;

public class LoginView extends View {
    private MainActivity activity;

    public LoginView(Context context) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(R.layout.login_activity);
    }
}
