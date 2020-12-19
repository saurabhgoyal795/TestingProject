package com.atlaaya.evdrecharge.apiPresenter;


import com.atlaaya.evdrecharge.listener.BaseListener;

import org.json.JSONObject;


public abstract class BasePresenter<I extends BaseListener> {

    private I iView;

    public BasePresenter() {
    }

    public I getView() {
        return iView;
    }

    public void setView(I iView) {
        this.iView = iView;
    }


    boolean handleError(retrofit2.Response response) {
        /*if (response.code() == 203) {
           // return handleError(((BaseResponse) response.body()), false);
        }
        else if (response.code() == 440) {
            getView().onTokenExpired();
            return true;
        }
        else */
        if (response.errorBody() != null) {
            try {
                String error = response.errorBody().string();
//                String error = response.message();
//                String error = response.toString();

//                Converter<ResponseBody, ErrorResponse> converter = retrofit2.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

                JSONObject jObjError = new JSONObject(error);
                if (jObjError.has("error")) {
                    error = jObjError.optString("error");
                } else {
                    error = jObjError.optString("message");
                }

                if ((response.code() == 401 && jObjError.has("error"))
                        || (jObjError.has("active") && !jObjError.getBoolean("active"))) {
                    getView().dialogAccountDeactivate(!error.isEmpty() ? error : "");
                } else {
                    getView().onError(!error.isEmpty() ? error : null);
                }

            } catch (Exception e) {
                e.printStackTrace();
                getView().onError(null);
                return true;
            }
            return true;
        }
        return false;
    }

}
