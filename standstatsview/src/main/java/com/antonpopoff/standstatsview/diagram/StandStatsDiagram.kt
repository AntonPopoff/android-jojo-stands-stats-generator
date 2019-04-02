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
        paint.style = Paint.Style.STROKE

        diagramValues.apply {
            paint.strokeWidth = outerBorderWidth
            drawCircleInCenterWithRadius(canvas, outerBorderRadius)

            paint.strokeWidth = innerBorderWidth
            drawCircleInCenterWithRadius(canvas, innerBorderRadius)
        }
    }

    private fun drawBorderNotches(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = diagramValues.borderNotchWidth
        }

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
            var startAngle = 270 - bigNotchAngle / 2
            val deltaAngle = 180

            for (i in 0 until bigNotchesCount) {
                canvas.drawArc(rect, startAngle, bigNotchAngle, false, paint)
                startAngle += deltaAngle
            }
        }
    }

    private fun drawSmallBorderNotches(canvas: Canvas) {
        diagramValues.apply {
            val spaceBetweenNotches = (180 - bigNotchAngle) / (smallNotchesCount / 2 + 1)
            var startAngle = 270 + (bigNotchAngle - smallNotchAngle) / 2 + spaceBetweenNotches

            for (i in 0 until smallNotchesCount) {
                if (i == smallNotchesCount / 2) {
                    startAngle += bigNotchAngle + spaceBetweenNotches
                }

                canvas.drawArc(rect, startAngle, smallNotchAngle, false, paint)
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
            val letter = Rating.letterRatings[Rating.letterRatings.size - ratingIndex - 1].letter
            val ratingLetterX = ratingNotchRight + ratingNotchLen / 2
            val ratingLetterY = notchY + statsLinesWidth / 2
            canvas.drawText(letter, ratingLetterX, ratingLetterY, textPaint)
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
                val letter = rating.letter
                val radians = toRadians(angle)
                val textWidth = textPaint.measureText(letter)
                val textHeight = textPaint.getTextHeight(letter, 0, letter.length, rectF)
                val letterX = ratingLetterCircleRadius * cos(radians) + centerX - textWidth / 2
                val letterY = ratingLetterCircleRadius * sin(radians) + centerY + textHeight / 2

                canvas.drawText(letter, 0, letter.length, letterX, letterY, textPaint)
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
