package com.jin123d.location

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.services.weather.*


/**
 * Created by jin123d on 2017/10/18.
 **/
class CustomAmapLocation(context: Context) : WeatherSearch.OnWeatherSearchListener {


    private val mContext = context

    //声明AMapLocationClientOption对象
    private var mLocationOption = AMapLocationClientOption()
    private var mLocationClient = AMapLocationClient(mContext)
    private lateinit var mQuery: WeatherSearchQuery
    private var mWeatherSearch = WeatherSearch(mContext)
    private lateinit var locationInter: LocationSuccess

    fun location(locationInter: LocationSuccess) {
        this.locationInter = locationInter
        //声明AMapLocationClient类对象
        //初始化定位
        mLocationClient = AMapLocationClient(mContext)
        //设置定位回调监听
        mLocationClient.setLocationListener { amapLocation ->
            if (amapLocation?.errorCode == 0) {
                //定位成功
                this.locationInter.success(amapLocation.latitude, amapLocation.longitude)
                mLocationClient.stopLocation()

                mQuery = WeatherSearchQuery(amapLocation.city, WeatherSearchQuery.WEATHER_TYPE_LIVE)

                mWeatherSearch.setOnWeatherSearchListener(this)
                mWeatherSearch.query = mQuery
                mWeatherSearch.searchWeatherAsyn()


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

        fun weather(weatherLive: LocalWeatherLive)
    }


    override fun onWeatherLiveSearched(weatherLiveResult: LocalWeatherLiveResult?, p1: Int) {
        if (p1 == 1000) {
            if (weatherLiveResult?.liveResult != null) {
                this.locationInter.weather(weatherLiveResult.liveResult)
            }
        }

    }

    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {
    }

}