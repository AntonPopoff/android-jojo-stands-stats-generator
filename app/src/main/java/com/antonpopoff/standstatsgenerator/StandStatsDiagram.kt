package com.antonpopoff.standstatsgenerator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val availableWidth = (width - paddingLeft - paddingRight).toFloat()
        val availableHeight = (height - paddingTop - paddingBottom).toFloat()
        val circleCenterX = availableWidth / 2 + paddingRight
        val circleCenterY = availableHeight / 2 + paddingTop
        val outerCircleRadius = minOf(availableWidth, availableHeight) / 2
        val innerCircleRadius = outerCircleRadius * 0.9f

        drawBorderRings(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
    }

    private fun drawBorderRings(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                outerRingRadius: Float, innerRingRadius: Float) {
        val circleWidth = resources.displayMetrics.density * OUTER_RING_WIDTH

        paint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = circleWidth
        }

        canvas.apply {
            drawCircle(circleCenterX, circleCenterY, outerRingRadius, paint)
            drawCircle(circleCenterX, circleCenterY, innerRingRadius, paint)
        }
    }

    companion object {

        private const val OUTER_RING_WIDTH = 3
    }
}