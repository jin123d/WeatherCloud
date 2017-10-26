package com.jin123d.factory

import com.jin123d.weathercloud.IDateFactory
import com.jin123d.weathercloud.Const
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tianGe_jin123d on 2017/9/28.
 **/
class NmcDateFactory : IDateFactory {

    /**
     * 格式必须为201709200930
     * */
    override fun getUrl(time: String): String {
        val tempTime = if (time.length != 12) {
            getWeatherTime()
        } else {
            time
        }
        val url = StringBuilder()
        url.append(Const.baseUrl)
        url.append(time2Date(tempTime))
        url.append(Const.normalUrl)
        url.append(tempTime)
        url.append(Const.endUrl)
        return url.toString()
    }


    /**
     * 将2017082009391245转换为2017/08/20
     *
     * */
    private fun time2Date(time: String): String {
        val pat1 = "yyyyMMddHHmm"
        val pat2 = "yyyy/MM/dd"
        val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
        val sdf2 = SimpleDateFormat(pat2, Locale.getDefault())
        val newDate = sdf1.parse(time)
        return sdf2.format(newDate)
    }


    override fun timeLastOrNext(nowTime: String, isLast: Boolean): String {
        val pat1 = "yyyyMMddHHmm" //20170931515
        val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf1.parse(nowTime)
        calendar.time = date
        calendar.add(Calendar.MINUTE, if (isLast) -30 else 30) //201709301605
        if (isLast && calendar.get(Calendar.HOUR_OF_DAY) == 23) {
            calendar.add(Calendar.HOUR_OF_DAY, -8)
        } else if (!isLast && calendar.get(Calendar.HOUR_OF_DAY) == 16) {
            calendar.add(Calendar.HOUR_OF_DAY, 8)
        }
        if (!isLast) {
            val phoneTime = sdf1.parse(getWeatherTime()).time
            val weatherTime = calendar.time.time
            val diff = (phoneTime - weatherTime) / (60 * 1000)
            if (diff < 10) {
                //说明没有最新的图片
                return ""
            }
        }
        return sdf1.format(calendar.time)
    }


    override fun weather2LocalTime(weatherTime: String): String {
        val pat1 = "yyyyMMddHHmm" //20170931515
        val pat2 = "yyyy/MM/dd HH:mm"
        val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
        val sdf2 = SimpleDateFormat(pat2, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf1.parse(weatherTime)
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, 8)
        return sdf2.format(calendar.time)
    }


    /**
     * 获取距离当前时间最近的时间
     * */
    override fun getWeatherTime(): String {

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val nowTime = StringBuilder()
        val calendar = Calendar.getInstance()

        val minute = calendar.get(Calendar.MINUTE)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)

        if (hours < 8 || (hours == 8 && minute < 25)) {
            //am 8点20之前，直接是前一天晚上23:45
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            nowTime.append(sdf.format(calendar.time))
            nowTime.append(23 - 8)
            nowTime.append(45)
        } else {
            when {
                minute >= 55 -> {
                    //55-59
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", hours - 8))
                    nowTime.append(45)
                }
                minute >= 25 -> {
                    //25-55
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", hours - 8))
                    nowTime.append(15)
                }
                else -> {
                    //25分之前计算为上一个小时的45分
                    calendar.add(Calendar.HOUR, -1)
                    val newHour = calendar.get(Calendar.HOUR_OF_DAY)
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", newHour - 8))
                    nowTime.append(45)
                }
            }
        }
        return nowTime.toString()
    }

}