package com.example.mytest.utils;

import android.app.Activity;
import android.location.LocationManager;

import static android.content.Context.LOCATION_SERVICE;

/**
 * 作者：Android_AJ on 2017/4/7.
 * 邮箱：ai15116811712@163.com
 * 版本：v1.0
 * 通用工具类 1.给RecyclerView设置分割线，2.获取版本号，3.判断Service是否在运行
 */
public class CommonUtils {
    public static boolean isGPS(Activity activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            return true;
        } else {
            return false;
        }
    }
}
