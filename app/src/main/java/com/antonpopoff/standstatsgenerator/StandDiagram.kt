package com.antonpopoff.standstatsgenerator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class StandDiagram @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Common coordinates
        val availableWidth = (width - paddingLeft - paddingRight).toFloat()
        val availableHeight = (height - paddingTop - paddingBottom).toFloat()
        val circleCenterX = availableWidth / 2 + paddingRight
        val circleCenterY = availableHeight / 2 + paddingTop
        val outerCircleRadius = minOf(availableWidth, availableHeight) / 2
        val innerCircleRadius = outerCircleRadius * 0.9f

        drawBorderRings(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)

        val fatLinePath = Path()
        val angle = (360 - 30) % 360
        val angleRadians = (angle * Math.PI) / 180
        val bottomArcX = innerCircleRadius * Math.cos(angleRadians).toFloat() + circleCenterX
        val bottomArcY = innerCircleRadius * Math.sin(angleRadians).toFloat() + circleCenterY

        fatLinePath.moveTo(circleCenterX, circleCenterY)
        fatLinePath.lineTo(bottomArcX, bottomArcY)

        paint.color = Color.BLUE
        canvas.drawPath(fatLinePath, paint)
    }

    private fun drawBorderRings(canvas: Canvas, ringCenterX: Float, ringCenterY: Float,
                                outerRingRadius: Float, innerRingRadius: Float) {
        val circleWidth = resources.displayMetrics.density * OUTER_RING_WIDTH

        paint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = circleWidth
        }

        canvas.apply {
            drawCircle(ringCenterX, ringCenterY, outerRingRadius, paint)
            drawCircle(ringCenterX, ringCenterY, innerRingRadius, paint)
        }
    }

    private fun drawFatRects() {
        
    }

    companion object {

        private const val OUTER_RING_WIDTH = 4
    }
}