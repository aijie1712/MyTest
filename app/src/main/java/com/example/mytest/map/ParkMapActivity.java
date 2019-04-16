package com.example.mytest.map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.example.mytest.R;
import com.example.mytest.map.location.LocationClient;
import com.example.mytest.map.location.MyLocationListener;
import com.example.mytest.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示停车场的地图页面
 */
public class ParkMapActivity extends AppCompatActivity
        implements MyLocationListener, View.OnClickListener {
    private MapView mMapView = null;
    //初始化地图控制器对象
    private AMap aMap;

    private double latitude;
    private double longitude;

    private List<LatLng> polygonOutlineList;  // 多边形轮廓

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_map);
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        polygonOutlineList = new ArrayList<>();

        new LocationClient(this).start(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onGetLocation(AMapLocation bdLocation) {
        LogUtils.i("定位结果：" + bdLocation);
        this.latitude = bdLocation.getLatitude();
        this.longitude = bdLocation.getLongitude();
        initMap();
    }

    @Override
    public void onError(String reason) {

    }

    /**
     * 初始化地图
     */
    private void initMap() {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14f));
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                polygonOutlineList.add(latLng);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_polygon:
                addPark();
                break;
            case R.id.btn_remove_all:
                aMap.clear();
                polygonOutlineList.clear();
                break;
            case R.id.btn_readme:
                startActivity(new Intent(this, MapReadmeActivity.class));
                break;
        }
    }

    /**
     * 停车场--添加多边形
     */
    private void addPark() {
        // 声明 多边形参数对象
        PolygonOptions polygonOptions = new PolygonOptions();
        // 添加 多边形的每个顶点（顺序添加）
        polygonOptions.addAll(polygonOutlineList);
        for (LatLng latLng : polygonOutlineList) {
            createRectangle(latLng);
        }
        polygonOutlineList.clear();
        polygonOptions.strokeWidth(5) // 多边形的边框
                .strokeColor(getResources().getColor(R.color.map_park_stroke_color)) // 边框颜色
                .fillColor(getResources().getColor(R.color.trans_btn_color));   // 多边形的填充色
        // 绘制
        aMap.addPolygon(polygonOptions);
    }

    /**
     * 停车位-生成一个长方形的四个坐标点
     */
    private void createRectangle(LatLng center) {
        double halfHeight = 1.0f / 10000;
        double halfWidth = 0.5f / 10000;
        List<LatLng> latLngs = new ArrayList<>();
        LatLng leftTop = new LatLng(center.latitude - halfHeight, center.longitude - halfWidth);
        LatLng rightTop = new LatLng(center.latitude - halfHeight, center.longitude + halfWidth);
        LatLng rightBottom = new LatLng(center.latitude + halfHeight, center.longitude + halfWidth);
        LatLng leftBottom = new LatLng(center.latitude + halfHeight, center.longitude - halfWidth);
        latLngs.add(leftTop);
        latLngs.add(rightTop);
        latLngs.add(rightBottom);
        latLngs.add(leftBottom);
        float length = AMapUtils.calculateLineDistance(leftTop, rightTop);
        float zoom = aMap.getCameraPosition().zoom;
        LogUtils.i("aijie", "矩形的宽：" + length);
        LogUtils.i("aijie", "真实的宽：" + length/zoom);
        // 绘制一个长方形
        aMap.addPolygon(new PolygonOptions()
                .addAll(latLngs)
                .fillColor(getResources().getColor(R.color.map_parking_space_fill_color))
                .strokeColor(Color.RED)
                .strokeWidth(1));
    }
}
