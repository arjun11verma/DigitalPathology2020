package com.example.digitalpath2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class AfterCaptureView extends BaseView {
    private String partitionKey = "DigitalPath2020";
    private List<Bitmap> bitArr = new ArrayList<Bitmap>();
    private byte[][] imgArr;
    
    public AfterCaptureView(Context context) {
        super(context);
        activity.setContentView(R.layout.after_capture_activity);

        LinearLayout lay = activity.findViewById(R.id.imageLayout);

        for(int i = 0; i < activity.getMatList().size() - 1; i++)
        {
            bitArr.add(toBitmap(activity.getMatList().get(i)));
            ImageView view = new ImageView(activity);
            view.setImageBitmap(bitArr.get(i));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lay.addView(view);
        }

        activity.findViewById(R.id.uploadImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        activity.findViewById(R.id.retakeImgBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetClick();
                activity.changeView(new MainView(activity));
            }
        });

        /*activity.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(imgSet);
            }
        });*/
    }

    public Bitmap toBitmap(Mat m)
    {
        Bitmap map =  Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, map);
        return map;
    }
}
