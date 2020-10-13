package com.example.digitalpath2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.bson.types.ObjectId;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class AfterCaptureView extends BaseView {
    private String partitionKey = "DigitalPath2020";
    private Bitmap[] bitArr = new Bitmap[activity.getMatList().size() - 1];
    
    public AfterCaptureView(Context context) {
        super(context);
        activity.setContentView(R.layout.after_capture_activity);

        LinearLayout lay = activity.findViewById(R.id.imageLayout);

        for(int i = 0; i < activity.getMatList().size() - 1; i++)
        {
            bitArr[i] = (toBitmap(activity.getMatList().get(i)));
            ImageView view = new ImageView(activity);
            view.setImageBitmap(bitArr[i]);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lay.addView(view);
        }

        activity.findViewById(R.id.uploadImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncConfiguration config = new SyncConfiguration.Builder(activity.getApp().currentUser(), "digitalpath").build();
                Realm mongoRealm = Realm.getInstance(config);

                mongoRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ObjectId id = new ObjectId();
                        ImageSet imgSet = realm.createObject(ImageSet.class, id);
                        imgSet.setCancer(activity.getCancer());
                        imgSet.setName(activity.getName());
                        imgSet.setSlide(activity.getSlide());
                        imgSet.setUsername(activity.getUsername());
                    }
                });

                mongoRealm.close();
            }
        });

        activity.findViewById(R.id.retakeImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.changeView(new MainView(activity));
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
        m.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }
}
