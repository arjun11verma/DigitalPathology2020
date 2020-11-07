package com.example.digitalpath2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class AfterCaptureView extends BaseView {
    private Bitmap[] bitArr = new Bitmap[activity.getMatList().size()]; // list of the captured images
    private byte[][] byteArr = new byte[activity.getMatList().size()][];
    private boolean clicked = false;
    
    public AfterCaptureView(Context context) {
        super(context);
        activity.setContentView(R.layout.after_capture_activity);

        LinearLayout lay = activity.findViewById(R.id.imageLayout);

        for(int i = 0; i < activity.getMatList().size(); i++)
        {
            bitArr[i] = (toBitmap(activity.getMatList().get(i)));
            ImageView view = new ImageView(activity);
            view.setImageBitmap(bitArr[i]);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lay.addView(view); // updates post view with all of the images captured so the client can confirm their quality

            byteArr[i] = toByteArray(bitArr[i]);

            System.out.println(byteArr[i].length);
        }

        activity.findViewById(R.id.uploadImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clicked) {
                    TextView upload = activity.findViewById(R.id.uploading);
                    upload.setText("Uploading...");

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

                    System.out.println("Images uploaded to server!");

                    new CountDownTimer(bitArr.length * 1000, 1000) {
                        public void onFinish() {
                            activity.changeView(new PostUploadView(activity));
                        }

                        public void onTick(long millisUntilFinished) {

                        }
                    }.start();
                }
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

    public Bitmap toBitmap(Mat m)
    {
        Bitmap map =  Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, map);
        return map;
    }

    public byte[] toByteArray(Bitmap m)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.compress(Bitmap.CompressFormat.JPEG, 100, bos); // compresses image file so its binary data can fit reasonably on the database
        return bos.toByteArray();
    }

    public void serverUpload(JSONObject object) {
        activity.getServerConnection().makePost(object);
    }

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

                for(int i = 0; i < bitArr.length; i++) { // uploads the image objects to the ImageSet object in MongoDB
                    ImageObject imgObj = realm.createEmbeddedObject(ImageObject.class, imgSet, "imageObjects"); // uploads the object
                    imgObj.setImage(toByteArray(bitArr[i])); // uploads the image data
                    imgObj.setImageType("JPEG");
                }
            }

        });
        mongoRealm.close();

        activity.changeView(new PostUploadView(activity));
    }
}
