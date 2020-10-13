package com.example.digitalpath2020;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class ImageObject extends RealmObject {
    byte[] image;
    String imageType;

    public ImageObject() { super(); }

    public ImageObject(byte[] image, String imageType) {
        super();
        this.image = image;
        this.imageType = imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
