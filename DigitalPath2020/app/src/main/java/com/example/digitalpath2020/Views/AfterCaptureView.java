/**
 * This is the after capture page of the app, where images are reviewed and uploaded
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.digitalpath2020.R;
import com.example.digitalpath2020.ViewInterfaces.ServerUploadable;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AfterCaptureView extends BaseView implements ServerUploadable {
    private Bitmap[] bitArr = new Bitmap[activity.getMatList().size()]; // Empty array of Bitmaps whose length is equivalent to the number of images captured
    private byte[][] byteArr = new byte[activity.getMatList().size()][]; // Empty array of byte arrays whose length is equivalent to the number of images captured
    private boolean clicked = false; // Boolean to prevent button from being over-clicked
    private ProgressBar uploading; // Progress bar to visualize image uploading

    /**
     * Constructor for the AfterCaptureView class
     * Sets the UI to the after capture layout
     * Converts the list of images taken in the main view into bitmaps, and then uploads these bitmaps to the UI page so the user can review the images taken
     * Sets the upload images button's click to the serverUpload method and sets the retake images button's click to a method that redirects to the image capture page
     * @param context A reference to the instance of the main activity class
     */
    public AfterCaptureView(Context context, int layout) {
        super(context, layout);

        checkLoggedIn(false);

        uploading = activity.findViewById(R.id.uploadingBar);
        uploading.setVisibility(View.GONE);

        LinearLayout imageLayout = activity.findViewById(R.id.imageLayout);
        addImagesToDisplay(activity.getMatList(), imageLayout);

        activity.findViewById(R.id.uploadImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer("");
            }
        });

        activity.findViewById(R.id.retakeImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.changeView(new ConfirmCameraView(activity, R.layout.confirm_camera_activity));
            }
        });
    }

    public void addImagesToDisplay(List<Mat> matList, LinearLayout imageLayout) {
        for (int i = 0; i < matList.size(); i++) {
            bitArr[i] = (toBitmap(activity.getMatList().get(i)));
            System.out.println(activity.getMatList().get(i).size());
            ImageView view = new ImageView(activity);
            view.setImageBitmap(bitArr[i]);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            imageLayout.addView(view);
            byteArr[i] = toByteArray(bitArr[i]);
        }
    }

    /**
     * Converts the OpenCV Mat to a Bitmap
     * @param m Mat to be converted to a bitmap
     * @return The converted bitmap
     */
    public Bitmap toBitmap(Mat m) {
        Bitmap map = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, map);
        return map;
    }

    /**
     * Converts a Bitmap to a byte array
     * @param m Bitmap to be converted to a byte array
     * @return The converted byte array
     */
    public byte[] toByteArray(Bitmap m) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.compress(Bitmap.CompressFormat.JPEG, 100, bos); // compresses image file so its binary data can fit reasonably on the database
        return bos.toByteArray();
    }

    @Override
    public void uploadToServer(String status) {
        if (!clicked) {
            uploading.setVisibility(View.VISIBLE);

            JSONObject object = new JSONObject();

            try {
                object.put("name", activity.getCurrentUser().getName());
                object.put("cancer", activity.getCurrentUser().getCancer());
                object.put("slide", activity.getCurrentUser().getSlide());
                object.put("username", activity.getCurrentUser().getUsername());

                for (int i = 0; i < byteArr.length; i++) {
                    String tag = "" + i;
                    object.put(tag, Base64.encodeToString(byteArr[i], Base64.DEFAULT));
                }
            } catch (JSONException e) {
                System.out.println(e);
            }

            clicked = true;

            activity.getServerConnection().sendImages(object);
        }
    }
}
