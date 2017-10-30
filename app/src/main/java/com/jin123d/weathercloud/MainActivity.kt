package com.jin123d.weathercloud

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.amap.api.services.weather.LocalWeatherLive
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.jin123d.factory.DateFactory
import com.jin123d.location.CustomAmapLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class MainActivity : AppCompatActivity() {

    private var time = "201709280045"
    private val TAG = this.javaClass.simpleName
    private var dateFactory = DateFactory.create(DateFactory.ApiType.CMA)
    private lateinit var amapLocation: CustomAmapLocation
    private var options: RequestOptions? = null
    private var paint = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permission()

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
        Log.d(TAG, url)
        options?.let {
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.about -> {
                alert {
                    titleResource = R.string.app_name
                    message = "v" + packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).versionName
                }.show()
            }

            R.id.setting -> {


            }
        }
        return true
    }


    private fun getLocation() {
        amapLocation = CustomAmapLocation(this)
        amapLocation.location(object : CustomAmapLocation.LocationSuccess {
            override fun weather(weatherLive: LocalWeatherLive) {
                val text = weatherLive.city + "当前天气" + "\n" +
                        "天气：" + weatherLive.weather + "\n" +
                        "温度：" + weatherLive.temperature + "\n" +
                        "湿度：" + weatherLive.humidity
                tv_weather.text = text
            }

            override fun success(latitude: Double, longitude: Double) {
                //定位成功
                Log.d(TAG, "$latitude---$longitude")
            }
        })
    }


    /**
     * 申请权限
     */
    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //拒绝权限以后
                //showMessageOKCancel()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        Const.ACCESS_LOCATION_CODE)
            }
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Const.ACCESS_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意授权
                getLocation()
            } else {
                //拒绝授权后重新申请
                permission()
            }
        }
    }


    private fun showMessageOKCancel() {
        alert("需要授予定位权限") {
            yesButton {
                val packageURI = Uri.parse("package:" + packageName)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                startActivity(intent)
            }
            noButton { finish() }
        }.show()
    }

}

