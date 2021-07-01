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

import com.example.digitalpath2020.ExternalClasses.ImageProcessor;
import com.example.digitalpath2020.R;
import com.example.digitalpath2020.ViewInterfaces.ServerUploadable;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class UploadView extends BaseView implements ServerUploadable {
    /**
     * Constructor for the PostUploadView class
     * Sets the UI to the post upload layout
     * Sets the logout button to the activity's logout method and a method that redirects the user to the login page
     * Sets the take more images button to a method that resets the upload/images taken statuses and redirects the user to the confirm camera page
     * @param context Instance of the main activity class
     * @param status String representing the success of the upload
     */
    public UploadView(Context context, int layout, String status, String stitchedImage) {
        super(context, layout);
        checkLoggedIn(false);

        verifyAndDisplayImage(stitchedImage, (ImageView) activity.findViewById(R.id.stitchedImage));

        ((TextView)(activity.findViewById(R.id.postTitle))).setText(status);

        activity.findViewById(R.id.uploadImages).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer("Y");
            }
        });

        activity.findViewById(R.id.moreImagesBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer("N");
            }
        });
    }

    @Override
    public void uploadToServer(String status) {
        JSONObject postObject = new JSONObject();

        try {
            postObject.put("status", status);
            postObject.put("slide_id", activity.getCurrentUser().getSlideID());
            postObject.put("time_marker", activity.getImageCurrentTime());
        } catch (JSONException e) {
            System.out.println(e);
        }

        activity.getServerConnection().sendUpload(postObject);
    }

    private void verifyAndDisplayImage(String stitchedImage, ImageView imageDisplay) {
        if (stitchedImage != null) {
            byte[] decodedBytes = Base64.decode(stitchedImage, Base64.DEFAULT);
            Mat decodedImage = Imgcodecs.imdecode(new MatOfByte(decodedBytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
            decodedImage = ImageProcessor.resizeScreen(decodedImage, 350, 375);
            Bitmap imageMap = Bitmap.createBitmap(decodedImage.width(), decodedImage.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(decodedImage, imageMap);
            imageDisplay.setImageBitmap(Bitmap.createScaledBitmap(imageMap, 350, 375, false));
        }
    }
}
