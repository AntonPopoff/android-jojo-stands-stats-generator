package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val borderArcsRect = RectF()

    private val outerCircleWidth = pxToDp(3f)
    private val innerCircleWidth = pxToDp(2.75f)
    private val ratingLineWidth = pxToDp(3f)
    private val ratingLineAndLetterSpacing = pxToDp(2.5f)
    private val statsMarkLineWidth = pxToDp(1.5f)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val availableWidth = (width - paddingLeft - paddingRight - outerCircleWidth)
        val availableHeight = (height - paddingTop - paddingBottom - outerCircleWidth)
        val circleCenterX = availableWidth / 2 + paddingLeft + outerCircleWidth / 2
        val circleCenterY = availableHeight / 2 + paddingTop + outerCircleWidth / 2
        val outerCircleRadius = minOf(availableWidth, availableHeight) / 2
        val innerCircleRadius = outerCircleRadius * 0.9f

        drawBorderCircles(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
        drawBorderArcs(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
        drawStatsMark(canvas, circleCenterX, circleCenterY, innerCircleRadius)
    }

    private fun drawBorderCircles(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                  outerCircleRadius: Float, innerCircleRadius: Float) {
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

        borderArcsRect.apply {
            left = circleCenterX - outerCircleRadius
            top = circleCenterY - outerCircleRadius + arcStrokeWidth / 2
            right = circleCenterX + outerCircleRadius
            bottom = circleCenterY + outerCircleRadius
        }

        canvas.apply {
            drawArc(borderArcsRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
            save()
            rotate(180f, circleCenterX, circleCenterY)
            drawArc(borderArcsRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
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

                drawArc(borderArcsRect, smallArcsStartAngle, SMALL_BORDER_ARC_ANGLE, false, paint)
                rotate(spaceAngleBetweenArcs, circleCenterX, circleCenterY)
            }

            restore()
        }
    }

    private fun drawStatsMark(canvas: Canvas, circleCenterX: Float, circleCenterY: Float, innerCircleRadius: Float) {
        val statsCircleRadius = innerCircleRadius * 0.625f
        val statRatingLength = statsCircleRadius / (NUMBER_OF_RATINGS + 1)
        val angleBetweenStats = 60f
        val ratingLineStartX = circleCenterX - ratingLineWidth
        val ratingLineEndX = circleCenterX + ratingLineWidth

        paint.strokeWidth = statsMarkLineWidth
        textPaint.textSize = statRatingLength

        canvas.drawCircle(circleCenterX, circleCenterY, statsCircleRadius, paint)
        canvas.save()

        for (i in 0 until NUMBER_OF_STATS) {
            canvas.drawLine(circleCenterX, circleCenterY, circleCenterX, circleCenterY - statsCircleRadius, paint)

            for (j in 0 until NUMBER_OF_RATINGS) {
                val ratingLineY = circleCenterY - statRatingLength * (j + 1)
                canvas.drawLine(ratingLineStartX, ratingLineY, ratingLineEndX, ratingLineY, paint)
            }

            canvas.rotate(angleBetweenStats, circleCenterX, circleCenterY)
        }

        canvas.restore()

        val ratingLetterX = circleCenterX + ratingLineWidth + ratingLineAndLetterSpacing

        for (i in 0 until NUMBER_OF_RATINGS) {
            val ratingLineY = circleCenterY - statRatingLength * (i + 1)
            canvas.drawText(RATING_LETTER[i], ratingLetterX, ratingLineY, textPaint)
        }
    }

    companion object {

        private const val BIG_BORDER_ARC_ANGLE = 3.5f
        private const val SMALL_BORDER_ARC_ANGLE = 2.5f
        private const val NUMBER_OF_SMALL_BORDER_ARCS = 10
        private const val NUMBER_OF_STATS = 6
        private const val NUMBER_OF_RATINGS = 5
        private val RATING_LETTER = arrayOf("E", "D", "C", "B", "A")
    }
}
