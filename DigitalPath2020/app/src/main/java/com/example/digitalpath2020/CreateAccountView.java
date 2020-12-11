/**
 * This is the create account page of the app, where users can create a new account
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import io.realm.mongodb.App;

public class CreateAccountView extends BaseView {
    private boolean isValid = true; // Boolean determining whether account information is valid or not
    private EditText usernameText; // Text input for username
    private EditText passwordText; // Text input for password

    /**
     * Constructor for the CreateAccountView class
     * Sets the UI to the create account layout
     * Sets the create account button to the createAccount method
     * @param context Instance of the main activity
     */
    public CreateAccountView(Context context) {
        super(context);
        activity = (MainActivity) context;
        activity.setContentView(R.layout.create_account_activity);

        usernameText = activity.findViewById(R.id.createUsername);
        passwordText = activity.findViewById(R.id.createPassword);

        activity.findViewById(R.id.accountBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    /**
     * Creates an account using input from the username and password input fields
     * Determines whether input is valid and then creates an account if it is using the MongoDB API
     */
    private void createAccount() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            usernameText.setError("Please enter a valid email!"); // checks if username is valid
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordText.setError("Please enter a valid password!"); // checks if password is valid
            isValid = false;
        }

        if (isValid) {
            app.getEmailPassword().registerUserAsync(username, password, new App.Callback() {
                @Override
                public void onResult(App.Result result) { // makes an async call to the database to register a user
                    if (result.isSuccess()) {
                        System.out.println("Account Creation Succeeded.");
                        activity.changeView(new LoginView(activity)); // switches to the login page
                    } else {
                        usernameText.setError("Please enter a valid email.");
                        passwordText.setError("Please enter a longer password.");
                        System.out.println("Account Creation Failed.");
                    }
                }
            });
        }
    }
}
