package com.atlaaya.evdrecharge.firebase.beans;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public abstract class AbstractFirebaseBean implements FirebaseBean {

    @Exclude
    private String uid;

    @Exclude
    @Override
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

}
