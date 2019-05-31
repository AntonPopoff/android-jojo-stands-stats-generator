package com.antonpopoff.standparametersview.diagram

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standparametersview.extensions.getTextHeight
import com.antonpopoff.standparametersview.utils.PI
import com.antonpopoff.standparametersview.utils.toRadians
import kotlin.math.cos
import kotlin.math.sin

class StandParametersDiagram(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val rect = RectF()
    private val rectF = Rect()
    private val diagramValues = DiagramValues()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val parametersTextPath = Path()
    private val ratingPolygonPath = Path()

    private val normalFont = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    var standParameters = StandParameters.UNKNOWN
        set(value) {
            field = value
            invalidate()
        }

    var polylineColor = 0
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        diagramValues.calculate(width, height, paddingLeft, paddingTop, paddingBottom, paddingRight)

        drawBorderCircles(canvas)
        drawBorderNotches(canvas)
        drawParametersCircle(canvas)
        drawParameters(canvas)
        drawParametersNames(canvas)
        drawRatingsLetters(canvas)
        drawRatingPolyline(canvas)
    }

    private fun drawBorderCircles(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
        }

        with(diagramValues) {
            drawCircle(canvas, outerBorderRadius, outerBorderWidth)
            drawCircle(canvas, innerBorderRadius, innerBorderWidth)
        }
    }

    private fun drawBorderNotches(canvas: Canvas) {
        paint.strokeWidth = diagramValues.borderNotchWidth
        setupRectForBorderNotches()
        drawSmallBorderNotches(canvas)
        drawBigBorderNotches(canvas)
    }

    private fun setupRectForBorderNotches() {
        with(diagramValues) {
            rect.apply {
                left = centerX - borderNotchRadius
                top = centerY - borderNotchRadius
                right = centerX + borderNotchRadius
                bottom = centerY + borderNotchRadius
            }
        }
    }

    private fun drawBigBorderNotches(canvas: Canvas) {
        with(diagramValues) {
            val startAngle = 270 - bigBorderNotchAngle / 2

            for (i in 0 until bigBorderNotchesCount) {
                canvas.drawArc(rect, startAngle + i * 180, bigBorderNotchAngle, false, paint)
            }
        }
    }

    private fun drawSmallBorderNotches(canvas: Canvas) {
        with(diagramValues) {
            val angleBetweenNotches = (180 - bigBorderNotchAngle) / (smallBorderNotchesCount / 2 + 1)
            val startAngle = 270 + (bigBorderNotchAngle - smallBorderNotchAngle) / 2 + angleBetweenNotches

            for (i in 0 until smallBorderNotchesCount / 2) {
                canvas.drawArc(rect, startAngle + i * angleBetweenNotches, smallBorderNotchAngle, false, paint)
                canvas.drawArc(rect, startAngle + i * angleBetweenNotches + 180, smallBorderNotchAngle, false, paint)
            }
        }
    }

    private fun drawParametersCircle(canvas: Canvas) {
        with(diagramValues) { drawCircle(canvas, parametersCircleRadius, parametersLinesWidth) }
    }

    private fun drawCircle(canvas: Canvas, r: Float, strokeWidth: Float) {
        paint.strokeWidth = strokeWidth
        with(diagramValues) { canvas.drawCircle(centerX, centerY, r, paint) }
    }

    private fun drawParameters(canvas: Canvas) {
        paint.strokeWidth = diagramValues.parametersLinesWidth

        textPaint.apply {
            textSize = diagramValues.spaceBetweenRatings
            typeface = boldFont
        }

        diagramValues.apply {
            val y = centerY - parametersCircleRadius

            canvas.save()

            for (i in 0 until ParameterName.count) {
                canvas.drawLine(centerX, centerY, centerX, y, paint)
                drawRatingNotches(canvas, i)
                canvas.rotate(angleBetweenParameters, centerX, centerY)
            }

            canvas.restore()
        }
    }

    private fun drawRatingNotches(canvas: Canvas, parameterIndex: Int) {
        diagramValues.apply {
            for (i in 0 until ParameterRating.letterRatings.size) {
                val notchY = centerY - spaceBetweenRatings * (i + 1)

                canvas.drawLine(ratingNotchLeft, notchY, ratingNotchRight, notchY, paint)

                if (parameterIndex == 0) {
                    drawRatingLetter(canvas, notchY, i)
                }
            }
        }
    }

    private fun drawRatingLetter(canvas: Canvas, notchY: Float, ratingIndex: Int) {
        diagramValues.apply {
            val char = ParameterRating.letterRatings[ParameterRating.letterRatings.size - ratingIndex - 1].char
            val charX = ratingNotchRight + ratingNotchLen / 2
            val charY = notchY + parametersLinesWidth / 2
            canvas.drawText(char, charX, charY, textPaint)
        }
    }

    private fun drawParametersNames(canvas: Canvas) {
        textPaint.apply {
            typeface = boldFont
            textSize = diagramValues.parametersNameTextSize
        }

        canvas.save()

        diagramValues.apply {
            for (i in 0 until ParameterName.count) {
                val name = ParameterName.get(i).name
                val textWidth = textPaint.measureText(name)
                val textHeight = textPaint.getTextHeight(name, 0, name.length, rectF)
                val textArcAngle = (textWidth * 180f) / (PI * parametersNameCircleRadius)
                val sweepAngle = getSweepAngle(i, textArcAngle)
                val pathTextRadius = getNameArcRadius(i, textHeight)
                val startAngle = 270f - angleBetweenParameters - sweepAngle / 2f

                rect.apply {
                    left = centerX - pathTextRadius
                    top = centerY - pathTextRadius
                    right = centerX + pathTextRadius
                    bottom = centerY + pathTextRadius
                }

                parametersTextPath.apply {
                    rewind()
                    addArc(rect, startAngle, sweepAngle)
                }

                canvas.apply {
                    drawTextOnPath(name, parametersTextPath, 0f, 0f, textPaint)
                    rotate(angleBetweenParameters, centerX, centerY)
                }
            }
        }

        canvas.restore()
    }

    private fun getSweepAngle(parameterIndex: Int, textArcAngle: Float): Float {
        return if (parameterIndex < ParameterName.count / 2) {
            textArcAngle
        } else {
            -textArcAngle
        }
    }

    private fun getNameArcRadius(parameterIndex: Int, textHeight: Int): Float {
        return if (parameterIndex < ParameterName.count / 2) {
            diagramValues.parametersNameCircleRadius
        } else {
            diagramValues.parametersNameCircleRadius + textHeight
        }
    }

    private fun drawRatingsLetters(canvas: Canvas) {
        textPaint.apply {
            textSize = diagramValues.parametersNameTextSize
            typeface = normalFont
        }

        var angle = 270f - diagramValues.angleBetweenParameters

        diagramValues.apply {
            for (rating in standParameters.ratings) {
                val char = rating.char
                val radians = toRadians(angle)
                val charWidth = textPaint.measureText(char)
                val charHeight = textPaint.getTextHeight(char, 0, char.length, rectF)
                val charX = ratingLetterCircleRadius * cos(radians) + centerX - charWidth / 2
                val charY = ratingLetterCircleRadius * sin(radians) + centerY + charHeight / 2

                canvas.drawText(char, 0, char.length, charX, charY, textPaint)
                angle += angleBetweenParameters
            }
        }
    }

    private fun drawRatingPolyline(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = polylineColor
        }

        ratingPolygonPath.rewind()

        diagramValues.apply {
            var angle = 270 - angleBetweenParameters

            for (i in 0 until ParameterName.count) {
                val ratingRadius = spaceBetweenRatings * standParameters.ratings[i].mark
                val radians = toRadians(angle)
                val x = ratingRadius * cos(radians) + centerX
                val y = ratingRadius * sin(radians) + centerY

                if (i == 0) {
                    ratingPolygonPath.moveTo(x, y)
                } else {
                    ratingPolygonPath.lineTo(x, y)
                }

                angle += angleBetweenParameters
            }
        }

        ratingPolygonPath.close()

        canvas.drawPath(ratingPolygonPath, paint)
    }
}
