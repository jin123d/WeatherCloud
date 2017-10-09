package com.jin123d.weathercloud

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.jin123d.factory.DateFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var time = "201709280045"
    private var dateFactory: IDateFactory = DateFactory.create(DateFactory.ApiType.SINA)
    private var options: RequestOptions? = null
    private var mToast: Toast? = null
    private var paint = Paint()


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

        paint.color = Color.RED
        paint.strokeWidth = 5f
        paint.isAntiAlias = true

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
            /* GlideApp.with(this).load(url).placeholder(R.mipmap.error).
                     transition(DrawableTransitionOptions.withCrossFade(500))
                     .apply(it)
                     .into(img_weather)*/


            GlideApp.with(this)
                    .asBitmap()
                    .load(url)
                    .placeholder(R.mipmap.error)
                    .apply(it)
                    .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>) {
                            val canvas = Canvas(resource)
                            val x = ((965f / 1720f) * resource.width)
                            val y = ((532f / 1200f) * resource.height)

                            canvas.drawCircle(x, y, 5f, paint)
                            img_weather.setImageBitmap(resource)
                        }

                        override fun onStart() {
                            super.onStart()

                        }
                    })
        }

        val weatherTime = getString(R.string.local_time, dateFactory.weather2LocalTime(time))
        tv_time.text = (weatherTime)
    }


    private fun getLastAndNextWeather(time: String, isLast: Boolean = false, isNext: Boolean = false) {
        val lastTime = dateFactory.timeLastOrNext(time)
        val nextTime = dateFactory.timeLastOrNext(time, false)
        if (isLast && !TextUtils.isEmpty(lastTime)) {
            GlideApp.with(this).download(dateFactory.getUrl(dateFactory.getUrl(lastTime)))
                    .preload()
        }
        if (isNext && !TextUtils.isEmpty(nextTime)) {
            GlideApp.with(this).load(dateFactory.getUrl(dateFactory.getUrl(nextTime)))
                    .preload()
        }
    }

    @SuppressLint("ShowToast")
    private fun toast(msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        }
        mToast?.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.about -> {
                AlertDialog.Builder(this).setTitle(R.string.app_name)
                        .setMessage("v" + packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).versionName)
                        .create().show()
            }

            R.id.setting -> {

            }
        }


        return true
    }

}

