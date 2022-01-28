package com.jampez.uceh.utils

import android.app.Application
import com.jampez.uceh.data.modules.viewModelModule
import com.jampez.uceh.features.supportissue.supportIssueModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(
                listOf(
                    viewModelModule,
                    supportIssueModule
                )
            )
        }


    }
}