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
import android.view.ViewConfiguration
import com.antonpopoff.standstatsview.diagram.Rating
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class StandStatsRatingBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val viewConfiguration = ViewConfiguration.get(context)

    private val selectedBarColor = Color.parseColor("#EA7371")
    private val unselectedBarColor = Color.parseColor("#F1B8B8")

    private val barHeight = context.resources.displayMetrics.density * 3
    private val notchesRadius = barHeight * 2.5f / 2
    private val thumbRadius = barHeight * 4.5f / 2

    private val totalRatingRect = RectF()
    private val actualRatingRect = RectF()
    private var distanceBetweenNotches = 0f

    private var thumbX = 0f
    private var downX = 0f

    private val thumbXAnimator = ValueAnimator().apply {
        addUpdateListener(ThumbXAnimatorUpdateListener())
        duration = 200
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val preferredHeight = (thumbRadius * 2).roundToInt()

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        calcTotalRatingBarRect()
        calcDistanceBetweenNotches()
        ensureThumbWithinTotalRatingRect()
        calcActualRatingBarRect()

        drawRect(canvas, totalRatingRect, unselectedBarColor)
        drawRect(canvas, actualRatingRect, selectedBarColor)
        drawNotches(canvas)
        drawThumb(canvas)
    }

    private fun calcTotalRatingBarRect() {
        val availableHeight = height - paddingTop - paddingBottom

        totalRatingRect.apply {
            left = paddingLeft + thumbRadius
            right = width - paddingRight - thumbRadius
            top = paddingTop + (availableHeight - barHeight) / 2
            bottom = top + barHeight
        }
    }

    private fun calcActualRatingBarRect() {
        actualRatingRect.apply {
            set(totalRatingRect)
            right = thumbX
        }
    }

    private fun calcDistanceBetweenNotches() {
        distanceBetweenNotches = totalRatingRect.width() / (Rating.ratingsCount - 1)
    }

    private fun ensureThumbWithinTotalRatingRect() {
        if (thumbX == 0f) {
            thumbX = totalRatingRect.left
        }
    }

    private fun drawRect(canvas: Canvas, rectF: RectF, color: Int) {
        paint.color = color
        canvas.drawRect(rectF, paint)
    }

    private fun drawThumb(canvas: Canvas) {
        paint.color = selectedBarColor
        canvas.drawCircle(thumbX, totalRatingRect.centerY(), thumbRadius, paint)
    }

    private fun drawNotches(canvas: Canvas) {
        for (i in 0 until Rating.ratingsCount) {
            val notchX = totalRatingRect.left + i * distanceBetweenNotches
            paint.color = getNotchColor(notchX)
            canvas.drawCircle(notchX, totalRatingRect.centerY(), notchesRadius, paint)
        }
    }

    private fun getNotchColor(notchX: Float) = if (notchX > thumbX) {
        unselectedBarColor
    } else {
        selectedBarColor
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                thumbXAnimator.cancel()
                updateThumbX(event.x)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateThumbX(event.x)
            }
            MotionEvent.ACTION_UP -> {
                updateThumbX(event.x)
                animateThumbToFinalPosition()
                if (isTap(event)) performClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                updateThumbX(event.x)
                animateThumbToFinalPosition()
            }
        }

        return false
    }

    override fun performClick() = super.performClick()

    private fun isTap(event: MotionEvent): Boolean {
        return (event.eventTime - event.downTime < ViewConfiguration.getTapTimeout()
                && (event.x - downX).absoluteValue < viewConfiguration.scaledTouchSlop)
    }

    private fun animateThumbToFinalPosition() {
        val ratingIndex = ((thumbX - thumbRadius) / distanceBetweenNotches).roundToInt()
        val destination = ratingIndex * distanceBetweenNotches + thumbRadius

        thumbXAnimator.apply {
            setFloatValues(thumbX, destination)
            start()
        }
    }

    private fun updateThumbX(newX: Float) {
        thumbX = when {
            newX < totalRatingRect.left -> totalRatingRect.left
            newX > totalRatingRect.right -> totalRatingRect.right
            else -> newX
        }

        invalidate()
    }

    private inner class ThumbXAnimatorUpdateListener : ValueAnimator.AnimatorUpdateListener {

        override fun onAnimationUpdate(animation: ValueAnimator) {
            thumbX = animation.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }
}
