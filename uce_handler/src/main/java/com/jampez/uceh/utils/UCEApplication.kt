package com.jampez.uceh.utils

import android.app.Application
import com.jampez.uceh.data.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                    modules = listOf(viewModelModule)
            )
        }


    }
}