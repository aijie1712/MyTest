package com.example.mytest.map.utils;

import com.amap.api.maps.model.LatLng;

public class ParkingSpackUtils {
    /**
     * 根据起始点和结束点位置坐标计算该方向上指定位置的坐标
     *
     * @param start
     * @param end
     * @param distance
     * @return
     */
    public static LatLng getPointLatlng(LatLng start, LatLng end, double distance) {
        double x1 = start.latitude;
        double y1 = start.longitude;
        double x2 = end.latitude;
        double y2 = end.longitude;

        double r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        double cx = (distance * (x2 - x1)) / r + x1;
        double cy = (distance * (y2 - y1)) / r + y1;

        return new LatLng(cx, cy);
    }

    /**
     * 计算两点间的距离
     * @param start 开始点
     * @param end 结束点
     * @return 距离
     */
    public static double getTwoPointLength(LatLng start, LatLng end){
        double x1 = start.latitude;
        double y1 = start.longitude;
        double x2 = end.latitude;
        double y2 = end.longitude;

        return Math.sqrt(Math.abs((x1 - x2)) * Math.abs((x1 - x2)) + Math.abs((y1 - y2)) * Math.abs((y1 - y2)));
    }
}
