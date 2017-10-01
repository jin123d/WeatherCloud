package com.jin123d.weathercloud

/**
 * Created by jin123d on 10/1 0001.
 **/
interface IDateFactory {

    fun getUrl(time: String): String

    fun timeLastOrNext(nowTime: String, isLast: Boolean = true): String

    fun weather2LocalTime(weatherTime: String): String

    fun getWeatherTime(): String

}