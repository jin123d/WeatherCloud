package com.jin123d.factory

import com.jin123d.weathercloud.IDateFactory
import com.jin123d.weathercloud.Const
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tianGe_jin123d on 2017/9/28.
 **/
class SinaDateFactory : IDateFactory {


    override fun getUrl(time: String): String {
        val tempTime = if (time.length != 16) {
            getWeatherTime()
        } else {
            time
        }
        val url = StringBuilder()
        url.append(Const.sinaUrl)
        url.append(tempTime)
        url.append(Const.sinaEndUel)
        return url.toString()
    }


    override fun timeLastOrNext(nowTime: String, isLast: Boolean): String {
        val pat1 = "yyyy-MM-dd-HH-mm" //201710012300
        val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf1.parse(nowTime)
        calendar.time = date
        calendar.add(Calendar.MINUTE, if (isLast) -30 else 30) //201709301605
        if (isLast && calendar.get(Calendar.HOUR_OF_DAY) == 7 && calendar.get(Calendar.MINUTE) == 30) {
            calendar.add(Calendar.HOUR_OF_DAY, -8)
            //calendar.add(Calendar.MINUTE, 30) //201709301605
        } else if (!isLast && calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.add(Calendar.HOUR_OF_DAY, 8)
            //calendar.add(Calendar.MINUTE, 30) //201709301605
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
        val pat1 = "yyyy-MM-dd-HH-mm" //20170931515
        val pat2 = "yyyy/MM/dd HH:mm"
        val sdf1 = SimpleDateFormat(pat1, Locale.getDefault())
        val sdf2 = SimpleDateFormat(pat2, Locale.getDefault())
        val date = sdf1.parse(weatherTime)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, 15)
        return sdf2.format(calendar.time)
    }


    override fun getWeatherTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd-", Locale.getDefault())

        val nowTime = StringBuilder()
        val calendar = Calendar.getInstance()
        val minute = calendar.get(Calendar.MINUTE)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)

        if (hours < 9) {
            //am 4点00之前，直接是前一天晚上23:45-
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            nowTime.append(sdf.format(calendar.time))
            nowTime.append("23")
            nowTime.append("-")
            nowTime.append("30")
        } else {
            when {
                minute > 50 -> {
                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(hours)
                    nowTime.append("-")
                    nowTime.append("00")
                }
                minute > 15 -> {
                    //50分之前计算为上一个小时的45分
                    calendar.add(Calendar.HOUR, -1)
                    val newHour = calendar.get(Calendar.HOUR_OF_DAY)

                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", newHour))
                    nowTime.append("-")
                    nowTime.append("30")
                }
                else -> {
                    //15之前为上一个小时00
                    calendar.add(Calendar.HOUR, -1)
                    val newHour = calendar.get(Calendar.HOUR_OF_DAY)

                    nowTime.append(sdf.format(calendar.time))
                    nowTime.append(String.format("%02d", newHour))
                    nowTime.append("-")
                    nowTime.append("00")
                }

            }

        }
        return nowTime.toString()
    }

}