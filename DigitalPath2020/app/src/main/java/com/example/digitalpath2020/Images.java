package com.example.digitalpath2020;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Images extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    private String _partition = "DigitalPath2020";
    private String username = "";

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
}
