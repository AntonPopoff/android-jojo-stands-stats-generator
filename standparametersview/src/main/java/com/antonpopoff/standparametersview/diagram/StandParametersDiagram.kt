package com.antonpopoff.standparametersview.diagram

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.antonpopoff.standparametersview.extensions.getTextHeight
import com.antonpopoff.standparametersview.extensions.lineToIfNotEmpty
import com.antonpopoff.standparametersview.utils.*

private const val PARAMETERS_ANIMATION_DURATION = 1000L
private const val POLYLINE_COLOR_ANIMATION_DURATION = 750L

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

    private var polylineOffsets = FloatArray(ParameterName.count)
    private val polylineStartOffsets = FloatArray(polylineOffsets.size)
    private val polylineEndOffsets = FloatArray(polylineOffsets.size)
    private val polylineAnimator = createPolylineOffsetAnimators()
    private val polylineColorAnimator = createPolylineColorAnimator()

    var standParameters = StandParameters.UNKNOWN
        private set

    var polylineColor = 0
        private set

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private fun createPolylineOffsetAnimators() = ValueAnimator.ofObject(
            FloatArrayEvaluator(polylineOffsets),
            polylineStartOffsets,
            polylineEndOffsets
    ).also {
        it.addUpdateListener(this::onPolylineOffsetAnimatorUpdate)
        it.duration = PARAMETERS_ANIMATION_DURATION
    }

    private fun onPolylineOffsetAnimatorUpdate(animator: ValueAnimator) {
        polylineOffsets = animator.animatedValue as FloatArray
        postInvalidateOnAnimation()
    }

    private fun createPolylineColorAnimator() = ObjectAnimator.ofInt(0, 0).also {
        it.addUpdateListener(this::onPolylineColorAnimatorUpdate)
        it.setEvaluator(ArgbEvaluator())
        it.interpolator = LinearInterpolator()
        it.duration = POLYLINE_COLOR_ANIMATION_DURATION
    }

    private fun onPolylineColorAnimatorUpdate(animator: ValueAnimator) {
        polylineColor = animator.animatedValue as Int
        postInvalidateOnAnimation()
    }

    fun setParameters(parameters: StandParameters, animated: Boolean = false) {
        standParameters = parameters

        if (animated) {
            updatePolylineAnimated()
        } else {
            updatePolyline()
        }
    }

    private fun updatePolylineAnimated() {
        polylineOffsets.copyInto(polylineStartOffsets)
        standParameters.ratings.forEachIndexed { i, r -> polylineEndOffsets[i] = r.mark.toFloat() }

        polylineAnimator.apply {
            cancel()
            start()
        }
    }

    private fun updatePolyline() {
        standParameters.ratings.forEachIndexed { i, r -> polylineOffsets[i] = r.mark.toFloat() }
        invalidate()
    }

    fun setPolylineColor(color: Int, animated: Boolean = false) {
        if (animated) {
            updatePolylineColorAnimated(color)
        } else {
            updatePolylineColor(color)
        }
    }

    private fun updatePolylineColorAnimated(color: Int) {
        polylineColorAnimator.apply {
            cancel()
            setIntValues(polylineColor, color)
            start()
        }
    }

    private fun updatePolylineColor(color: Int) {
        polylineColor = color
        invalidate()
    }

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
                val x = xOnCircle(angle, parametersCircleRadius, centerX)
                val y = yOnCircle(angle, parametersCircleRadius, centerY)
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
                val charX = xOnCircle(radians, ratingLetterCircleRadius, centerX - charWidth / 2)
                val charY = yOnCircle(radians, ratingLetterCircleRadius, centerY + charHeight / 2)
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
                val radius = spaceBetweenRatings * polylineOffsets[i]
                val radians = toRadians(startAngle + i * angleBetweenParameters)
                val x = xOnCircle(radians, radius, centerX)
                val y = yOnCircle(radians, radius, centerY)
                ratingPolygonPath.lineToIfNotEmpty(x, y)
            }

            ratingPolygonPath.close()
        }

        canvas.drawPath(ratingPolygonPath, paint)
    }
}
