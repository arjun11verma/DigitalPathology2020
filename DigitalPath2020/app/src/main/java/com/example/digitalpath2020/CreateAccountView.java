package com.example.digitalpath2020;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;

public class CreateAccountView extends BaseView {
    private Credentials connectCred;
    private boolean isValid = true;
    private EditText usernameText;
    private EditText passwordText;

    public CreateAccountView(Context context) {
        super(context);
        activity = (MainActivity)context;
        activity.setContentView(R.layout.create_account_activity);

        app = activity.getApp();
        usernameText = activity.findViewById(R.id.createUsername);
        passwordText = activity.findViewById(R.id.createPassword);

        activity.findViewById(R.id.accountBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            usernameText.setError("Please enter a valid username!");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordText.setError("Please enter a valid password!");
            isValid = false;
        }

        if(isValid) {
            app.getEmailPassword().registerUserAsync(username, password, new App.Callback() {
                @Override
                public void onResult(App.Result result) {
                    if(result.isSuccess()) {
                        System.out.println("Account Creation Succeeded.");
                        activity.changeView(new LoginView(activity));
                    }
                    else {
                        usernameText.setError("Please enter a valid email.");
                        passwordText.setError("Please enter a longer password.");
                        System.out.println("Account Creation Failed.");
                    }
                }
            });
        }
    }
}
