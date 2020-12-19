package com.atlaaya.evdrecharge.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.atlaaya.evdrecharge.R;
import com.atlaaya.evdrecharge.configs.GlideApp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.ViewById;

import de.hdodenhof.circleimageview.CircleImageView;

@EView
public class FaceWidget extends BaseWidget {

//    @ViewById(R.id.textViewFace)
    TextView textViewFace;

//    @ViewById(R.id.imageViewFace)
    CircleImageView imageViewFace;

//    @ViewById(R.id.viewSwitcherFace)
    ViewSwitcher viewSwitcherFace;

    public FaceWidget(@NonNull Context context) {
        super(context);
    }

    public FaceWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected int layout() {
        return R.layout.widget_face;
    }

    @AfterViews
    void initView() {
        textViewFace = findViewById(R.id.textViewFace);
        imageViewFace = findViewById(R.id.imageViewFace);
        viewSwitcherFace = findViewById(R.id.viewSwitcherFace);
    }

    public void setInitials(String initials) {
        textViewFace.setText(initials);
    }

    public void photo(final Object photo) {
        if (photo != null) {
            viewSwitcherFace.setDisplayedChild(1);
            try {
                GlideApp.with(this).load(photo).fitCenter().into(imageViewFace);
            } catch (Exception e) {
                //do nothing
            }
        } else {
            clearPhoto();
            viewSwitcherFace.setDisplayedChild(0);
        }
    }

    public void clear() {
        clearPhoto();
        viewSwitcherFace.setDisplayedChild(0);
        clearInitials();
    }

    private void clearPhoto() {
        imageViewFace.setImageResource(R.color.transparent);
    }

    public void clearRoundView() {
        viewSwitcherFace.setBackgroundResource(R.color.transparent);
    }

    public void setRoundViewWithoutBorder() {
        viewSwitcherFace.setBackgroundResource(R.drawable.rounded_face_without_border);
        imageViewFace.setBorderWidth(0);
    }

    public void setRoundView() {
        viewSwitcherFace.setBackgroundResource(R.drawable.rounded_face);
    }

    private void clearInitials() {
        textViewFace.setText("");
    }


}
