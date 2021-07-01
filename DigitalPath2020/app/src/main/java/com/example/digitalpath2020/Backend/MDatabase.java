/**
 * This is a class for the MongoDB database connection
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Backend;

import android.app.Application;

import com.example.digitalpath2020.BuildConfig;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

public class MDatabase extends Application {
    private App taskApp; // MongoDB Realm App
    private Realm realm; // MongoDB Realm

    /**
     * Intializes the MongoDB Realm client to be this application
     * Calls on my MongoDB cluster's appID to access the MongoDB Realm
     */
    @Override
    public void onCreate() {
        super.onCreate();
        realm.init(this);
        taskApp = new App(new AppConfiguration.Builder("digitalpathology2020-ecrjr").build());
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL);
        }
    }

    /**
     * Logs the current user out
     */
    public void logout() {
        taskApp.currentUser().logOutAsync(new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                System.out.println(result);
            }
        });
    }

    // Getters for the fields

    public App getTaskApp() {
        return taskApp;
    }
}
