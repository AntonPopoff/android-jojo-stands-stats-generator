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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val parametersTextPath = Path()
    private val ratingPolygonPath = Path()

    private val normalFont = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldFont = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    private val diagramValues = DiagramValues()

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

        calculateBaseDiagramValues()

        drawBorderCircles(canvas)
        drawBorderNotches(canvas)
        drawParametersCircle(canvas)
        drawParameters(canvas)
        drawParametersNames(canvas)
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

            parametersCircleRadius = outerBorderRadius * parametersCircleRadiusToOuterRatio
            parametersLinesWidth = outerBorderRadius * parametersCircleWidthToOuterRadiusRatio
            parametersNameTextSize = (innerBorderRadius - parametersCircleRadius) / 3
            parametersNameCircleRadius = innerBorderRadius - parametersNameTextSize
            angleBetweenParameters = 360f / ParameterName.count

            spaceBetweenRatings = parametersCircleRadius / (ParameterRating.letterRatings.size + 1)
            ratingNotchLen = parametersCircleRadius * parametersNotchLenToRatingCircleRadiusRatio
            ratingNotchLeft = centerX - ratingNotchLen / 2
            ratingNotchRight = ratingNotchLeft + ratingNotchLen
            ratingLetterCircleRadius = innerBorderRadius - parametersNameTextSize * 2
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

    private fun drawParametersCircle(canvas: Canvas) {
        paint.strokeWidth = diagramValues.parametersLinesWidth
        drawCircleInCenterWithRadius(canvas, diagramValues.parametersCircleRadius)
    }

    private fun drawCircleInCenterWithRadius(canvas: Canvas, r: Float) {
        diagramValues.apply { canvas.drawCircle(centerX, centerY, r, paint) }
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
            for (parameter in standParameters.parameters) {
                val char = parameter.rating.char
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
                val ratingRadius = spaceBetweenRatings * standParameters.parameters[i].rating.mark
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
