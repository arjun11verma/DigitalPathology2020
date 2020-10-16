package com.example.digitalpath2020;

import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ImageSet extends RealmObject {
    @PrimaryKey
    private ObjectId _id;
    private String _partition;

    @Required
    private String username;
    private String name;

    @Required
    private String slide;
    private String cancer;

    RealmList<ImageObject> imageObjects = new RealmList<ImageObject>();

    public ImageSet() {
        super();
    }

    public ImageSet(String username, String name, String slide, String cancer, RealmList<ImageObject> imageObjects) {
        super();
        this.username = username;
        this.slide = slide;
        this.cancer = cancer;
        this.imageObjects = imageObjects;
    }

    public RealmList<ImageObject> getImageObjects() {
        return imageObjects;
    }

    public void setImageObjects(RealmList<ImageObject> imageObjects) {
        this.imageObjects = imageObjects;
    }

    public void addImageObject(ImageObject obj) {
        imageObjects.add(obj);
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
}
