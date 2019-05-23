package com.antonpopoff.colorwheel.utils


fun clearAlpha(argb: Int): Int = (argb shl 8 ushr 8)

fun setAlpha(argb: Int, alpha: Int) = clearAlpha(argb) or (alpha shl 24)
