package com.example.mytest.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.net.SocketException;

public class AppUtils {
    public static String getUUID(Context context) {
        StringBuffer sb = new StringBuffer();
        String imei = "";
        String mac = "";
        //获取imei 使用IMEI来作为Android的设备唯一标识符存在一定的弊端， 如果用户禁用掉相关权限，那么对于以上获取参数的代码。则会直接报错，不会得到我们想要的内容
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        // TODO: Consider calling
        if (telephonyManager != null) {
            imei = telephonyManager.getDeviceId();
        }
        //获取mac 地址
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //获取设备id 在设备首次运行的时候，系统会随机生成一64位的数字，
        // 并把这个数值以16进制保存下来，这个16进制的数字就是ANDROID_ID，
        // 但是如果手机恢复出厂设置这个值会发生改变。
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);

        sb.append("IMEI==")
                .append(imei)
                .append("\n mac==")
                .append(mac)
                .append("\n Android_id==")
                .append(ANDROID_ID);

        return sb.toString();
    }
}
