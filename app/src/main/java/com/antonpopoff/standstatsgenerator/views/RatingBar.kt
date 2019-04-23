package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RatingBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val selectedBarColor = Color.parseColor("#EA7371")
    private val unselectedBarColor = Color.parseColor("#F1B8B8")

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = unselectedBarColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.color = selectedBarColor
        canvas.drawRect(0f, 0f, width.toFloat() / 2, height.toFloat(), paint)
    }
}
