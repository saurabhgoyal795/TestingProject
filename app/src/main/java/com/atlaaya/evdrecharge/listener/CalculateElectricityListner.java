
package com.atlaaya.evdrecharge.listener;
import com.atlaaya.evdrecharge.model.ResponseDefault;

public interface CalculateElectricityListner extends BaseListener {

    void onSuccess(ResponseDefault body);

}
