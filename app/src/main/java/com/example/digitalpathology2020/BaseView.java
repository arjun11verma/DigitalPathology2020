package com.example.digitalpathology2020;

import android.content.Context;
import android.view.View;

public abstract class BaseView extends View {
    protected MainActivity activity;

    public BaseView(Context context) {
        super(context);
        activity = (MainActivity)context;
    }
}
