package com.jampez.uceh.features.uce

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.jampez.uceh.R
import com.jampez.uceh.data.modules.githubModule
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
                            githubModule
                    )
            )
        }

        if (Build.VERSION.SDK_INT > 21)
            setTheme(android.R.style.Theme_Material) else setTheme(android.R.style.Theme)

        if (UCEHandler.showAsDialog) {
            if (Build.VERSION.SDK_INT > 21)
                setTheme(android.R.style.Theme_Material_Dialog) else setTheme(android.R.style.Theme_Dialog)
            setFinishOnTouchOutside(false)
        }

        if (!UCEHandler.showTitle)
            requestWindowFeature(Window.FEATURE_NO_TITLE)

        if (UCEHandler.showAsDialog)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setContentView(R.layout.default_error_activity)
    }
}