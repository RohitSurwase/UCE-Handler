package com.jampez.uceh.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat

enum class Mode {
    Automatic, Manual
}

fun Context.getDrawableCompat(id: Int): Drawable?{
    return ContextCompat.getDrawable(this, id)
}

@Suppress("DEPRECATION")
fun Context.getColorCompat(id: Int): Int{
    return if (Build.VERSION.SDK_INT >= 23) {
        ContextCompat.getColor(this, id)
    } else {
        this.resources.getColor(id)
    }
}

fun Context.getBitmap(drawableRes: Int): Bitmap {
    val drawable = this.getDrawableCompat(drawableRes)
    val canvas = Canvas()
    val bitmap = drawable?.intrinsicWidth?.let { Bitmap.createBitmap(it, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) }
    canvas.setBitmap(bitmap)
    drawable?.intrinsicWidth?.let { drawable.setBounds(0, 0, it, drawable.intrinsicHeight) }
    drawable?.draw(canvas)
    return bitmap!!
}