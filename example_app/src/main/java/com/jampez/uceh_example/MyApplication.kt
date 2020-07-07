package com.jampez.uceh_example

import android.app.Application
import com.jampez.uceh.features.bitbucket.BitBucket
import com.jampez.uceh.features.github.Github
import com.jampez.uceh.features.uce.UCEHandler
import com.jampez.uceh.utils.Mode

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
        uceHandlerBuilder.setIssueMode(Mode.Manual)
        uceHandlerBuilder.setIssueButtonText("Create a Support Ticket")
        uceHandlerBuilder.setGithubService(Github.Builder()
                .setAccessToken("c1510626614c54859a3af2a30cf486e20f0c025c")
                .setRepoName("UCE-Handler")
                .setUsername("jampez77")
        )
        uceHandlerBuilder.setBitBucketService(BitBucket.Builder()
                .setUsername("jampez77_")
                .setAppPassword("t78b8AjfrGxYWwA3gRre")
                .setProjectName("metro-times-android")
                .setRepoName("metrotimes")
        )
        uceHandlerBuilder.build()
    }
}