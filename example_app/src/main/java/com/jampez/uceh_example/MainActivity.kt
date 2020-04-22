package com.jampez.uceh_example

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())
        setContentView(R.layout.activity_main)
    }

    fun onCrashButtonClicked(view: View?) {
        Toast.makeText(this, "App Crashed", Toast.LENGTH_SHORT).show()
        throw RuntimeException("Hey! App Crashed.")
    }
}