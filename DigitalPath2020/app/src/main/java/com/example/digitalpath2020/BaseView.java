package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;

import io.realm.mongodb.App;

public abstract class BaseView extends View {
    protected MainActivity activity;
    protected App app;

    public BaseView(Context context) {
        super(context);
        activity = (MainActivity)context;
        app = activity.getApp();
    }
}
