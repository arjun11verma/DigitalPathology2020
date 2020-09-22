package com.example.digitalpathology2020;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PictureLayoutView extends BaseView {

    public PictureLayoutView(Context context) {
        super(context);
        activity.setContentView(R.layout.picture_view_layout);

        addPictures();
    }

    private void addPictures() {
        LinearLayout lay = (LinearLayout)activity.findViewById(R.id.pictureLayout);

        for(int i = 0; i < activity.getBitmapList().size(); i++) {
            addPicture(activity.getBitmapList().get(i), lay);
        }

        TextView text = activity.findViewById(R.id.numPictures);
        String numPics = String.format("You've taken %d pictures!", activity.getBitmapList().size());
        text.setText(numPics);
    }

    private void addPicture(Bitmap map, LinearLayout lay) {
        ImageView imgView = new ImageView(activity);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        imgView.setImageBitmap(Bitmap.createScaledBitmap(map, 800, 800, false));
        lay.addView(imgView);
    }

}
