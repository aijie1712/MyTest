package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mytest.map.ParkMapActivity;
import com.example.mytest.notification.NotificationTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_notification:
                startActivity(new Intent(this, NotificationTestActivity.class));
                break;
            case R.id.btn_test_donwload:
                startActivity(new Intent(this, DownloadApkActivity.class));
                break;
            case R.id.btn_test_map:
                startActivity(new Intent(this, ParkMapActivity.class));
                break;
        }
    }
}
