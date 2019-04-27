package com.antonpopoff.standstatsgenerator.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.antonpopoff.standstatsgenerator.extensions.getTextHeight
import com.antonpopoff.standstatsview.diagram.Rating
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class StandStatsRatingBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val viewConfiguration = ViewConfiguration.get(context)

    private val selectedBarColor = Color.parseColor("#EA7371")
    private val unselectedBarColor = Color.parseColor("#F1B8B8")

    private val barHeight = context.resources.displayMetrics.density * 3
    private val ratingTextSize = context.resources.displayMetrics.density * 22
    private val textOffset = barHeight * 1.5f
    private val notchesRadius = barHeight * 2.5f / 2
    private val thumbRadius = barHeight * 4.5f / 2
    private var distanceBetweenNotches = 0f

    private var maxTextHeight = 0
    private var sidesOffset = 0f

    private val totalRatingRect = RectF()
    private val actualRatingRect = RectF()
    private val textRect = Rect()

    private var thumbX = 0f
    private var downX = 0f

    private val defaultTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    private val thumbXAnimator = ValueAnimator().apply {
        addUpdateListener(ThumbXAnimatorUpdateListener())
        duration = 200
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = ratingTextSize
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        calculateMaxRatingCharacterHeight()

        val ratingBarTotalHeight = maxOf(thumbRadius * 2, notchesRadius * 2, barHeight)
        val preferredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val preferredHeight = (ratingBarTotalHeight + textOffset + maxTextHeight).roundToInt()

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    private fun calculateMaxRatingCharacterHeight() {
        maxTextHeight = Rating.ratings.fold(0) { acc, rating ->
            max(acc, textPaint.getTextHeight(rating.letter, textRect))
        }
    }

    override fun onDraw(canvas: Canvas) {
        calcSidesOffset()
        calcTotalRatingBarRect()
        calcDistanceBetweenNotches()
        ensureThumbWithinTotalRatingRect()
        calcActualRatingBarRect()

        drawRect(canvas, totalRatingRect, unselectedBarColor)
        drawRect(canvas, actualRatingRect, selectedBarColor)
        drawNotches(canvas)
        drawThumb(canvas)
        drawRatingText(canvas)
    }

    private fun calcSidesOffset() {
        val firstRatingCharacterWidth = textPaint.measureText(Rating.ratings.first().letter) / 2
        val lastRatingCharacterWidth = textPaint.measureText(Rating.ratings.last().letter) / 2
        val maxCharacterWidth = maxOf(firstRatingCharacterWidth, lastRatingCharacterWidth)
        sidesOffset = maxOf(thumbRadius, notchesRadius, maxCharacterWidth)
    }

    private fun calcTotalRatingBarRect() {
        val availableHeight = height - paddingTop - paddingBottom - maxTextHeight - textOffset

        totalRatingRect.apply {
            left = paddingLeft + sidesOffset
            right = width - paddingRight - sidesOffset
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

    private fun drawRatingText(canvas: Canvas) {
        val textTop = totalRatingRect.centerY() + thumbRadius + textOffset

        for (i in 0 until Rating.ratingsCount) {
            textPaint.apply {
                color = getTextColor(i)
                typeface = getTextTypeface(i)
            }

            val char = Rating.ratings[i].letter
            val charWidth = textPaint.measureText(char)
            val charHeight = textPaint.getTextHeight(char, textRect)
            val textY = textTop + (maxTextHeight + charHeight) / 2
            val textX = totalRatingRect.left + distanceBetweenNotches * i - charWidth / 2

            canvas.drawText(char, textX, textY, textPaint)
        }
    }

    private fun getTextColor(textIndex: Int) = if (textIndex == getDestinationRatingIndex()) {
        selectedBarColor
    } else {
        unselectedBarColor
    }

    private fun getTextTypeface(textIndex: Int) = if (textIndex == getDestinationRatingIndex()) {
        boldTypeface
    } else {
        defaultTypeface
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
        val eventDuration = event.eventTime - event.downTime
        val eventTravelDistance = abs(event.x - downX)
        return eventDuration < ViewConfiguration.getTapTimeout() && eventTravelDistance < viewConfiguration.scaledTouchSlop
    }

    private fun animateThumbToFinalPosition() {
        val ratingIndex = getDestinationRatingIndex()
        val destination = ratingIndex * distanceBetweenNotches + totalRatingRect.left

        thumbXAnimator.apply {
            setFloatValues(thumbX, destination)
            start()
        }
    }

    private fun getDestinationRatingIndex() = ((thumbX - sidesOffset - paddingLeft) / distanceBetweenNotches).roundToInt()

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
