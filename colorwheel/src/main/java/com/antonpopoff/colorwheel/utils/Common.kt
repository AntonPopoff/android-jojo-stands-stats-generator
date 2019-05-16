package com.antonpopoff.colorwheel.utils

import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

fun createThumbDrawable() = LayerDrawable(arrayOf(
        ShapeDrawable(OvalShape()).apply { paint.color = Color.GRAY }, // Shadow Drawable
        ShapeDrawable(OvalShape()).apply { paint.color = Color.WHITE }, // Thumb Drawable
        ShapeDrawable(OvalShape()) // Color Indicator Drawable
))
