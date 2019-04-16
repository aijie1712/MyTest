package com.example.mytest.map.location;

import com.amap.api.location.AMapLocation;

/**
 * Created by klx on 2017/7/21.
 * 定位的回调
 */

public interface MyLocationListener {
    void onGetLocation(AMapLocation bdLocation);

    void onError(String reason);
}
