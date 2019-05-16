package com.antonpopoff.colorwheel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

class ThumbDrawable {

    private val colorIndicatorDrawable = ShapeDrawable(OvalShape())

    private val thumbDrawable = LayerDrawable(arrayOf(
            ShapeDrawable(OvalShape()).apply { paint.color = Color.GRAY }, // Shadow Drawable
            ShapeDrawable(OvalShape()).apply { paint.color = Color.WHITE }, // Thumb Drawable
            colorIndicatorDrawable
    ))

    var bounds: Rect
        get() = thumbDrawable.bounds
        set(value) { thumbDrawable.bounds = value }

    var indicatorColor
        get() = colorIndicatorDrawable.paint.color
        set(value) { colorIndicatorDrawable.paint.color = value }

    fun applyInsets(thumbRadius: Float) {
        val shadowHInset = (thumbRadius * 0.1f).toInt()
        val shadowVInset = (thumbRadius * 0.1f).toInt()

        val colorHInset = (thumbRadius * 0.25f).toInt()
        val colorVInset = (thumbRadius * 0.25f).toInt()

        thumbDrawable.apply {
            setLayerInset(0, shadowHInset, shadowVInset, -shadowHInset, -shadowVInset)
            setLayerInset(2, colorHInset, colorVInset, colorHInset, colorVInset)
        }
    }

    fun draw(canvas: Canvas) = thumbDrawable.draw(canvas)
}
