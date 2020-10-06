package com.example.digitalpath2020;

import org.bson.types.ObjectId;
import org.opencv.core.Mat;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Images extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    private String _partition = "DigitalPath2020";
    private String username;
    private String name;
    private String slide;
    private String cancer;
    private byte[] imgArr;

    public Images() {}

    public Images(String _partition, String username, String name, String slide, String cancer, byte[] imgArr) {
        super();
        this._partition = _partition;
        this.username = username;
        this.slide = slide;
        this.cancer = cancer;
        this.imgArr = imgArr;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get_partition() {
        return _partition;
    }

    public void set_partition(String _partition) {
        this._partition = _partition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlide() {
        return slide;
    }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public String getCancer() {
        return cancer;
    }

    public void setCancer(String cancer) {
        this.cancer = cancer;
    }

    public byte[] getImgArr() {
        return imgArr;
    }

    public void setImgArr(byte[] imgArr) {
        this.imgArr = imgArr;
    }
}
