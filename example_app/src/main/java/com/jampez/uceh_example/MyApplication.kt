package com.jampez.uceh_example

import android.app.Application
import com.jampez.uceh.features.github.Github
import com.jampez.uceh.features.uce.UCEHandler

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Initialize UCE Handler library
        val uceHandlerBuilder = UCEHandler.Builder(applicationContext)
        uceHandlerBuilder.setTrackActivitiesEnabled(true)
        uceHandlerBuilder.setIconDrawable(R.mipmap.ic_launcher)
        uceHandlerBuilder.setBackgroundModeEnabled(true)
        uceHandlerBuilder.setCanViewErrorLog(false)
        uceHandlerBuilder.setCanCopyErrorLog(false)
        uceHandlerBuilder.setCanShareErrorLog(false)
        uceHandlerBuilder.setCanSaveErrorLog(false)
        uceHandlerBuilder.setGithubService(Github.Builder()
                .setAccessToken("d786ba38971a472f38f6d7fbd40b6c7453506b4c")
                .setRepoName("UCE-Handler")
                .setUsername("jampez77")
                .setButtonText("Create a Support Ticket")
                .setMode(Github.Mode.Manual)
        )
        uceHandlerBuilder.build()
    }
}