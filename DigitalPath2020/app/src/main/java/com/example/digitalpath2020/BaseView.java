/**
 * This is the abstract class that all views extend
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;

import io.realm.mongodb.App;

public abstract class BaseView extends View {
    protected MainActivity activity; // Instance of the main activity
    protected App app; // Instance of the MongoDB App

    /**
     * Constructor for the base view class
     * @param context Instance of the main activity
     */
    public BaseView(Context context) {
        super(context);
        activity = (MainActivity)context;
        app = activity.getApp();
    }

    /**
     * Checks if the current user is logged in and redirects them to the login page if not
     */
    public void checkLoggedIn() {
        if(!activity.isLoggedIn()) {
            activity.changeView(new LoginView(activity));
        }
    }
}
