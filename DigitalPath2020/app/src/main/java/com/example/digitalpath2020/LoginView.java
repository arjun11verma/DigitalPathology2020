package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class LoginView extends BaseView {
    private Credentials connectCred;
    private boolean isValid = true;
    private EditText usernameText;
    private EditText passwordText;

    public LoginView(final Context context) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(R.layout.login_activity);

        app = activity.getApp();
        usernameText = activity.findViewById(R.id.username);
        passwordText = activity.findViewById(R.id.password);

        activity.findViewById(R.id.loginBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        activity.findViewById(R.id.createBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeView(new CreateAccountView(activity));
            }
        });
    }

    private void login() {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if(username.isEmpty()) {
            usernameText.setError("Please enter a valid username!");
            isValid = false;
        }

        if(password.isEmpty()) {
            passwordText.setError("Please enter a valid password!");
            isValid = false;
        }

        if(isValid) {
            connectCred = Credentials.emailPassword(username, password);
            app.loginAsync(connectCred, new App.Callback<User>() {
                @Override
                public void onResult(App.Result<User> result) {
                    if (result.isSuccess()) {
                        activity.setUsername(username);
                        activity.changeView(new ConfirmCameraView(activity));
                        System.out.println("Successfully logged into MongoDB. Nice!");
                    }
                    else {
                        System.out.println("You couldn't log in.");
                        String error = "Your username or password is incorrect.";
                        usernameText.setError(error);
                        passwordText.setError(error);
                    }
                }
            });
        }
    }
}
