/**
 * This is the abstract class that all views extend
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.digitalpath2020.MainActivity;
import com.example.digitalpath2020.R;

import io.realm.mongodb.App;

public abstract class BaseView extends View {
    protected MainActivity activity; // Instance of the main activity
    protected App app; // Instance of the MongoDB App

    /**
     * Constructor for the base view class
     * @param context Instance of the main activity
     */
    public BaseView(Context context, int layout) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(layout);
        app = activity.getApp();
    }

    /**
     * Checks if the current user is logged in and redirects them to the login page if not
     */
<<<<<<< HEAD
    public void checkLoggedIn(boolean loginPage) {
        if (app.currentUser() == null && !loginPage) {
            activity.changeView(new LoginView(activity, R.layout.login_activity));
        } else if (loginPage && app.currentUser().getProfile().getEmail() != null) {
            activity.getCurrentUser().setUsername(app.currentUser().getProfile().getEmail());
            activity.changeView(new ConfirmCameraView(activity, R.layout.confirm_camera_activity));
=======
    public void checkLoggedIn() {
        if(!activity.isLoggedIn()) {
            activity.changeView(new LoginView(activity, R.layout.login_activity));
>>>>>>> parent of 0717b92 (Dev Server Url and Refactoring)
        }
    }
}
