package com.antonpopoff.standparametersview.diagram

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standparametersview.extensions.getTextHeight
import com.antonpopoff.standparametersview.extensions.lineToIfNotEmpty
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
        drawParametersLines(canvas)
        drawRatingNotches(canvas)
        drawRatingLetters(canvas)
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
            rect.set(
                    centerX - borderNotchRadius,
                    centerY - borderNotchRadius,
                    centerX + borderNotchRadius,
                    centerY + borderNotchRadius
            )
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
                canvas.apply {
                    drawArc(rect, startAngle + i * angleBetweenNotches, smallBorderNotchAngle, false, paint)
                    drawArc(rect, startAngle + i * angleBetweenNotches + 180, smallBorderNotchAngle, false, paint)
                }
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

    private fun drawParametersLines(canvas: Canvas) {
        with(diagramValues) {
            paint.strokeWidth = parametersLinesWidth

            for (i in 0 until ParameterName.count) {
                val angle = toRadians(angleBetweenParameters * i - 90)
                val x = parametersCircleRadius * cos(angle) + centerX
                val y = parametersCircleRadius * sin(angle) + centerY
                canvas.drawLine(centerX, centerY, x, y, paint)
            }
        }
    }

    private fun drawRatingNotches(canvas: Canvas) {
        for (i in 0 until ParameterName.count) {
            for (j in 0 until ParameterRating.letterRatings.size) {
                with(diagramValues) {
                    val y = centerY - spaceBetweenRatings * (j + 1)
                    canvas.drawLine(ratingNotchLeft, y, ratingNotchRight, y, paint)
                    canvas.rotate(angleBetweenParameters, centerX, centerY)
                }
            }
        }
    }

    private fun drawRatingLetters(canvas: Canvas) {
        textPaint.apply {
            textSize = diagramValues.spaceBetweenRatings
            typeface = boldFont
        }

        for (i in 0 until ParameterRating.letterRatings.size) {
            with(diagramValues) {
                val y = centerY - spaceBetweenRatings * (i + 1)
                val char = ParameterRating.letterRatings[ParameterRating.letterRatings.size - i - 1].char
                val charX = ratingNotchRight + ratingNotchLen / 2
                val charY = y + parametersLinesWidth / 2
                canvas.drawText(char, charX, charY, textPaint)
            }
        }
    }

    private fun drawParametersNames(canvas: Canvas) {
        textPaint.apply {
            typeface = boldFont
            textSize = diagramValues.parametersNameTextSize
        }

        with(diagramValues) {
            for (i in 0 until ParameterName.count) {
                val name = ParameterName.get(i).name
                val textWidth = textPaint.measureText(name)
                val textHeight = textPaint.getTextHeight(name, 0, name.length, rectF)
                val textArcAngle = (textWidth * 180f) / (PI * parametersNameCircleRadius)
                val pathTextRadius = getNameArcRadius(i, textHeight)
                val sweepAngle = getSweepAngle(i, textArcAngle)
                val startAngle = 270f - angleBetweenParameters - sweepAngle / 2f

                rect.set(
                        centerX - pathTextRadius,
                        centerY - pathTextRadius,
                        centerX + pathTextRadius,
                        centerY + pathTextRadius
                )

                parametersTextPath.apply {
                    parametersTextPath.rewind()
                    parametersTextPath.addArc(rect, startAngle, sweepAngle)
                }

                canvas.apply {
                    canvas.drawTextOnPath(name, parametersTextPath, 0f, 0f, textPaint)
                    canvas.rotate(angleBetweenParameters, centerX, centerY)
                }
            }
        }
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

        with(diagramValues) {
            val startAngle = 270f - angleBetweenParameters

            for (i in 0 until ParameterName.count) {
                val char = standParameters.ratings[i].char
                val radians = toRadians(startAngle + angleBetweenParameters * i)
                val charWidth = textPaint.measureText(char)
                val charHeight = textPaint.getTextHeight(char, 0, char.length, rectF)
                val charX = ratingLetterCircleRadius * cos(radians) + centerX - charWidth / 2
                val charY = ratingLetterCircleRadius * sin(radians) + centerY + charHeight / 2
                canvas.drawText(char, 0, char.length, charX, charY, textPaint)
            }
        }
    }

    private fun drawRatingPolyline(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = polylineColor
        }

        with(diagramValues) {
            val startAngle = 270 - angleBetweenParameters

            ratingPolygonPath.rewind()

            for (i in 0 until ParameterName.count) {
                val ratingRadius = spaceBetweenRatings * standParameters.ratings[i].mark
                val radians = toRadians(startAngle + i * angleBetweenParameters)
                val x = ratingRadius * cos(radians) + centerX
                val y = ratingRadius * sin(radians) + centerY
                ratingPolygonPath.lineToIfNotEmpty(x, y)
            }

            ratingPolygonPath.close()
        }

        canvas.drawPath(ratingPolygonPath, paint)
    }
}
