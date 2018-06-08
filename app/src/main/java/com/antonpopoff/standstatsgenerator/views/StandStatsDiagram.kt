package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 112f
    }
    private val arcRect = RectF()
    private val textMeasureRect = Rect()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val outerCircleWidth = pxToDp(OUTER_CIRCLE_BORDER_WIDTH)
        val innerCircleWidth = pxToDp(INNER_CIRCLE_BORDER_WIDTH)
        val availableWidth = (width - paddingLeft - paddingRight - outerCircleWidth)
        val availableHeight = (height - paddingTop - paddingBottom - outerCircleWidth)
        val circleCenterX = availableWidth / 2 + paddingLeft + outerCircleWidth / 2
        val circleCenterY = availableHeight / 2 + paddingTop + outerCircleWidth / 2
        val outerCircleRadius = minOf(availableWidth, availableHeight) / 2
        val innerCircleRadius = outerCircleRadius * 0.9f

        drawBorderCircles(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius,
                outerCircleWidth, innerCircleWidth)
        drawBorderArcs(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
        drawStatsMark(canvas, circleCenterX, circleCenterY, innerCircleRadius)
    }

    private fun drawBorderCircles(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                  outerCircleRadius: Float, innerCircleRadius: Float,
                                  outerCircleWidth: Float, innerCircleWidth: Float) {
        paint.style = Paint.Style.STROKE

        canvas.apply {
            paint.strokeWidth = outerCircleWidth
            drawCircle(circleCenterX, circleCenterY, outerCircleRadius, paint)
            paint.strokeWidth = innerCircleWidth
            drawCircle(circleCenterX, circleCenterY, innerCircleRadius, paint)
        }
    }

    private fun drawBorderArcs(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                               outerCircleRadius: Float, innerCircleRadius: Float) {
        val arcStrokeWidth = outerCircleRadius - innerCircleRadius
        val bigArcsStartAngle = 270 - BIG_BORDER_ARC_ANGLE / 2

        paint.apply {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = arcStrokeWidth
        }

        arcRect.apply {
            left = circleCenterX - outerCircleRadius
            top = circleCenterY - outerCircleRadius + arcStrokeWidth / 2
            right = circleCenterX + outerCircleRadius
            bottom = circleCenterY + outerCircleRadius
        }

        canvas.apply {
            drawArc(arcRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
            save()
            rotate(180f, circleCenterX, circleCenterY)
            drawArc(arcRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
            restore()
        }

        val availableAngle = 180 - BIG_BORDER_ARC_ANGLE
        val spaceAngleBetweenArcs = availableAngle / (NUMBER_OF_SMALL_BORDER_ARCS + 1)
        val smallArcsStartAngle = 270 - SMALL_BORDER_ARC_ANGLE / 2

        canvas.apply {
            save()
            rotate(BIG_BORDER_ARC_ANGLE / 2 + spaceAngleBetweenArcs, circleCenterX, circleCenterY)

            for (i in 0 until NUMBER_OF_SMALL_BORDER_ARCS * 2) {
                if (i == NUMBER_OF_SMALL_BORDER_ARCS) {
                    rotate(BIG_BORDER_ARC_ANGLE + spaceAngleBetweenArcs, circleCenterX, circleCenterY)
                }

                drawArc(arcRect, smallArcsStartAngle, SMALL_BORDER_ARC_ANGLE, false, paint)
                rotate(spaceAngleBetweenArcs, circleCenterX, circleCenterY)
            }

            restore()
        }
    }

    private fun drawStatsMark(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                              innerCircleRadius: Float) {
        val statsCircleRadius = innerCircleRadius * 0.6f
        var angle = 270

        paint.strokeWidth = pxToDp(STATS_CIRCLE_BORDER_WIDTH)

        canvas.drawCircle(circleCenterX, circleCenterY, statsCircleRadius, paint)

        for (i in 0 until 6) {
            val oppositePointAngle = (angle + 180) % 360
            val oppositeAngleRadians = Math.toRadians(oppositePointAngle.toDouble())
            val radians = Math.toRadians(angle.toDouble())
            val startPointX = (statsCircleRadius * cos(radians) + circleCenterX).toFloat()
            val startPointY = (statsCircleRadius * sin(radians) + circleCenterY).toFloat()
            val oppositePointX = (statsCircleRadius * cos(oppositeAngleRadians) + circleCenterX).toFloat()
            val oppositePointY = (statsCircleRadius * sin(oppositeAngleRadians) + circleCenterY).toFloat()
            canvas.drawLine(startPointX, startPointY, oppositePointX, oppositePointY, paint)
            angle += 60
        }
    }

    companion object {

        private const val OUTER_CIRCLE_BORDER_WIDTH = 3f
        private const val INNER_CIRCLE_BORDER_WIDTH = 2.75f
        private const val STATS_CIRCLE_BORDER_WIDTH = 1.5f
        private const val BIG_BORDER_ARC_ANGLE = 3.5f
        private const val SMALL_BORDER_ARC_ANGLE = 2.5f
        private const val NUMBER_OF_SMALL_BORDER_ARCS = 10
    }
}
