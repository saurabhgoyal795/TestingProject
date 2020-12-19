package com.atlaaya.evdrecharge.widgets;

import android.content.Context;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;


import org.androidannotations.annotations.EView;

import icepick.Icepick;


/**
 * ClickableFrameLayout
 * https://stackoverflow.com/questions/16977873/framelayout-click-event-is-not-firing
 */
@EView
public abstract class BaseWidget extends FrameLayout implements Checkable {

    private OnClickListener mOnClickListener;

    @BottomSheetBehavior.State
    volatile boolean checked;

    private static final int[] CheckedStateSet = {
            android.R.attr.state_checked,
    };

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mOnClickListener != null;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    public BaseWidget(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract int layout();

    protected void init() {
        final int layout = layout();

        final View view = (layout != 0) ? inflate(getContext(), layout, this) : null;
    }

}
