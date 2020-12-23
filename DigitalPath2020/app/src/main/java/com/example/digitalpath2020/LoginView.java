/**
 * This is the login page of the app, where users can log in
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class LoginView extends BaseView {
    private Credentials connectCred; // Login credentials
    private boolean isValid = true; // Boolean determining whether login information is valid or not
    private EditText usernameText; // Text input for username
    private EditText passwordText; // Text input for password

    /**
     * Constructor for the LoginView class
     * Sets the UI to the login layout
     * Sets the login button to the login method and the create account button to a method that redirects the user to the create account page
     * @param context Instance of the main activity
     */
    public LoginView(Context context) {
        super(context);

        if(activity.isLoggedIn()) {
            activity.setUsername(app.currentUser().getProfile().getEmail());
            activity.setLoggedIn(true);
            activity.changeView(new ConfirmCameraView(activity));
        }

        activity.setContentView(R.layout.login_activity);

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

    /**
     * Logs in a user based off of their username and password input. Notifies user if the username/password are invalid
     */
    private void login() {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            usernameText.setError("Please enter a valid username!");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordText.setError("Please enter a valid password!");
            isValid = false;
        }

        if (isValid) {
            connectCred = Credentials.emailPassword(username, password);
            app.loginAsync(connectCred, new App.Callback<User>() {
                @Override
                public void onResult(App.Result<User> result) {
                    if (result.isSuccess()) {
                        activity.setUsername(username);
                        activity.changeView(new ConfirmCameraView(activity));
                        activity.setLoggedIn(true);
                        System.out.println("Successfully logged into MongoDB. Nice!");
                        System.out.println(app.currentUser().getProfile().getEmail());
                    } else {
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
