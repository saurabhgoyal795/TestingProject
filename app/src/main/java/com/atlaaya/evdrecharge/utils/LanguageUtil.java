package com.atlaaya.evdrecharge.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atlaaya.evdrecharge.storage.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LanguageUtil {
    public static void setTextViewTextByLanguage(Context context, TextView view, String key) {
        try {
            JSONObject languageData = new JSONObject(SessionManager.getLanguageData(context));
            if (languageData.has(key)) {
                view.setText(languageData.getString(key));
            }
        } catch (JSONException e) {
        }
    }

    public static void setButtonTextByLanguage(Context context, Button view, String key) {
        try {
            JSONObject languageData = new JSONObject(SessionManager.getLanguageData(context));
            if (languageData.has(key)) {
                view.setText(languageData.getString(key));
            }
        } catch (JSONException e) {
        }
    }

    public static void setEditTextHintTextByLanguage(Context context, EditText view, String key) {
        try {
            JSONObject languageData = new JSONObject(SessionManager.getLanguageData(context));
            if (languageData.has(key)) {
                view.setHint(languageData.getString(key));
            }
        } catch (JSONException e) {
        }
    }
}
