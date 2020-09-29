package com.example.digitalpath2020;

import android.app.Application;
import android.os.AsyncTask;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class MDatabase extends Application {
    private App taskApp;

    @Override
    public void onCreate() { // this is called immediately when the app begins, and it connects to the MongoDB server
        super.onCreate();
        Realm.init(this); // intializes the client to be this application
        taskApp = new App(new AppConfiguration.Builder("digitalpathology2020-ecrjr").build()); // calls on my cluster's appID to access the MongoDB Realm through the MongoDB server
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL);
        }
    }

    public App getTaskApp() {
        return taskApp;
    }
}
