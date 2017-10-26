package com.jin123d.location

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption


/**
 * Created by jin123d on 2017/10/18.
 **/
class CustomAmapLocation(context: Context) {
    private val mContext = context

    //声明AMapLocationClientOption对象
    private var mLocationOption = AMapLocationClientOption()
    private var mLocationClient = AMapLocationClient(mContext)

    fun location(location: LocationSuccess) {
        //声明AMapLocationClient类对象
        //初始化定位
        mLocationClient = AMapLocationClient(mContext)

        //设置定位回调监听
        mLocationClient.setLocationListener { amapLocation ->
            if (amapLocation?.errorCode == 0) {
                //定位成功
                location.success(amapLocation.latitude, amapLocation.longitude)
                mLocationClient.stopLocation()

            } else {
                Log.e("CustomAmapLocation", "location Error, ErrCode:" + amapLocation.errorCode + ", errInfo:" + amapLocation.errorInfo)
            }
        }
        //声明AMapLocationClientOption对象
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.isOnceLocation = true
        mLocationOption.isOnceLocationLatest = true

        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.startLocation()
    }


    interface LocationSuccess {
        fun success(latitude: Double, longitude: Double)
    }


}