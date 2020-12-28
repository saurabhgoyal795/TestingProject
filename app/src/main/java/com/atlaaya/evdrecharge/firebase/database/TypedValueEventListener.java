package com.atlaaya.evdrecharge.firebase.database;

import androidx.annotation.NonNull;

import com.atlaaya.evdrecharge.firebase.beans.AbstractFirebaseBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public abstract class TypedValueEventListener<T> implements ValueEventListener {

    private final Class<T> clazz;

    public TypedValueEventListener(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        final T obj = dataSnapshot.getValue(clazz);

        if (obj instanceof AbstractFirebaseBean) {
            ((AbstractFirebaseBean) obj).setUid(dataSnapshot.getKey());
        }

        onDataChange(obj);
    }

    public abstract void onDataChange(T data);

}
