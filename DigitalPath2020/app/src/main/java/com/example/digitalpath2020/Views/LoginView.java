/**
 * This is the login page of the app, where users can log in
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.digitalpath2020.R;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class LoginView extends BaseView implements FormFillable {
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
    public LoginView(Context context, int layout) {
        super(context, layout);

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
                activity.changeView(new CreateAccountView(activity, R.layout.create_account_activity));
            }
        });
    }

    /**
     * Logs in a user based off of their username and password input. Notifies user if the username/password are invalid
     */
    private void login() {
        String[] formInputs = new String[]{usernameText.getText().toString(), passwordText.getText().toString()};
        checkValidity(formInputs, new EditText[]{usernameText, passwordText}, new String[]{"Please enter a valid username!", "Please enter a valid password!"});
    }

    @Override
    public boolean checkValidity(String[] formInputs, final EditText[] forms, final String[] errorMessages) {
        boolean isValid = true;

        for (int i = 0; i < formInputs.length; i++) {
            if (formInputs[i].isEmpty()) {
                forms[i].setError(errorMessages[i]);
                isValid = false;
            }
        }

        final String username = formInputs[0], password = formInputs[1];

        if (isValid) {
            connectCred = Credentials.emailPassword(username, password);
            app.loginAsync(connectCred, new App.Callback<User>() {
                @Override
                public void onResult(App.Result<User> result) {
                    if (result.isSuccess()) {
                        activity.getCurrentUser().setUsername(username);
                        activity.changeView(new ConfirmCameraView(activity, R.layout.confirm_camera_activity));
                    } else {
                        usernameText.setError(errorMessages[0]);
                        passwordText.setError(errorMessages[1]);
                    }
                }
            });
        }

        return isValid;
    }

    @Override
    public void inputForm(String[] formInputs) {

    }
}
