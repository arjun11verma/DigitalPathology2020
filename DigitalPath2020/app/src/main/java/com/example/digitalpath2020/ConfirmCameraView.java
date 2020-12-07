package com.example.digitalpath2020;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmCameraView extends BaseView {
    private EditText slideName;
    private EditText cancerName;
    private EditText patientName;
    private boolean isValid = true;

    public ConfirmCameraView(Context context) {
        super(context);

        checkLoggedIn();

        activity.setContentView(R.layout.confirm_camera_activity);

        slideName = activity.findViewById(R.id.slideType);
        cancerName = activity.findViewById(R.id.cancerType);
        patientName = activity.findViewById(R.id.patientName);

        ((TextView)(activity.findViewById(R.id.setupTitle))).setText("Welcome " + (activity.getUsername().split("@"))[0] + "!");

        activity.findViewById(R.id.previewCamera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                testCamera();
            }
        });

        activity.findViewById(R.id.startCameraPage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;

                String slide = slideName.getText().toString();
                String cancer = cancerName.getText().toString();
                String patient = patientName.getText().toString();

                if(slide.isEmpty()) {
                    slideName.setError("Please enter a valid slide name"); // checks if the slide name is valid
                    isValid = false;
                }
                if(cancer.isEmpty()) {
                    cancerName.setError("Please enter a valid cancer name"); // checks if the cancer name is valid
                    isValid = false;
                }
                if(patient.isEmpty()) {
                    patientName.setError("Please enter a valid patient name"); // checks if the patient name is valid
                    isValid = false;
                }

                if(isValid) {
                    activity.setName(patient);
                    activity.setCancer(cancer);
                    activity.setSlide(slide);
                    activity.changeView(new MainView(activity)); // switches to picture capturing page
                }
            }
        });
    }

    private void testCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            activity.startActivityForResult(takePictureIntent, 1); // opens up Android camera for camera calibration
        } catch (ActivityNotFoundException e) {
            System.out.println("Couldn't do it, sorry");
        }
    }
}
