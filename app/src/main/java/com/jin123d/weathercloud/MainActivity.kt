package com.jin123d.weathercloud

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var time = "201709280045"
    private var mToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        init()

        btn_last.setOnClickListener {
            getLastOrNext()
        }

        btn_next.setOnClickListener {
            getLastOrNext(false)
        }

        btn_now.setOnClickListener {
            time = DateUtil.getWeatherTime()
            getWeather(time)

            val weatherTime = getString(R.string.local_time, DateUtil.weather2LocalTime(time))
            tv_now_time.text = (weatherTime)
        }
    }

    private fun init() {
        time = DateUtil.getWeatherTime()
        tv_now_time.text = (getString(R.string.weather_time, DateUtil.weather2LocalTime(time)))
        getWeather(time)
        getLastAndNextWeather(time, true, true)

    }

    private fun getLastOrNext(isLast: Boolean = true) {
        time = DateUtil.timeLastOrNext(time, isLast)

        if (TextUtils.isEmpty(time)) {
            toast("无最新云图")
            time = DateUtil.getWeatherTime()
        }
        getWeather(time)
        getLastAndNextWeather(time, isLast, !isLast)
    }

    private fun getWeather(time: String) {
        val url = DateUtil.getUrl(time)
        Log.d("url", url)
        GlideApp.with(this).load(url).into(img_weather)

        val weatherTime = getString(R.string.local_time, DateUtil.weather2LocalTime(time))
        tv_time.text = (weatherTime)
    }


    private fun getLastAndNextWeather(time: String, isLast: Boolean = false, isNext: Boolean = false) {
        val lastTime = DateUtil.timeLastOrNext(time)
        val nextTime = DateUtil.timeLastOrNext(time, false)
        if (isLast && !TextUtils.isEmpty(lastTime)) {
            GlideApp.with(this).download(DateUtil.getUrl(DateUtil.getUrl(lastTime)))
        }
        if (isNext && !TextUtils.isEmpty(nextTime)) {
            GlideApp.with(this).download(DateUtil.getUrl(DateUtil.getUrl(nextTime)))
        }
    }

    private fun toast(msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(msg)
        }
        mToast!!.show()
    }


}

