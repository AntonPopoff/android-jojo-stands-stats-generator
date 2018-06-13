package com.antonpopoff.standstatsgenerator.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standstatsgenerator.R
import com.antonpopoff.standstatsgenerator.utils.toRadians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val outerCircleWidth = dpToPx(3f)
    private val innerCircleWidth = dpToPx(2.75f)
    private val ratingLineWidth = dpToPx(3f)
    private val ratingLineAndLetterSpacing = dpToPx(2.5f)
    private val statsMarkLineWidth = dpToPx(1.5f)
    private val spaceBetweenStatsAndBorder = dpToPx(6f)
    private val statNameTextSize = getDimension(R.dimen.stat_name_text_size)
    private val statMarkTextSize = getDimension(R.dimen.stat_mark_text_size)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val borderArcsRect = RectF()
    private val textMeasureRect = Rect()
    private val statsTextPathRect = RectF()
    private val statsTextPath = Path()
    private val statsPolylinePath = Path()

    private val normalFont = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    private val polylineColor = Color.parseColor("#70BF00F0")

    private val testStats = arrayOf(4, 3, 4, 5, 5, 3)

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
        val statsCircleRadius = innerCircleRadius * 0.625f

        drawBorderCircles(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
        drawBorderArcs(canvas, circleCenterX, circleCenterY, outerCircleRadius, innerCircleRadius)
        drawStatsMark(canvas, circleCenterX, circleCenterY, statsCircleRadius)
        drawStatsNames(canvas, circleCenterX, circleCenterY, innerCircleRadius)
        drawStatsRatings(canvas, circleCenterX, circleCenterY, innerCircleRadius, statsCircleRadius)
        drawStatsPolyline(canvas, circleCenterX, circleCenterY, statsCircleRadius)
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

        drawBigArcs(canvas, circleCenterX, circleCenterY)
        drawSmallArcs(canvas, circleCenterX, circleCenterY)
    }

    private fun drawBigArcs(canvas: Canvas, circleCenterX: Float, circleCenterY: Float) {
        val bigArcsStartAngle = 270 - BIG_BORDER_ARC_ANGLE / 2

        canvas.apply {
            drawArc(borderArcsRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
            save()
            rotate(180f, circleCenterX, circleCenterY)
            drawArc(borderArcsRect, bigArcsStartAngle, BIG_BORDER_ARC_ANGLE, false, paint)
            restore()
        }
    }

    private fun drawSmallArcs(canvas: Canvas, circleCenterX: Float, circleCenterY: Float) {
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

    private fun drawStatsMark(canvas: Canvas, circleCenterX: Float, circleCenterY: Float, statsCircleRadius: Float) {
        val statRatingLength = statsCircleRadius / (NUMBER_OF_RATINGS + 1)

        paint.strokeWidth = statsMarkLineWidth

        textPaint.apply {
            textSize = statRatingLength
            typeface = normalFont
        }

        canvas.drawCircle(circleCenterX, circleCenterY, statsCircleRadius, paint)
        drawStatsLines(canvas, circleCenterX, circleCenterY, statsCircleRadius, statRatingLength)
        drawRatingLetters(canvas, circleCenterX, circleCenterY, statRatingLength)
    }

    private fun drawStatsLines(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                               statsCircleRadius: Float, statRatingLength: Float) {
        val angleBetweenStats = 360f / NUMBER_OF_STATS
        val ratingLineStartX = circleCenterX - ratingLineWidth
        val ratingLineEndX = circleCenterX + ratingLineWidth

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
    }

    private fun drawRatingLetters(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                  statRatingLength: Float) {
        val ratingLetterX = circleCenterX + ratingLineWidth + ratingLineAndLetterSpacing

        for (i in 0 until NUMBER_OF_RATINGS) {
            val ratingLineY = circleCenterY - statRatingLength * (i + 1)
            canvas.drawText(RATING_LETTER[i], ratingLetterX, ratingLineY, textPaint)
        }
    }

    private fun drawStatsNames(canvas: Canvas, circleCenterX: Float, circleCenterY: Float, innerCircleRadius: Float) {
        val angleBetweenStats = 360f / NUMBER_OF_STATS

        textPaint.apply {
            typeface = boldFont
            textSize = statNameTextSize
        }

        canvas.save()

        for ((index, stat) in STATS.withIndex()) {
            textPaint.getTextBounds(stat, 0, stat.length, textMeasureRect)

            val textWidth = textPaint.measureText(stat)
            val pathTextRadius = calcPathTextRadius(index, innerCircleRadius)
            val arcAngle = ((textWidth * 180) / (PI * pathTextRadius)).toFloat()
            val sweepAngle = if (index < STATS.size / 2) arcAngle else -arcAngle
            val startAngle = (270 - angleBetweenStats - sweepAngle / 2) % 360

            statsTextPathRect.apply {
                left = circleCenterX - pathTextRadius
                top = circleCenterY - pathTextRadius
                right = circleCenterX + pathTextRadius
                bottom = circleCenterY + pathTextRadius
            }

            statsTextPath.rewind()
            statsTextPath.addArc(statsTextPathRect, startAngle, sweepAngle)
            canvas.drawTextOnPath(stat, statsTextPath, 0f, 0f, textPaint)
            canvas.rotate(angleBetweenStats, circleCenterX, circleCenterY)
        }

        canvas.restore()
    }

    private fun drawStatsRatings(canvas: Canvas, circleCenterX: Float, circleCenterY: Float,
                                 innerCircleRadius: Float, statsCircleRadius: Float) {
        textPaint.textSize = statMarkTextSize

        val deltaAngle = 360 / NUMBER_OF_STATS
        var currentAngle = 270 - deltaAngle
        val letterToDraw = "A"

        for (i in 0 until NUMBER_OF_STATS) {
            val statName = STATS[i]
            textPaint.getTextBounds(statName, 0, statName.length, textMeasureRect)

            val radiusWithTextHeight = innerCircleRadius - textMeasureRect.height() - spaceBetweenStatsAndBorder
            val r = radiusWithTextHeight - (radiusWithTextHeight - statsCircleRadius) / 2
            val letterWidth = textPaint.measureText(letterToDraw)

            textPaint.getTextBounds(letterToDraw, 0, letterToDraw.length, textMeasureRect)

            val radians = toRadians(currentAngle.toFloat())
            val letterX = r * cos(radians) + circleCenterX - letterWidth / 2
            val letterY = r * sin(radians) + circleCenterY + textMeasureRect.height() / 2

            canvas.drawText(letterToDraw, letterX, letterY, textPaint)
            currentAngle = (currentAngle + deltaAngle) % 360
        }
    }

    private fun calcPathTextRadius(index: Int, innerCircleRadius: Float) = if (index < STATS.size / 2) {
        innerCircleRadius - textMeasureRect.height() - spaceBetweenStatsAndBorder
    } else {
        innerCircleRadius - spaceBetweenStatsAndBorder
    }

    private fun drawStatsPolyline(canvas: Canvas, circleCenterX: Float, circleCenterY: Float, statsCircleRadius: Float) {
        val statLineRatingLength = statsCircleRadius / (NUMBER_OF_RATINGS + 1)
        val deltaAngle = 60
        val startAngle = 270 - deltaAngle
        var currentAngle = startAngle

        paint.apply {
            color = polylineColor
            style = Paint.Style.FILL
        }

        for ((index, stat) in testStats.withIndex()) {
            val r = statLineRatingLength * stat
            val radians = toRadians(currentAngle.toFloat())
            val pointX = r * cos(radians) + circleCenterX
            val pointY = r * sin(radians) + circleCenterY

            if (index == 0) {
                statsPolylinePath.moveTo(pointX, pointY)
            } else {
                statsPolylinePath.lineTo(pointX, pointY)
            }

            currentAngle = (currentAngle + deltaAngle) % 360
        }

        statsPolylinePath.close()

        canvas.drawPath(statsPolylinePath, paint)
    }

    companion object {

        private const val BIG_BORDER_ARC_ANGLE = 3.5f
        private const val SMALL_BORDER_ARC_ANGLE = 2.5f
        private const val NUMBER_OF_SMALL_BORDER_ARCS = 10
        private const val NUMBER_OF_STATS = 6
        private const val NUMBER_OF_RATINGS = 5
        private val RATING_LETTER = arrayOf("E", "D", "C", "B", "A")
        private val STATS = arrayOf("POTENTIAL", "POWER", "SPEED", "RANGE", "DURABILITY", "PRECISION")
    }
}
