package com.atlaaya.evdrecharge.firebase.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class BaseServiceImpl {

    private DatabaseReference databaseReference;

    private StorageReference storageReference;

    protected DatabaseReference databaseReference() {

        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }

    protected StorageReference storageReference() {
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }

        return storageReference;
    }
}
