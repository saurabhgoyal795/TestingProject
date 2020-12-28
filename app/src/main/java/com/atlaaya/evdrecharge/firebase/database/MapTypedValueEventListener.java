package com.atlaaya.evdrecharge.firebase.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.atlaaya.evdrecharge.firebase.beans.AbstractFirebaseBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MapTypedValueEventListener<T> implements ValueEventListener {

    private static final String TAG = "LTVEL";

    private final Class<T> clazz;

    private final int maxChidren;

    public MapTypedValueEventListener(final Class<T> clazz) {
        this(clazz, Integer.MAX_VALUE);
    }

    public MapTypedValueEventListener(final Class<T> clazz, final int maxChildren) {
        this.clazz = clazz;
        this.maxChidren = maxChildren;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        final Long childrenCount = dataSnapshot.getChildrenCount();

        final int maxSize;

        final Map<String, T> map = new LinkedHashMap<>();

        if (childrenCount <= 0L) {
            onDataChange(map);
            return;
        } else if (childrenCount > maxChidren) {
            Log.w(TAG, "children count exceed max children");
            maxSize = maxChidren;
        } else {
            maxSize = childrenCount.intValue();
        }


        int count = 0;

        for (final DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
            final String key = dataSnapshotItem.getKey();
            final T obj = dataSnapshotItem.getValue(clazz);

            if (obj instanceof AbstractFirebaseBean) {
                ((AbstractFirebaseBean) obj).setUid(key);
            }

            map.put(key, obj);
            count++;

            if (count > maxSize) {
                break;
            }
        }

        onDataChange(map);
    }

    public abstract void onDataChange(Map<String, T> data);

}
