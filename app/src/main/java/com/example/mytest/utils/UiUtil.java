package com.example.mytest.utils;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author YH
 */
public class UiUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable mRunnable = new Runnable() {
        public void run() {
            if (mToast != null) {
                mToast.cancel();
                mToast = null;              // toast 隐藏后，将其置为 null
            }
        }
    };

    public static void showToast(final Context context, final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);// 居中显示
                } else {
                    mToast.setText(message);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        }, 100);
    }


    public static void showToast(Context context, int res) {
        showToast(context, context.getString(res));
    }

    /**
     * 显示toast信息
     */
    @SuppressLint("ShowToast")
    public static void showToastLong(Context context, String message) {
        mHandler.removeCallbacks(mRunnable);
        if (mToast == null) {           // 只有 mToast == null 时才重新创建，否则只需更改提示文字
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);// 居中显示
        mToast.setText(message);
        mHandler.postDelayed(mRunnable, 2000);  // 延迟 duration 事件隐藏 toast
        mToast.show();
    }

    /**
     * 实现文本复制功能
     */
    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getPrimaryClip().toString().trim();
    }

    /**
     * 获取应用详情页面intent
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }
}
