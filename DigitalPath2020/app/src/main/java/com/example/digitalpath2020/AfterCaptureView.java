/**
 * This is the after capture page of the app, where images are reviewed and uploaded
 *
 * @author Arjun Verma
 * @version 1.0
 */

package com.example.digitalpath2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class AfterCaptureView extends BaseView {
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
    public AfterCaptureView(Context context) {
        super(context);

        activity.setContentView(R.layout.after_capture_activity);

        uploading = activity.findViewById(R.id.uploadingBar);
        uploading.setVisibility(View.GONE);

        LinearLayout lay = activity.findViewById(R.id.imageLayout);

        for (int i = 0; i < activity.getMatList().size(); i++) {
            bitArr[i] = (toBitmap(activity.getMatList().get(i)));
            ImageView view = new ImageView(activity);
            view.setImageBitmap(bitArr[i]);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lay.addView(view);

            byteArr[i] = toByteArray(bitArr[i]);

            System.out.println(byteArr[i].length);
        }

        activity.findViewById(R.id.uploadImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                serverUpload();
            }
        });

        activity.findViewById(R.id.retakeImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.changeView(new ConfirmCameraView(activity));
            }
        });
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

    /**
     * Uploads the entire set of images alongside the user data in a JSON object to the Python server by utilizing the server connection
     */
    public void serverUpload() {
        if (!clicked) {
            uploading.setVisibility(View.VISIBLE);

            JSONObject object = new JSONObject();

            try {
                object.put("name", activity.getName());
                object.put("cancer", activity.getCancer());
                object.put("slide", activity.getSlide());
                object.put("username", activity.getUsername());

                for (int i = 0; i < bitArr.length; i++) {
                    String tag = "" + i;
                    object.put(tag, Base64.encodeToString(byteArr[i], Base64.DEFAULT));
                    System.out.println(i);
                }
            } catch (JSONException e) {
                System.out.println(e);
            }

            clicked = true;

            activity.getServerConnection().makePost(object);

            System.out.println("Images sent to server!");
        }
    }

    /**
     * Uploads the images directly to the MongoDB database. This method is no longer in use
     */
    public void mongoUpload() {
        SyncConfiguration config = new SyncConfiguration.Builder(activity.getApp().currentUser(), "digitalpath").build();
        Realm mongoRealm = Realm.getInstance(config); // gets an instance of the MongoDB realm based off of the current logged in user

        mongoRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ObjectId id = new ObjectId();
                ImageSet imgSet = realm.createObject(ImageSet.class, id); // creates an ImageSet object in MongoDB

                imgSet.setCancer(activity.getCancer());
                imgSet.setName(activity.getName());
                imgSet.setSlide(activity.getSlide());
                imgSet.setUsername(activity.getUsername());

                for (int i = 0; i < bitArr.length; i++) { // uploads the image objects to the ImageSet object in MongoDB
                    ImageObject imgObj = realm.createEmbeddedObject(ImageObject.class, imgSet, "imageObjects"); // uploads the object
                    imgObj.setImage(toByteArray(bitArr[i])); // uploads the image data
                    imgObj.setImageType("JPEG");
                }
            }

        });
        mongoRealm.close();

        activity.changeView(new PostUploadView(activity, "Deprecated"));
    }
}
