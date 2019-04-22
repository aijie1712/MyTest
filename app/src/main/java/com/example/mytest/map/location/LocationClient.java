package com.example.mytest.map.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.mytest.R;
import com.example.mytest.http.MySubscribe;
import com.example.mytest.rxpermission.RxPermissions;
import com.example.mytest.utils.CommonDialogUtils;
import com.example.mytest.utils.CommonUtils;
import com.example.mytest.utils.LogUtils;
import com.example.mytest.utils.UiUtil;

/**
 * Created by klx on 2017/7/21.
 * 定位相关工具类
 */

public class LocationClient {
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private OnStartLocationListener onStartLocationListener;

    public LocationClient(Context context) {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        initLocationOption();
    }

    /**
     * 开始定位
     *
     * @param activity           定位的页面
     * @param myLocationListener 定位结果回调
     */
    public void start(final Activity activity, MyLocationListener myLocationListener) {
        start(activity, myLocationListener, null);
    }

    /**
     * 开始定位
     *
     * @param activity                定位的页面
     * @param myLocationListener      定位结果回调
     * @param onStartLocationListener 开始定位回调
     */
    public void start(final Activity activity, MyLocationListener myLocationListener, OnStartLocationListener onStartLocationListener) {
        this.onStartLocationListener = onStartLocationListener;
        //设置定位回调监听
        mLocationClient.setLocationListener(new CustomLocationListener(myLocationListener));
        if (CommonUtils.isGPS(activity)) {
            requestLocationPermission(activity);
        } else {
            CommonDialogUtils.showConfirmDialog(false, activity, activity.getString(R.string.cancel), activity.getString(R.string.goSetting),
                    activity.getString(R.string.map_no_location_permission_tip),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestLocationPermission(activity);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
        }
    }

    private void requestLocationPermission(final Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions
                .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new MySubscribe<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean && secondCheckPermission(activity)) {
                            //给定位客户端对象设置定位参数
                            mLocationClient.setLocationOption(mLocationOption);
                            //启动定位
                            mLocationClient.startLocation();
                            if (onStartLocationListener != null) {
                                onStartLocationListener.onStartLocation();
                            }
                        } else {
                            CommonDialogUtils.showSetPermissionDialog(activity,
                                    "需要定位和获取手机信息权限,去设置",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.finish();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = UiUtil.getAppDetailSettingIntent(activity);
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                    });
                        }
                    }
                });
    }

    private boolean secondCheckPermission(Activity activity) {
        boolean hasLocationPermission = true;
        boolean hasReadPhoneImei = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            hasReadPhoneImei = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        }
        return hasLocationPermission && hasReadPhoneImei;
    }

    private void initLocationOption() {
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(true);
    }

    public class CustomLocationListener implements AMapLocationListener {
        MyLocationListener myLocationListener;

        CustomLocationListener(MyLocationListener myLocationListener) {
            this.myLocationListener = myLocationListener;
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    if (myLocationListener != null) {
                        StringBuilder address = new StringBuilder(aMapLocation.getProvince())
                                .append(aMapLocation.getCity())
                                .append(aMapLocation.getDistrict())
                                .append(aMapLocation.getStreet())
                                .append(aMapLocation.getStreetNum());
                        myLocationListener.onGetLocation(aMapLocation);
                    }
                    mLocationClient.stopLocation();
                } else {
                    LogUtils.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    public interface OnStartLocationListener {
        void onStartLocation();
    }

}
