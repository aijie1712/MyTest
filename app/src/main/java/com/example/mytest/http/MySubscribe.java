package com.example.mytest.http;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by Administrator on 2016-12-19
 *
 * @desc 封装的观察者
 */

public abstract class MySubscribe<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.i("aijie", "操作onError==" + e.getMessage());

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
