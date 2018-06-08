package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcRect = RectF()

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

        drawBorderCircles(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius, circleWidth)
        drawBorderArcs(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
    }

    private fun drawBorderCircles(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
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

    companion object {

        private const val OUTER_RING_WIDTH = 3
        private const val BIG_BORDER_ARC_ANGLE = 3.5f
        private const val SMALL_BORDER_ARC_ANGLE = 2.5f
        private const val NUMBER_OF_SMALL_BORDER_ARCS = 10
    }
}
