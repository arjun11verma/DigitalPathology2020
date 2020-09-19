package com.example.digitalpathology2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int cameraRequest = 1;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private BaseView currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.camera_layout);

        changeView(new StartingView(this));
    }

    public void setCamera() {
        Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //sets up the Android camera
        startActivityForResult(newIntent, cameraRequest); //opens the Android camera
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap tempMap = (Bitmap)data.getExtras().get("data"); //turns the picture data into a bitmap
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        bitmapList.add(tempMap); //this stores the bitmap of the image on the app locally

        tempMap.compress(Bitmap.CompressFormat.JPEG, 90, outStream); //converts this data into a JPEG
        byte[] temp = outStream.toByteArray(); //this is a byte array of the JPEG file
        // I'm not 100% sure we should even convert it to a JPEG at this point, I'm going to have to take a look at implementing the OpenCV libraries into Android Studio
    }

    public void changeView(BaseView view) { currentView = view; }

}