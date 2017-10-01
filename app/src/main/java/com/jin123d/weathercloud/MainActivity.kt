package com.jin123d.weathercloud

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jin123d.factory.DateFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var time = "201709280045"
    private var dateFactory: IDateFactory = DateFactory.create(DateFactory.ApiType.SINA)
    private var options: RequestOptions? = null
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
            time = dateFactory.getWeatherTime()
            getWeather(time)

            val weatherTime = getString(R.string.local_time, dateFactory.weather2LocalTime(time))
            tv_now_time.text = (weatherTime)
        }
    }

    private fun init() {
        options = RequestOptions()
                .placeholder(R.mipmap.error)
                .error(R.mipmap.error)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        time = dateFactory.getWeatherTime()
        tv_now_time.text = (getString(R.string.weather_time, dateFactory.weather2LocalTime(time)))
        getWeather(time)
        getLastAndNextWeather(time, true, true)


    }

    private fun getLastOrNext(isLast: Boolean = true) {
        time = dateFactory.timeLastOrNext(time, isLast)

        if (TextUtils.isEmpty(time)) {
            toast("无最新云图")
            time = dateFactory.getWeatherTime()
        }
        getWeather(time)
        getLastAndNextWeather(time, isLast, !isLast)
    }

    private fun getWeather(time: String) {
        val url = dateFactory.getUrl(time)
        Log.d("url", url)
        options?.let {
            GlideApp.with(this).load(url).placeholder(R.mipmap.error).
                    transition(DrawableTransitionOptions.withCrossFade(500))
                    .apply(it)
                    .into(img_weather)
        }


        val weatherTime = getString(R.string.local_time, dateFactory.weather2LocalTime(time))
        tv_time.text = (weatherTime)
    }


    private fun getLastAndNextWeather(time: String, isLast: Boolean = false, isNext: Boolean = false) {
        val lastTime = dateFactory.timeLastOrNext(time)
        val nextTime = dateFactory.timeLastOrNext(time, false)
        if (isLast && !TextUtils.isEmpty(lastTime)) {
            options?.let {
                GlideApp.with(this).download(dateFactory.getUrl(dateFactory.getUrl(lastTime)))
                        .apply(it)
                        .preload()
            }
        }
        if (isNext && !TextUtils.isEmpty(nextTime)) {
            options?.let {
                GlideApp.with(this).load(dateFactory.getUrl(dateFactory.getUrl(nextTime)))
                        .apply(it)
                        .preload()
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun toast(msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        }
        mToast?.show()
    }

}

