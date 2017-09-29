package com.jin123d.weathercloud

import android.support.annotation.Nullable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tianGe_jin123d on 2017/9/28.
 **/
class DateUtil {

    companion object {


        /**
         * 格式必须为201709200930
         * */
        fun getUrl(time: String): String {
            val tempTime: String
            if (time.length != 12) {
                tempTime = getNowWeatherTime(null)
            } else {
                tempTime = time
            }
            val url = StringBuilder()
            url.append(Url.baseUrl)
            url.append(time2Date(tempTime))
            url.append(Url.normalUrl)
            url.append(tempTime)
            url.append(Url.endUrl)
            return url.toString()
        }


        /**
         * 将2017082009391245转换为2017/08/20
         *
         * */
        fun time2Date(time: String): String {
            val pat1 = "yyyyMMddhhmm"
            val pat2 = "yyyy/MM/dd"
            val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
            val sdf2 = SimpleDateFormat(pat2, Locale.getDefault())
            val newDate = sdf1.parse(time)
            return sdf2.format(newDate)
        }


        fun timeLastOrNext(nowTime: String, isLast: Boolean): String {
            val pat1 = "yyyyMMddhhmm"
            val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
            val calendar = Calendar.getInstance()
            val date = sdf1.parse(nowTime)
            calendar.time = date
            if (isLast) {
                calendar.add(Calendar.MINUTE, -30)
            } else {
                calendar.add(Calendar.MINUTE, 30)
            }
            return getNowWeatherTime(calendar.time)
        }


        /**
         * 获取距离当前时间最近的时间
         * */
        fun getNowWeatherTime(date: Date?): String {

            val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val nowTime = StringBuilder()
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            val minute = calendar.get(Calendar.MINUTE)
            val hours = calendar.get(Calendar.HOUR_OF_DAY)

            if (hours < 8 || (hours == 8 && minute < 25)) {
                //am 8点20之前，直接是前一天晚上23:45
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                nowTime.append(sdf.format(calendar.time))
                nowTime.append(23 - 8)
                nowTime.append(45)
            } else {
                if (minute >= 55) {
                    //50-59
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", hours))
                    nowTime.append(45)
                } else if (minute >= 25) {
                    //20-49
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", hours - 8))
                    nowTime.append(15)
                } else {
                    //20分之前计算为上一个小时的45分
                    calendar.add(Calendar.HOUR, -1)
                    val newHour = calendar.get(Calendar.HOUR_OF_DAY)
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", newHour - 8))
                    nowTime.append(45)
                }
            }
            return nowTime.toString()
        }
    }

}