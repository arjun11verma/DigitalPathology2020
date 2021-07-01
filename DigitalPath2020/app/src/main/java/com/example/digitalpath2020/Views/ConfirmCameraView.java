/**
 * This is the confirm camera page of the app, where the user inputs important related data and tests the camera
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.digitalpath2020.ExternalClasses.Patient;
import com.example.digitalpath2020.R;
import com.example.digitalpath2020.ViewInterfaces.FormFillable;

public class ConfirmCameraView extends BaseView implements FormFillable {
    private EditText slideName; // Name of the type of slide being used
    private EditText cancerName; // Name of the type of cancer
    private EditText patientName; // Name of the patient
    private EditText devServerUrl;

    /**
     * Constructor for the ConfirmCameraView class
     * Sets the UI to the confirm camera layout
     * Sets the preview camera button to the testCamera method, and sets the startCameraPage button to a method that determines whether the user input is valid and correctly redirects the user
     * @param context Instance of the main activity class
     */
    public ConfirmCameraView(Context context, int layout) {
        super(context, layout);
        checkLoggedIn(false);

        slideName = activity.findViewById(R.id.slideType);
        cancerName = activity.findViewById(R.id.cancerType);
        patientName = activity.findViewById(R.id.patientName);
        devServerUrl = activity.findViewById(R.id.devServerUrl);
        reloadOldValues(activity.getCurrentUser());

        ((TextView) (activity.findViewById(R.id.setupTitle))).setText("Welcome " + (activity.getCurrentUser().getUsername().split("@"))[0] + "!");

        activity.findViewById(R.id.previewCamera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                testCamera();
            }
        });

        activity.findViewById(R.id.startCameraPage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] formInputs = {slideName.getText().toString(), cancerName.getText().toString(), patientName.getText().toString(), devServerUrl.getText().toString()};
                if (checkValidity(formInputs, new EditText[]{slideName, cancerName, patientName, devServerUrl},
                        new String[]{"Please input a valid slide type!", "Please input a valid cancer type!", "Please input a valid accession number!"})) {
                    inputForm(formInputs);
                }
            }
        });
    }

    private void reloadOldValues(Patient currentUser) {
        if (currentUser.getSlide() != null) slideName.setText(currentUser.getSlide());
        if (currentUser.getCancer() != null) cancerName.setText(currentUser.getCancer());
        if (currentUser.getSlideID() != null) patientName.setText(currentUser.getSlideID());
        if (activity.getServerConnection().getServerUrl() != null) devServerUrl.setText(activity.getServerConnection().getServerUrl());
    }

    /**
     * Opens the native Android camera to test if it is viewing the slide image correctly
     */
    private void testCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            activity.startActivityForResult(takePictureIntent, 1); // opens up Android camera for camera calibration
        } catch (ActivityNotFoundException e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean checkValidity(String[] formInputs, EditText[] forms, String[] errorMessages) {
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
    public void inputForm(String formInputs[]) {
        activity.getCurrentUser().setSlideID(formInputs[0]);
        activity.getCurrentUser().setCancer(formInputs[1]);
        activity.getCurrentUser().setSlide(formInputs[2]);
        activity.getServerConnection().setServerUrl(formInputs[3]);
        activity.changeView(new ImageCaptureView(activity, R.layout.activity_main)); // switches to picture capturing page
    }
}
