package com.jampez.uceh_example

import android.app.Application
import com.jampez.uceh.UCEHandler

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Initialize UCE Handler library
        val uceHandlerBuilder = UCEHandler.Builder(applicationContext)
        uceHandlerBuilder.setTrackActivitiesEnabled(true)
        uceHandlerBuilder.setIconDrawable(R.mipmap.ic_launcher)
        uceHandlerBuilder.setBackgroundModeEnabled(true)
        uceHandlerBuilder.build()
    }
}