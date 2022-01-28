package com.jampez.uceh_example

import android.app.Application
import com.jampez.uceh.features.bitbucket.BitBucket
import com.jampez.uceh.features.github.Github
import com.jampez.uceh.features.gitlab.GitLab
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
        uceHandlerBuilder.setCanShareErrorLog(false)
        uceHandlerBuilder.setIssueMode(Mode.Manual)
        uceHandlerBuilder.setIssueButtonText("Create a Support Ticket")
        uceHandlerBuilder.setGithubService(Github.Builder()
                .setAccessToken("<< github access token >>")
                .setRepoName("<< repo name >>")
                .setUsername("<< username >>")
        )
        uceHandlerBuilder.setBitBucketService(BitBucket.Builder()
                .setUsername("<< username >>")
                .setAppPassword("<< app password >>")
                .setProjectName("<< project name >>")
                .setRepoName("<< repo name >>")
        )
        uceHandlerBuilder.setGitLabService(GitLab.Builder()
                .setAccessToken("<< access token >>")
                .setProjectID("<< project id >>")
        )
        uceHandlerBuilder.build()
    }
}