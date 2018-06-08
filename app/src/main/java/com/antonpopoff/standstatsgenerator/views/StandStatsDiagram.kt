package com.antonpopoff.standstatsgenerator.views

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

        val circleWidth = pxToDp(OUTER_RING_WIDTH)
        val availableWidth = (width - paddingLeft - paddingRight - circleWidth)
        val availableHeight = (height - paddingTop - paddingBottom - circleWidth)
        val circleCenterX = availableWidth / 2 + paddingLeft + circleWidth / 2
        val circleCenterY = availableHeight / 2 + paddingTop + circleWidth / 2
        val outerCircleRadius = minOf(availableWidth, availableHeight) / 2
        val innerCircleRadius = outerCircleRadius * 0.9f

        drawBorderRings(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius,
                circleWidth)
    }

    private fun drawBorderRings(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                outerCircleRadius: Float, innerCircleRadius: Float,
                                circleWidth: Float) {
        paint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = circleWidth
        }

        canvas.apply {
            drawCircle(circleCenterX, circleCenterY, outerCircleRadius, paint)
            drawCircle(circleCenterX, circleCenterY, innerCircleRadius, paint)
        }
    }

    companion object {

        private const val OUTER_RING_WIDTH = 3
    }
}
