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
import com.amap.api.maps.model.CircleHoleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.example.mytest.R;
import com.example.mytest.map.location.LocationClient;
import com.example.mytest.map.location.MyLocationListener;
import com.example.mytest.map.utils.ParkingSpackUtils;
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

    private Polygon polygon;

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
                if (polygon != null) {
                    if (polygon.contains(latLng)) {
                        createParkingSpace(latLng, parkingSpaceDistance);
                    }
                }
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
                polygon = null;
                break;
            case R.id.btn_readme:
                startActivity(new Intent(this, MapReadmeActivity.class));
                break;
        }
    }

    private double parkingSpaceDistance;

    /**
     * 停车场--添加多边形
     */
    private void addPark() {
        aMap.clear();
        // 声明 多边形参数对象
        PolygonOptions polygonOptions = new PolygonOptions();
        // 添加 多边形的每个顶点（顺序添加）
        polygonOptions.addAll(polygonOutlineList);
        polygonOutlineList.clear();
        polygonOptions.strokeWidth(5) // 多边形的边框
                .strokeColor(getResources().getColor(R.color.map_park_stroke_color)) // 边框颜色
                .fillColor(getResources().getColor(R.color.trans_btn_color));   // 多边形的填充色
        // 绘制
        polygon = aMap.addPolygon(polygonOptions);
        float area = AMapUtils.calculateArea(polygonOptions.getPoints());
        float scalePerPixel = aMap.getScalePerPixel();
        LogUtils.i("aijie", "区域总面积大小：" + area);
        LogUtils.i("aijie", "缩放比例：" + scalePerPixel);
        if (area > 0) {
            List<LatLng> pointList = polygonOptions.getPoints();
            for (int i = 0; i < pointList.size(); i++) {
                LatLng currentPoint = pointList.get(i);
                LatLng nextPoint;
                if ((i + 1) < pointList.size()) {
                    nextPoint = pointList.get(i + 1);
                } else {
                    nextPoint = pointList.get(0);
                }
                // 计算两点间的实际直线距离
                float distance = AMapUtils.calculateLineDistance(currentPoint, nextPoint);
                // 两点间坐标长度
                double latlngDistance = ParkingSpackUtils.getTwoPointLength(currentPoint, nextPoint);
                // 地图坐标比例，及实际距离/坐标记录   1000:1
                double mapRatio = distance / latlngDistance;
                // 计算停车位的数量
                int parkingSpaceCount = (int) (distance / (ParkConstant.parkingSpaceWidth + ParkConstant.parkingSpaceDistance));
                // 停车位的坐标宽度和长度
                double parkingSpaceLatlngWidth = ParkConstant.parkingSpaceWidth / mapRatio;
                double parkingSpaceLatlngLength = parkingSpaceLatlngWidth * 2;
                // 停车位的间距的坐标宽度
                parkingSpaceDistance = ParkConstant.parkingSpaceDistance / mapRatio;
            }
        }
    }

    /**
     * 停车位-生成一个长方形的四个坐标点
     *
     * @param latLngList 矩形中心点
     */
    private void createParkingSpace(List<LatLng> latLngList) {
        // 绘制一个长方形
        PolygonOptions options = new PolygonOptions();
        options.addAll(latLngList)
                .fillColor(getResources().getColor(R.color.map_parking_space_fill_color))
                .strokeColor(Color.RED)
                .strokeWidth(1);
        options.addHoles(new CircleHoleOptions());
        aMap.addPolygon(options);
    }

    /**
     * 停车位-生成一个长方形的四个坐标点
     *
     * @param center                   矩形中心点
     * @param parkingSpaceLatlngLength 矩形坐标宽度
     */
    private void createParkingSpace(LatLng center, double parkingSpaceLatlngLength) {
        double halfHeight = parkingSpaceLatlngLength * 2;
        double halfWidth = parkingSpaceLatlngLength;
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
        // 绘制一个长方形
        PolygonOptions options = new PolygonOptions();
        options.addAll(latLngs)
                .fillColor(getResources().getColor(R.color.map_parking_space_fill_color))
                .strokeColor(Color.RED)
                .strokeWidth(1);
        options.addHoles(new CircleHoleOptions());
        aMap.addPolygon(options);
    }
}
