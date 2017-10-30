package com.jin123d.factory

import com.jin123d.weathercloud.Const
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tianGe_jin123d on 2017/9/28.
 **/
class CmaDateFactory : PmscDateFactory() {

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
        url.append(Const.cmaUrl)
        url.append(tempTime.subSequence(0, 8))
        url.append(Const.cmaNormalUrl)
        url.append(tempTime)
        url.append(Const.endUrl)
        return url.toString()
    }

}