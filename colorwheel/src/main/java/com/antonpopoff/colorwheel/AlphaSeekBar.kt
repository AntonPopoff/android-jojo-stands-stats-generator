package com.antonpopoff.colorwheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View

class AlphaSeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val gradientColors = IntArray(2)
    private val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColors)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        gradient.apply {
            setBounds(0, 0, width, height)
            draw(canvas)
        }
    }
}
