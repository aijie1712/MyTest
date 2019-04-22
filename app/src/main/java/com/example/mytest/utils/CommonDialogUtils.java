package com.example.mytest.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mytest.R;
import com.example.mytest.utils.dialog.CustomDialog;

/**
 * Created by klx on 2017/9/5.
 * 通用弹窗
 */

public class CommonDialogUtils {

    public static void showSetPermissionDialog(final Activity activity, String title,
                                               final DialogInterface.OnClickListener cancelClick,
                                               final DialogInterface.OnClickListener confirmClick) {
        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(title)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (cancelClick != null) {
                            cancelClick.onClick(dialog, which);
                        }
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (confirmClick != null) {
                            confirmClick.onClick(dialog, which);
                            return;
                        }
                        Intent intent = UiUtil.getAppDetailSettingIntent(activity);
                        activity.startActivity(intent);
                    }
                }).show();
    }

    /**
     * 显示确认弹窗
     *
     * @param activity
     * @param message        提示消息
     * @param onConfirmClick 确认点击监听
     */
    public static void showConfirmDialog(boolean canCancel, Activity activity, String leftText, String rightText, String message,
                                         final View.OnClickListener onCancelClick, final View.OnClickListener onConfirmClick) {
        final CustomDialog dialog = new CustomDialog(activity, R.style.Dialog);
        View layout = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_confirm, null);
        dialog.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setCancelable(canCancel);
        TextView tv_message = (TextView) layout.findViewById(R.id.tv_message);
        TextView tv_cancel = (TextView) layout.findViewById(R.id.tv_cancel);
        TextView tv_confirm = (TextView) layout.findViewById(R.id.tv_confirm);

        tv_message.setText(message);
        tv_cancel.setText(leftText);
        tv_confirm.setText(rightText);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onCancelClick != null) {
                    onCancelClick.onClick(v);
                }
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onConfirmClick != null) {
                    onConfirmClick.onClick(v);
                }
            }
        });
        dialog.show();
    }
}
