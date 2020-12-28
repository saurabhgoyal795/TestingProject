package com.atlaaya.evdrecharge.firebase.beans;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public interface FirebaseBean extends Serializable {

    @Exclude
    String getUid();

}
