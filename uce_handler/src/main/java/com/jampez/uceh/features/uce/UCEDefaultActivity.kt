package com.jampez.uceh.features.uce

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jampez.uceh.R
import com.jampez.uceh.features.supportissue.supportIssueModule
import com.jampez.uceh.data.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class UCEDefaultActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(
                    modules = listOf(
                            viewModelModule,
                            supportIssueModule
                    )
            )
        }

        if (Build.VERSION.SDK_INT > 21)
            setTheme(android.R.style.Theme_Material)
        else
            setTheme(android.R.style.Theme)

        setContentView(R.layout.default_error_activity)
    }
}