package com.antonpopoff.colorwheel.utils

import android.graphics.Color

class HSVColor {

    private val hsvComponents = floatArrayOf(0f, 0f, 0f)

    val hue get() = hsvComponents[0]

    val saturation get() = hsvComponents[1]

    val value get() = hsvComponents[2]

    fun set(hue: Float, saturation: Float, value: Float) {
        hsvComponents.apply {
            set(0, hue)
            set(1, saturation)
            set(2, value)
        }
    }

    fun set(argb: Int) = Color.colorToHSV(argb, hsvComponents)

    fun toArgb() = Color.HSVToColor(hsvComponents)
}
