/**
 * This is the post upload page of the app, where the user can view the status of their upload and choose to logout or remain
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PostUploadView extends BaseView {
    /**
     * Constructor for the PostUploadView class
     * Sets the UI to the post upload layout
     * Sets the logout button to the activity's logout method and a method that redirects the user to the login page
     * Sets the take more images button to a method that resets the upload/images taken statuses and redirects the user to the confirm camera page
     * @param context Instance of the main activity class
     * @param status String representing the success of the upload
     */
    public PostUploadView(Context context, String status, String stitchedImage) {
        super(context);

        checkLoggedIn();

        activity.setContentView(R.layout.post_upload_activity);

        if (stitchedImage != null) {
            ImageView imageStitch = activity.findViewById(R.id.stitchedImage);
            byte[] decodedImage = Base64.decode(stitchedImage, Base64.DEFAULT);
            Bitmap slideImageDecoded = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            imageStitch.setImageBitmap(Bitmap.createScaledBitmap(slideImageDecoded, 350, 375, false));
        }

        ((TextView)(activity.findViewById(R.id.postTitle))).setText(status);

        activity.findViewById(R.id.uploadImages).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getServerConnection().sendUpload();
            }
        });

        activity.findViewById(R.id.moreImagesBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.getServerConnection().setDone();
                activity.changeView(new ConfirmCameraView(activity));
            }
        });
    }
}
