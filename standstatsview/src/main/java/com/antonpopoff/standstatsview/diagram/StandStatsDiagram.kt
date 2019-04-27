package com.antonpopoff.standstatsview.diagram

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standstatsview.extensions.getTextHeight
import com.antonpopoff.standstatsview.utils.PI
import com.antonpopoff.standstatsview.utils.toRadians
import kotlin.math.cos
import kotlin.math.sin

class StandStatsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val ratingPolygonAlpha = 64

    private val rect = RectF()
    private val rectF = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val statsTextPath = Path()
    private val ratingPolygonPath = Path()

    private val normalFont = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    private val diagramValues = DiagramValues()

    var statistics = StandRating.UNKNOWN
        set(value) {
            field = value
            invalidate()
        }

    var polylineColor = Color.MAGENTA
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        calculateBaseDiagramValues()

        drawBorderCircles(canvas)
        drawBorderNotches(canvas)
        drawStatsCircle(canvas)
        drawStats(canvas)
        drawStatsNames(canvas)
        drawStatsRatingsLetters(canvas)
        drawStatsPolyline(canvas)
    }

    private fun calculateBaseDiagramValues() {
        val availableWidth = (width - paddingLeft - paddingRight)
        val availableHeight = (height - paddingTop - paddingBottom)

        diagramValues.apply {
            centerX = availableWidth / 2f + paddingLeft
            centerY = availableHeight / 2f + paddingTop

            outerBorderRadius = minOf(availableWidth, availableHeight) / 2f
            innerBorderRadius = outerBorderRadius * innerBorderRadiusToOuterRatio

            outerBorderWidth = outerBorderRadius * outerBorderWidthToOuterRadiusRatio
            innerBorderWidth = outerBorderRadius * innerBorderWidthToOuterRadiusRatio

            borderNotchWidth = outerBorderRadius - innerBorderRadius
            borderNotchRadius = innerBorderRadius + borderNotchWidth / 2

            statsCircleRadius = outerBorderRadius * statsCircleRadiusToOuterRatio
            statsLinesWidth = outerBorderRadius * statsCircleWidthToOuterRadiusRatio
            statNameTextSize = (innerBorderRadius - statsCircleRadius) / 3
            statsNameCircleRadius = innerBorderRadius - statNameTextSize
            angleBetweenStats = 360f / Statistics.count

            spaceBetweenRatings = statsCircleRadius / (Rating.letterRatings.size + 1)
            ratingNotchLen = statsCircleRadius * statNotchLenToRatingCircleRadiusRatio
            ratingNotchLeft = centerX - ratingNotchLen / 2
            ratingNotchRight = ratingNotchLeft + ratingNotchLen
            ratingLetterCircleRadius = innerBorderRadius - statNameTextSize * 2
        }
    }

    private fun drawBorderCircles(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
        }

        diagramValues.apply {
            paint.strokeWidth = outerBorderWidth
            drawCircleInCenterWithRadius(canvas, outerBorderRadius)

            paint.strokeWidth = innerBorderWidth
            drawCircleInCenterWithRadius(canvas, innerBorderRadius)
        }
    }

    private fun drawBorderNotches(canvas: Canvas) {
        paint.strokeWidth = diagramValues.borderNotchWidth

        diagramValues.apply {
            rect.apply {
                left = centerX - borderNotchRadius
                top = centerY - borderNotchRadius
                right = centerX + borderNotchRadius
                bottom = centerY + borderNotchRadius
            }
        }

        drawBigBorderNotches(canvas)
        drawSmallBorderNotches(canvas)
    }

    private fun drawBigBorderNotches(canvas: Canvas) {
        diagramValues.apply {
            var startAngle = 270 - bigBorderNotchAngle / 2

            for (i in 0 until bigBorderNotchesCount) {
                canvas.drawArc(rect, startAngle, bigBorderNotchAngle, false, paint)
                startAngle += 180
            }
        }
    }

    private fun drawSmallBorderNotches(canvas: Canvas) {
        diagramValues.apply {
            val spaceBetweenNotches = (180 - bigBorderNotchAngle) / (smallBorderNotchesCount / 2 + 1)
            var startAngle = 270 + (bigBorderNotchAngle - smallBorderNotchAngle) / 2 + spaceBetweenNotches

            for (i in 0 until smallBorderNotchesCount) {
                if (i == smallBorderNotchesCount / 2) {
                    startAngle += bigBorderNotchAngle + spaceBetweenNotches
                }

                canvas.drawArc(rect, startAngle, smallBorderNotchAngle, false, paint)
                startAngle += spaceBetweenNotches
            }
        }
    }

    private fun drawStatsCircle(canvas: Canvas) {
        paint.strokeWidth = diagramValues.statsLinesWidth
        drawCircleInCenterWithRadius(canvas, diagramValues.statsCircleRadius)
    }

    private fun drawCircleInCenterWithRadius(canvas: Canvas, r: Float) {
        diagramValues.apply { canvas.drawCircle(centerX, centerY, r, paint) }
    }

    private fun drawStats(canvas: Canvas) {
        paint.strokeWidth = diagramValues.statsLinesWidth

        textPaint.apply {
            textSize = diagramValues.spaceBetweenRatings
            typeface = boldFont
        }

        diagramValues.apply {
            val statLineY = centerY - statsCircleRadius

            canvas.save()

            for (i in 0 until Statistics.count) {
                canvas.drawLine(centerX, centerY, centerX, statLineY, paint)
                drawRatingNotches(canvas, i)
                canvas.rotate(angleBetweenStats, centerX, centerY)
            }

            canvas.restore()
        }
    }

    private fun drawRatingNotches(canvas: Canvas, statIndex: Int) {
        diagramValues.apply {
            for (i in 0 until Rating.letterRatings.size) {
                val notchY = centerY - spaceBetweenRatings * (i + 1)

                canvas.drawLine(ratingNotchLeft, notchY, ratingNotchRight, notchY, paint)

                if (statIndex == 0) {
                    drawRatingLetter(canvas, notchY, i)
                }
            }
        }
    }

    private fun drawRatingLetter(canvas: Canvas, notchY: Float, ratingIndex: Int) {
        diagramValues.apply {
            val char = Rating.letterRatings[Rating.letterRatings.size - ratingIndex - 1].char
            val charX = ratingNotchRight + ratingNotchLen / 2
            val charY = notchY + statsLinesWidth / 2
            canvas.drawText(char, charX, charY, textPaint)
        }
    }

    private fun drawStatsNames(canvas: Canvas) {
        textPaint.apply {
            typeface = boldFont
            textSize = diagramValues.statNameTextSize
        }

        canvas.save()

        diagramValues.apply {
            for (i in 0 until Statistics.count) {
                val statName = Statistics.get(i).name
                val textWidth = textPaint.measureText(statName)
                val textHeight = textPaint.getTextHeight(statName, 0, statName.length, rectF)
                val textArcAngle = (textWidth * 180f) / (PI * statsNameCircleRadius)
                val sweepAngle = getStatNameSweepAngle(i, textArcAngle)
                val pathTextRadius = getStatNameArcRadius(i, textHeight)
                val startAngle = 270f - angleBetweenStats - sweepAngle / 2f

                rect.apply {
                    left = centerX - pathTextRadius
                    top = centerY - pathTextRadius
                    right = centerX + pathTextRadius
                    bottom = centerY + pathTextRadius
                }

                statsTextPath.apply {
                    rewind()
                    addArc(rect, startAngle, sweepAngle)
                }

                canvas.apply {
                    drawTextOnPath(statName, statsTextPath, 0f, 0f, textPaint)
                    rotate(angleBetweenStats, centerX, centerY)
                }
            }
        }

        canvas.restore()
    }

    private fun getStatNameSweepAngle(statIndex: Int, textArcAngle: Float): Float {
        return if (statIndex < Statistics.count / 2) {
            textArcAngle
        } else {
            -textArcAngle
        }
    }

    private fun getStatNameArcRadius(statIndex: Int, textHeight: Int): Float {
        return if (statIndex < Statistics.count / 2) {
            diagramValues.statsNameCircleRadius
        } else {
            diagramValues.statsNameCircleRadius + textHeight
        }
    }

    private fun drawStatsRatingsLetters(canvas: Canvas) {
        textPaint.apply {
            textSize = diagramValues.statNameTextSize
            typeface = normalFont
        }

        var angle = 270f - diagramValues.angleBetweenStats

        diagramValues.apply {
            for (rating in statistics.ratings) {
                val char = rating.char
                val radians = toRadians(angle)
                val charWidth = textPaint.measureText(char)
                val charHeight = textPaint.getTextHeight(char, 0, char.length, rectF)
                val charX = ratingLetterCircleRadius * cos(radians) + centerX - charWidth / 2
                val charY = ratingLetterCircleRadius * sin(radians) + centerY + charHeight / 2

                canvas.drawText(char, 0, char.length, charX, charY, textPaint)
                angle += angleBetweenStats
            }
        }
    }

    private fun drawStatsPolyline(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = polylineColor
            alpha = ratingPolygonAlpha
        }

        ratingPolygonPath.rewind()

        diagramValues.apply {
            var angle = 270 - angleBetweenStats

            for (i in 0 until Statistics.count) {
                val ratingRadius = spaceBetweenRatings * statistics.ratings[i].mark
                val radians = toRadians(angle)
                val x = ratingRadius * cos(radians) + centerX
                val y = ratingRadius * sin(radians) + centerY

                if (i == 0) {
                    ratingPolygonPath.moveTo(x, y)
                } else {
                    ratingPolygonPath.lineTo(x, y)
                }

                angle += angleBetweenStats
            }
        }

        ratingPolygonPath.close()

        canvas.drawPath(ratingPolygonPath, paint)
    }
}
