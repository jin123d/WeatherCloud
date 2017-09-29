package com.jin123d.weathercloud

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var time = "201709280045"
    var requestBuilder: RequestBuilder<Drawable>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_now_time.text = "当前最新云图时间" + DateUtil.getNowWeatherTime(null)

        time = DateUtil.getNowWeatherTime(null)
        requestBuilder = Glide.with(this).load(DateUtil.getUrl(time))

        getWeather(time)

        btn_get_weather.setOnClickListener {
            time = et_date.text.toString().trim()
            val hour = et_time.text.toString().trim()
            if (TextUtils.isEmpty(time)) {
                time = DateUtil.getNowWeatherTime(null)
            } else {
                time += hour
            }
            getWeather(time)
        }


        btn_last.setOnClickListener {
            getWeather(DateUtil.timeLastOrNext(time,true))
        }

        btn_next.setOnClickListener {
            getWeather(DateUtil.timeLastOrNext(time,false))
        }
    }

    /**
     *
     * */
    fun getWeather(time: String) {
        val url = DateUtil.getUrl(time)
        Log.d("url", url)
        requestBuilder!!.load(url).into(img_weather)
    }

}

