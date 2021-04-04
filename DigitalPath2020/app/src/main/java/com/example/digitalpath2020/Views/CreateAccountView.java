/**
 * This is the create account page of the app, where users can create a new account
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.digitalpath2020.R;
import com.example.digitalpath2020.ViewInterfaces.FormFillable;

import io.realm.mongodb.App;

public class CreateAccountView extends BaseView implements FormFillable {
    private EditText usernameText; // Text input for username
    private EditText passwordText; // Text input for password

    /**
     * Constructor for the CreateAccountView class
     * Sets the UI to the create account layout
     * Sets the create account button to the createAccount method
     * @param context Instance of the main activity
     */
    public CreateAccountView(Context context, int layout) {
        super(context, layout);

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
        String[] formInputs = {usernameText.getText().toString(), passwordText.getText().toString()};
        if (checkValidity(formInputs, new EditText[]{usernameText, passwordText}, new String[]{"Please enter a valid email!", "Please enter a longer password!"})) {
            inputForm(formInputs);
        };
    }

    @Override
    public boolean checkValidity(String[] formInputs, final EditText[] forms, String[] errorMessages) {
        boolean isValid = true;

        for (int i = 0; i < formInputs.length; i++) {
            if (formInputs[i].isEmpty()) {
                forms[i].setError(errorMessages[i]);
                isValid = false;
            } else {
                formInputs[i] = formInputs[i].trim();
            }
        }

        return isValid;
    }

    @Override
    public void inputForm(String[] formInputs) {
        app.getEmailPassword().registerUserAsync(formInputs[0], formInputs[1], new App.Callback() {
            @Override
            public void onResult(App.Result result) { // makes an async call to the database to register a user
                if (result.isSuccess()) {
                    activity.changeView(new LoginView(activity, R.layout.login_activity)); // switches to the login page
                } else {
                    usernameText.setError("Please enter a valid email.");
                    passwordText.setError("Please enter a longer password.");
                }
            }
        });
    }
}
