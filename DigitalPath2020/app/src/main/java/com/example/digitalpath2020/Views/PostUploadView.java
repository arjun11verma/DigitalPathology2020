/**
 * This is the post upload page of the app, where the user can view the status of their upload and choose to logout or remain
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.digitalpath2020.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PostUploadView extends BaseView {
    /**
     * Constructor for the PostUploadView class
     * Sets the UI to the post upload layout
     * Sets the logout button to the activity's logout method and a method that redirects the user to the login page
     * Sets the take more images button to a method that resets the upload/images taken statuses and redirects the user to the confirm camera page
     * @param context Instance of the main activity class
     * @param status String representing the success of the upload
     */
    public PostUploadView(Context context, int layout, String status, String stitchedImage) {
        super(context, layout);

        checkLoggedIn();

        verifyAndDisplayImage(stitchedImage, (ImageView) activity.findViewById(R.id.stitchedImage));

        ((TextView)(activity.findViewById(R.id.postTitle))).setText(status);

        activity.findViewById(R.id.uploadImages).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpload();
            }
        });

        activity.findViewById(R.id.moreImagesBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.getServerConnection().setDone();
                activity.changeView(new ConfirmCameraView(activity, R.layout.confirm_camera_activity));
            }
        });
    }

    public void sendUpload() {
        JSONObject postObject = new JSONObject();

        try {
            postObject.put("name", activity.getName());
        } catch (JSONException e) {
            System.out.println(e);
        }

        activity.getServerConnection().sendUpload(postObject);
    }

    public void verifyAndDisplayImage(String stitchedImage, ImageView imageDisplay) {
        if (stitchedImage != null) {
            byte[] decodedImage = Base64.decode(stitchedImage, Base64.DEFAULT);
            Bitmap slideImageDecoded = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            imageDisplay.setImageBitmap(Bitmap.createScaledBitmap(slideImageDecoded, 350, 375, false));
        }
    }
}
