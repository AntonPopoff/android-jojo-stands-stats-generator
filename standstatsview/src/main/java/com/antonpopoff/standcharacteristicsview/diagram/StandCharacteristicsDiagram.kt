package com.antonpopoff.standcharacteristicsview.diagram

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standcharacteristicsview.extensions.getTextHeight
import com.antonpopoff.standcharacteristicsview.utils.PI
import com.antonpopoff.standcharacteristicsview.utils.toRadians
import kotlin.math.cos
import kotlin.math.sin

class StandCharacteristicsDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val ratingPolygonAlpha = 64

    private val rect = RectF()
    private val rectF = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val characteristicTextPath = Path()
    private val ratingPolygonPath = Path()

    private val normalFont = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    private val diagramValues = DiagramValues()

    var rating = StandRating.UNKNOWN
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
        drawCharacteristicsCircle(canvas)
        drawCharacteristics(canvas)
        drawCharacteristicsNames(canvas)
        drawRatingsLetters(canvas)
        drawRatingPolyline(canvas)
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

            characteristicsCircleRadius = outerBorderRadius * characteristicsCircleRadiusToOuterRatio
            characteristicsLinesWidth = outerBorderRadius * characteristicsCircleWidthToOuterRadiusRatio
            characteristicNameTextSize = (innerBorderRadius - characteristicsCircleRadius) / 3
            characteristicNameCircleRadius = innerBorderRadius - characteristicNameTextSize
            angleBetweenCharacteristics = 360f / Characteristics.count

            spaceBetweenRatings = characteristicsCircleRadius / (Rating.letterRatings.size + 1)
            ratingNotchLen = characteristicsCircleRadius * characteristicsNotchLenToRatingCircleRadiusRatio
            ratingNotchLeft = centerX - ratingNotchLen / 2
            ratingNotchRight = ratingNotchLeft + ratingNotchLen
            ratingLetterCircleRadius = innerBorderRadius - characteristicNameTextSize * 2
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

    private fun drawCharacteristicsCircle(canvas: Canvas) {
        paint.strokeWidth = diagramValues.characteristicsLinesWidth
        drawCircleInCenterWithRadius(canvas, diagramValues.characteristicsCircleRadius)
    }

    private fun drawCircleInCenterWithRadius(canvas: Canvas, r: Float) {
        diagramValues.apply { canvas.drawCircle(centerX, centerY, r, paint) }
    }

    private fun drawCharacteristics(canvas: Canvas) {
        paint.strokeWidth = diagramValues.characteristicsLinesWidth

        textPaint.apply {
            textSize = diagramValues.spaceBetweenRatings
            typeface = boldFont
        }

        diagramValues.apply {
            val y = centerY - characteristicsCircleRadius

            canvas.save()

            for (i in 0 until Characteristics.count) {
                canvas.drawLine(centerX, centerY, centerX, y, paint)
                drawRatingNotches(canvas, i)
                canvas.rotate(angleBetweenCharacteristics, centerX, centerY)
            }

            canvas.restore()
        }
    }

    private fun drawRatingNotches(canvas: Canvas, characteristicIndex: Int) {
        diagramValues.apply {
            for (i in 0 until Rating.letterRatings.size) {
                val notchY = centerY - spaceBetweenRatings * (i + 1)

                canvas.drawLine(ratingNotchLeft, notchY, ratingNotchRight, notchY, paint)

                if (characteristicIndex == 0) {
                    drawRatingLetter(canvas, notchY, i)
                }
            }
        }
    }

    private fun drawRatingLetter(canvas: Canvas, notchY: Float, ratingIndex: Int) {
        diagramValues.apply {
            val char = Rating.letterRatings[Rating.letterRatings.size - ratingIndex - 1].char
            val charX = ratingNotchRight + ratingNotchLen / 2
            val charY = notchY + characteristicsLinesWidth / 2
            canvas.drawText(char, charX, charY, textPaint)
        }
    }

    private fun drawCharacteristicsNames(canvas: Canvas) {
        textPaint.apply {
            typeface = boldFont
            textSize = diagramValues.characteristicNameTextSize
        }

        canvas.save()

        diagramValues.apply {
            for (i in 0 until Characteristics.count) {
                val name = Characteristics.get(i).name
                val textWidth = textPaint.measureText(name)
                val textHeight = textPaint.getTextHeight(name, 0, name.length, rectF)
                val textArcAngle = (textWidth * 180f) / (PI * characteristicNameCircleRadius)
                val sweepAngle = getSweepAngle(i, textArcAngle)
                val pathTextRadius = getNameArcRadius(i, textHeight)
                val startAngle = 270f - angleBetweenCharacteristics - sweepAngle / 2f

                rect.apply {
                    left = centerX - pathTextRadius
                    top = centerY - pathTextRadius
                    right = centerX + pathTextRadius
                    bottom = centerY + pathTextRadius
                }

                characteristicTextPath.apply {
                    rewind()
                    addArc(rect, startAngle, sweepAngle)
                }

                canvas.apply {
                    drawTextOnPath(name, characteristicTextPath, 0f, 0f, textPaint)
                    rotate(angleBetweenCharacteristics, centerX, centerY)
                }
            }
        }

        canvas.restore()
    }

    private fun getSweepAngle(characteristicIndex: Int, textArcAngle: Float): Float {
        return if (characteristicIndex < Characteristics.count / 2) {
            textArcAngle
        } else {
            -textArcAngle
        }
    }

    private fun getNameArcRadius(characteristicIndex: Int, textHeight: Int): Float {
        return if (characteristicIndex < Characteristics.count / 2) {
            diagramValues.characteristicNameCircleRadius
        } else {
            diagramValues.characteristicNameCircleRadius + textHeight
        }
    }

    private fun drawRatingsLetters(canvas: Canvas) {
        textPaint.apply {
            textSize = diagramValues.characteristicNameTextSize
            typeface = normalFont
        }

        var angle = 270f - diagramValues.angleBetweenCharacteristics

        diagramValues.apply {
            for (rating in rating.ratings) {
                val char = rating.char
                val radians = toRadians(angle)
                val charWidth = textPaint.measureText(char)
                val charHeight = textPaint.getTextHeight(char, 0, char.length, rectF)
                val charX = ratingLetterCircleRadius * cos(radians) + centerX - charWidth / 2
                val charY = ratingLetterCircleRadius * sin(radians) + centerY + charHeight / 2

                canvas.drawText(char, 0, char.length, charX, charY, textPaint)
                angle += angleBetweenCharacteristics
            }
        }
    }

    private fun drawRatingPolyline(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = polylineColor
            alpha = ratingPolygonAlpha
        }

        ratingPolygonPath.rewind()

        diagramValues.apply {
            var angle = 270 - angleBetweenCharacteristics

            for (i in 0 until Characteristics.count) {
                val ratingRadius = spaceBetweenRatings * rating.ratings[i].mark
                val radians = toRadians(angle)
                val x = ratingRadius * cos(radians) + centerX
                val y = ratingRadius * sin(radians) + centerY

                if (i == 0) {
                    ratingPolygonPath.moveTo(x, y)
                } else {
                    ratingPolygonPath.lineTo(x, y)
                }

                angle += angleBetweenCharacteristics
            }
        }

        ratingPolygonPath.close()

        canvas.drawPath(ratingPolygonPath, paint)
    }
}
