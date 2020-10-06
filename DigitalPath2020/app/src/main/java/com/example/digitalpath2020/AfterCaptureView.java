package com.example.digitalpath2020;

import android.app.blob.BlobHandle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import io.realm.Realm;

public class AfterCaptureView extends BaseView {
    private String partitionKey = "DigitalPath2020";
    private byte[][] imgArr;


    public AfterCaptureView(Context context) {
        super(context);

        for(int i = 0; i < activity.getMatList().size(); i++) {
            byte[] tempArr = toByteArray(activity.getMatList().get(i));
            imgArr[i] = tempArr;
        }

        final Images imgSet = new Images(partitionKey, activity.getUsername(), activity.getName(), activity.getSlide(), activity.getCancer(), imgArr[0]);

        activity.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(imgSet);
            }
        });
    }

    private byte[] toByteArray(Mat m) {
        MatOfByte mByte = new MatOfByte(m);
        byte[] arr = mByte.toArray();
        return arr;
    }
}
