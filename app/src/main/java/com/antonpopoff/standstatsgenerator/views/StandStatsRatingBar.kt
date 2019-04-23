package com.antonpopoff.standstatsgenerator.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.antonpopoff.standstatsview.diagram.Rating
import kotlin.math.roundToInt

class StandStatsRatingBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val selectedBarColor = Color.parseColor("#EA7371")
    private val unselectedBarColor = Color.parseColor("#F1B8B8")
    private val barHeight = context.resources.displayMetrics.density * 3
    private val notchesDiameter = barHeight * 2.5f
    private val thumbDiameter = barHeight * 4.5f
    private val notchesCount = Rating.values().size

    private val totalRatingRect = RectF()
    private val actualRatingRect = RectF()

    private var distanceBetweenNotches = 0f
    private var thumbX = 0f

    private val valueAnimator = ValueAnimator().apply {
        duration = 200

        addUpdateListener {
            thumbX = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val preferredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val preferredHeight = thumbDiameter.roundToInt()

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        calculateTotalRatingBarRect()
        ensureThumbPosition()
        calculateActualRatingBarRect()

        drawFullRatingBarRect(canvas)
        drawActualRatingBarRect(canvas)
        drawNotches(canvas)
        drawThumb(canvas)
    }

    private fun ensureThumbPosition() {
        thumbX = when (thumbX) {
            0f -> totalRatingRect.left
            else -> thumbX
        }
    }

    private fun calculateTotalRatingBarRect() {
        val availableHeight = height - paddingTop - paddingBottom

        totalRatingRect.apply {
            left = paddingLeft + thumbDiameter / 2
            right = width - paddingRight - thumbDiameter / 2
            top = paddingTop + (availableHeight - barHeight) / 2
            bottom = top + barHeight
        }
    }

    private fun drawFullRatingBarRect(canvas: Canvas) {
        paint.color = unselectedBarColor
        canvas.drawRect(totalRatingRect, paint)
    }

    private fun calculateActualRatingBarRect() {
        actualRatingRect.apply {
            set(totalRatingRect)
            right = thumbX
        }
    }

    private fun drawActualRatingBarRect(canvas: Canvas) {
        paint.color = selectedBarColor
        canvas.drawRect(actualRatingRect, paint)
    }

    private fun drawThumb(canvas: Canvas) {
        paint.color = selectedBarColor
        canvas.drawCircle(thumbX, totalRatingRect.centerY(), thumbDiameter / 2, paint)
    }

    private fun drawNotches(canvas: Canvas) {
        distanceBetweenNotches = totalRatingRect.width() / (notchesCount - 1)
        var cx = totalRatingRect.left

        for (i in 0 until notchesCount) {
            if (cx >= thumbX) {
                paint.color = unselectedBarColor
            } else {
                paint.color = selectedBarColor
            }
            canvas.drawCircle(cx, totalRatingRect.centerY(), notchesDiameter / 2, paint)
            cx += distanceBetweenNotches
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                valueAnimator.cancel()
                updateThumbX(event.x)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateThumbX(event.x)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                updateThumbX(event.x)
                val ratingIndex = ((thumbX - thumbDiameter / 2) / distanceBetweenNotches).roundToInt()
                val destination = ratingIndex * distanceBetweenNotches + thumbDiameter / 2
                valueAnimator.setFloatValues(thumbX, destination)
                valueAnimator.start()
            }
        }

        return super.onTouchEvent(event)
    }

    private fun updateThumbX(eventX: Float) {
        thumbX = when {
            eventX < totalRatingRect.left -> totalRatingRect.left
            eventX > totalRatingRect.right -> totalRatingRect.right
            else -> eventX
        }
    }
}
